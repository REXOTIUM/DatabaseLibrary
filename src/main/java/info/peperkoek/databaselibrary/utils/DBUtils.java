package info.peperkoek.databaselibrary.utils;

import info.peperkoek.databaselibrary.annotations.*;
import info.peperkoek.databaselibrary.exceptions.DatabaseException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import info.peperkoek.databaselibrary.annotations.PrimaryKey;
import info.peperkoek.databaselibrary.core.KeyValue;
import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public final class DBUtils {
    private static final Logger LOG = Logger.getLogger(DBUtils.class.getName());
    public static final String ID = "_id";
    
    private DBUtils() {
        throw new IllegalAccessError("Utility class");
    }
    
    /**
     * Returns the tablename for a class.
     * @param <T>
     * @param item
     * @return 
     */
    public static <T> String getTableName(Class<T> item) {
        Entity a = item.getAnnotation(Entity.class);
        if (a == null) {
            return item.getSimpleName();
        }
        return a.tableName();
    }
    
    /**
     * Returns a list of keyvalues of all fields of item (ignoring the fields annotated with Ignore).
     * If ignorePK is true primary key(annotated with PrimaryKey) will be ignored too.
     * @param <T>
     * @param item
     * @param ignorePK
     * @return 
     */
    public static <T> List<KeyValue> getFields(T item, boolean ignorePK) {
        List<KeyValue> output = new ArrayList<>();
        Field[] fields = getAllFields(item.getClass());
        for (Field f : fields) {
            if (f.isAnnotationPresent(Ignore.class) || (f.isAnnotationPresent(PrimaryKey.class) && ignorePK) || f.isAnnotationPresent(LinkTable.class)) {
                continue;
            }
            boolean foreign = f.isAnnotationPresent(ForeignKey.class);
            boolean nullable = f.isAnnotationPresent(Nullable.class);
            String key = getKeyName(f);
            KeyValue kv;
            if (Modifier.isPublic(f.getModifiers())) {
                kv = getPublicField(foreign, nullable, f, item, key);
            } else {
                kv = getPublicMethod(foreign, nullable, f, item, key);
            }
            if (kv != null) {
                output.add(kv);
            }
        }
        return output;
    }
    
    /**
     * Gets the database column name for field f.
     * @param f
     * @return 
     */
    private static String getKeyName(Field f) {
        if (f.isAnnotationPresent(Map.class)) {
            return f.getAnnotation(Map.class).mapping();
        } else {
            return f.getName();
        }
    }
    
    /**
     * Constructs a KeyValue that has the database column name as key and the value that needs to be inserted as value.
     * This method is to get the value from the item via a (public) field.
     * 
     * @param <T>
     * @param foreign
     * @param nullable
     * @param f
     * @param item
     * @param key
     * @return 
     */
    private static <T> KeyValue getPublicField(boolean foreign, boolean nullable, Field f, T item, String key) {
        try {
            if(foreign) {
                Object o = f.get(item);
                return getForeignKey(f, o, nullable);
            } else {
                KeyValue kv = new KeyValue();
                kv.setKey(key);
                kv.setValue(objectToString(f.get(item), nullable));
                return kv;
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new DatabaseRuntimeException("Could not access field: " + f + " in class: " + item.getClass());
        }
    }
    
    /**
     * Gets the primary key of the object o and returns it as the foreignkey.
     * 
     * @param o
     * @return 
     */
    private static KeyValue getForeignKey(Field f, Object o, boolean nullable) {
        if(o == null && nullable) {
            KeyValue kv = new KeyValue();
            kv.setKey(getTableName(f.getType()) + ID);
            kv.setValue("null");
            return kv;
        } else if (o != null) {
            String name = getTableName(o.getClass()) + ID;
            KeyValue k = getPrimaryKey(o);
            k.setKey(name);
            return k;
        }
        return null;
    }
    
    /**
     * Constructs a KeyValue that has the database column name as key and the value that needs to be inserted as value.
     * This method is to get the value from the item via a (public) get method.
     * 
     * @param <T>
     * @param foreign
     * @param nullable
     * @param f
     * @param item
     * @param key
     * @return 
     */
    private static <T> KeyValue getPublicMethod(boolean foreign, boolean nullable, Field f, T item, String key) {
        String method = getMethodName(f);
        try {
            Method m = item.getClass().getMethod(method, (Class<?>[]) null);
            if(foreign) {
                Object o = m.invoke(item, (Object[]) null);
                return getForeignKey(f, o, nullable);
            } else {
                KeyValue kv = new KeyValue();
                kv.key = key;
                kv.value = objectToString(m.invoke(item, (Object[]) null), nullable);
                return kv;
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Gets all fields of a class.
     * @param <T>
     * @param clazz
     * @return 
     */
    private static <T> Field[] getAllFields(Class<T> clazz) {
        ArrayList<Field> fields = new ArrayList<>();
        getFieldsOfClass(clazz, fields, true);
        Class c = clazz.getSuperclass();
        while(c != null && c != Object.class) {
            getFieldsOfClass(c, fields, false);
            c = c.getSuperclass();
        }
        return fields.toArray(new Field[]{});
    }
    
    /**
     * 
     * @param <T>
     * @param clazz
     * @param fields
     * @param privateToo 
     */
    private static <T> void getFieldsOfClass(Class<T> clazz, ArrayList<Field> fields, boolean privateToo) {
        for(Field field : clazz.getDeclaredFields()) {
            int mod = field.getModifiers();
            if(!privateToo && !Modifier.isPublic(mod) && !Modifier.isProtected(mod))
                continue;
            fields.add(field);
        }
    }
}