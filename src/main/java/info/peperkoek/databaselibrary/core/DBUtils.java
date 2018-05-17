package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.annotations.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import info.peperkoek.databaselibrary.annotations.PrimaryKey;
import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
final class DBUtils {
    private static final Logger LOG = Logger.getLogger(DBUtils.class.getName());
    private static final String EMPTY = "";
    private static final String ID = "_id";
    private static final String IS = "is";
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String APPA = "\'";
    private static final String MISSING_PK = "No primary key found in class: ";
    
    private DBUtils() {
        throw new IllegalAccessError("Utility class");
    }
    
    /**
     * 
     * @param <T> The type of the class
     * @param clazz The class
     * @return String of all column names of the class seperated by a comma (,)
     */
    static <T> String getColumnString(Class<T> clazz) {
        StringBuilder output = new StringBuilder();
        for(Field f : getAllFields(clazz)) {
            output.append(getKeyName(f));
            output.append(", ");
        }
        output.delete(output.length() - 2, output.length()); //remove excess comma
        return output.toString();
    }
    
    /**
     * Check if primary key (pk) will be autogenerated in the database
     * @param <T> The type of the item
     * @param item Item for which to check if pk is autogenerated
     * @return true if pk is autogenerated
     * @see PrimaryKey
     * @see AutoGenerated
     */
    static <T> boolean IsPKAutoGenerated(T item) {
        for(Field f : getAllFields(item.getClass())) {
            if(f.isAnnotationPresent(PrimaryKey.class) && f.isAnnotationPresent(AutoGenerated.class))
                return true;
        }
        return false;
    }
    
    /**
     * Checks if a class has a link table item inside
     * 
     * @param clazz The class to be checked
     * @return true if this class has a field with annotation linktable
     * @see LinkTable
     */
    static boolean hasLinkTable(Class<?> clazz) {
        for(Field field : getAllFields(clazz)) {
            if(field.isAnnotationPresent(LinkTable.class))
                return true;
        }
        return false;
    }
    
    /**
     * Returns a key-value pair with the primary key of item.
     * @param <T> The type of the item
     * @param item The item to get the primary key from
     * @return A key-value pair of the primary key
     */
    static <T> KeyValue getPrimaryKey(T item) {
        KeyValue output = new KeyValue();
        Field[] fields = getAllFields(item.getClass());
        for (Field f : fields) {
            if (!f.isAnnotationPresent(PrimaryKey.class)) {
                continue;
            }
            String s = f.getName();
            if (f.isAnnotationPresent(Map.class)) {
                output.setKey(f.getAnnotation(Map.class).mapping());
            } else {
                output.setKey(s);
            }
            if (Modifier.isPublic(f.getModifiers())) {
                try {
                    output.setValue(objectToString(f.get(item), false));
                    return output;
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            s = ((f.getType() == boolean.class) ? IS : GET) + firstToUpper(s);
            try {
                Method m = item.getClass().getMethod(s, (Class<?>[]) null);
                output.setValue(objectToString(m.invoke(item, (Object[]) null), false));
                return output;
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        throw new DatabaseRuntimeException(MISSING_PK + item.getClass());
    }
    
    /**
     * 
     * @param <T> The type of the class
     * @param clazz Class for which to get the primary key name
     * @return Name of the primary key
     */
    static <T> String getPrimaryKeyName(Class<T> clazz) {
        Field[] fields = getAllFields(clazz);
        for (Field f : fields) {
            if (!f.isAnnotationPresent(PrimaryKey.class)) {
                continue;
            }
            return getKeyName(f);
        }
        throw new DatabaseRuntimeException(MISSING_PK + clazz);
    }
    
    /**
     * Returns string representation of the primary key value.
     * @param <T> The type of the item
     * @param item Item which contains the primary key
     * @return String representation of the primary key value
     */
    static <T> String getPrimaryKeyValue(T item) {
        Field[] fields = getAllFields(item.getClass());
        for (Field f : fields) {
            if (!f.isAnnotationPresent(PrimaryKey.class)) {
                continue;
            }
            if (Modifier.isPublic(f.getModifiers())) {
                try {
                    return objectToString(f.get(item), false);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            String s = ((f.getType() == boolean.class) ? IS : GET) + firstToUpper(f.getName());
            try {
                Method m = item.getClass().getMethod(s, (Class<?>[]) null);
                return objectToString(m.invoke(item, (Object[]) null), false);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        throw new DatabaseRuntimeException(MISSING_PK + item.getClass());
    }
    
    /**
     * Returns the tablename for a class.
     * @param <T> The type of the item
     * @param clazz The class for which the table name will be returned
     * @return The name of the table for this class
     */
    static <T> String getTableName(Class<T> clazz) {
        Entity a = clazz.getAnnotation(Entity.class);
        if (a == null) {
            return clazz.getSimpleName();
        }
        return a.tableName();
    }
    
    /**
     * Returns a list of keyvalues of all fields of item (ignoring the fields annotated with Ignore).
     * If ignorePK is true primary key(annotated with PrimaryKey) will be ignored too.
     * @param <T> The type of the item
     * @param item The item to get all fields from
     * @param ignorePK If the primary key should be ignored
     * @return List of keys and values which represent the fields of the items
     */
    static <T> List<KeyValue> getFields(T item, boolean ignorePK) {
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
     * Gets the linktable item from item
     * 
     * @param <T> The type of the item
     * @param item
     * @param field
     * @return 
     */
    static <T> Object getLinkItem(T item, Field field) {
        if (Modifier.isPublic(field.getModifiers())) {
            try{
                return field.get(item);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            String method = getMethodName(field);
            try {
                Method m = item.getClass().getMethod(method, (Class<?>[]) null);
                return m.invoke(item, (Object[]) null);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }
    
    /**
     * Returns the field of class clazz with name field.
     * 
     * Note: is the field uses annotation MapAs it will check if the mapping matches field too.
     * @param <T> The type of the class
     * @param clazz
     * @param field
     * @return 
     */
    static <T> Field getFieldFromString(Class<T> clazz, String field) {
        Field[] fields = getAllFields(clazz);
        for (Field f : fields) {
            if (f.getName().equals(field)) {
                return f;
            }
            if (f.isAnnotationPresent(Map.class) && f.getAnnotation(Map.class).mapping().equals(field)) {
                return f;
            }
            if(f.isAnnotationPresent(ForeignKey.class) && (getTableName(f.getType()) + ID).equals(field)) {
                return f;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param <T> The type of the item
     * @param item
     * @param value
     * @return 
     */
    static <T> boolean setPrimaryKey(T item, String value) {
        return setField(item, getPrimaryKeyField(item.getClass()), value);
    }
    
    /**
     * 
     * @param <T> The type of the item
     * @param item
     * @param field
     * @param value
     * @return 
     */
    static <T> boolean setField(T item, Field field, String value) {
        Class c = field.getType();
        String method = SET + firstToUpper(field.getName());
        if (Modifier.isPublic(field.getModifiers())) {
            try {
                field.set(item, stringToObject(c, value));
                return true;
            } catch (IllegalArgumentException | IllegalAccessException | ParseException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            try {
                Method m = getMethod(item, method, c);
                m.invoke(item, stringToObject(c, value));
                return true;
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ParseException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }
    
    /**
     * 
     * @param <T> The type of the item
     * @param <U> The type of value
     * @param item
     * @param field
     * @param value
     * @return 
     */
    static <T, U> boolean setField(T item, Field field, U value) {
        Class c = field.getType();
        String method = SET + firstToUpper(field.getName());
        if (Modifier.isPublic(field.getModifiers())) {
            try {
                field.set(item, value);
                return true;
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            try {
                Method m = getMethod(item, method, c);
                m.invoke(item, value);
                return true;
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }
    
    /**
     * 
     * @param <T> The type of the class
     * @param clazz
     * @return 
     */
    static <T> Field getPrimaryKeyField(Class<T> clazz) {
        Field[] fields = getAllFields(clazz);
        for (Field f : fields) {
            if (!f.isAnnotationPresent(PrimaryKey.class)) {
                continue;
            }
            return f;
        }
        throw new DatabaseRuntimeException(MISSING_PK + clazz);
    }
    
    /**
     * Gets all fields of a class.
     * @param <T> The type of the class
     * @param clazz
     * @return 
     */
    static <T> Field[] getAllFields(Class<T> clazz) {
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
     * Gets the database column name for field f.
     * @param f The field to get the name from
     * @return The name
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
     * @param <T> The type of the item
     * @param foreign if it is a foreign key or not
     * @param nullable if it is nullable or not
     * @param f The field
     * @param item The item to which the field belongs
     * @param key the name of the key
     * @return A key value pair of the field
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
     * @param f The field of the item (i.e. this is the foreign key field)
     * @param o The object (the value of the field) of which the primary key will be returned
     * @param nullable if the key is nullable or not
     * @return A key value pair for the foreign key
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
     * @param <T> The type of the item
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
                kv.setKey(key);
                kv.setValue(objectToString(m.invoke(item, (Object[]) null), nullable));
                return kv;
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Returns the string that is needed to insert the value in the database.
     * 
     * @param o
     * @param nullable
     * @return 
     */
    private static String objectToString(Object o, boolean nullable) {
        if (o == null) {
            return nullable ? "null" :EMPTY;
        }
        Class<?> c = o.getClass();
        if (o instanceof IDatabaseObject) {
            IDatabaseObject obj = (IDatabaseObject) o;
            return APPA + obj.toDatabaseString() + APPA;
        } else if (c == Boolean.class) {
            return (boolean) o ? "\'y\'" : "\'n\'";
        } else {
            return APPA + o.toString() + APPA;
        }
    }
    
    /**
     * Parses the string value to class c.
     * 
     * Works for both types and classes.
     * 
     * @param c
     * @param value
     * @return
     * @throws ParseException If value cannot be parsed
     */
    private static Object stringToObject(Class c, String value) throws ParseException {
        if (c == String.class) {
            return value;
        } else if (c == Character.class || c == char.class) {
            return value.charAt(0);
        } else if (c == Integer.class || c == int.class) {
            return Integer.parseInt(value);
        } else if (c == Double.class || c == double.class) {
            return Double.parseDouble(value);
        } else if  (c == Long.class || c == long.class) {
            return Long.parseLong(value);
        } else if (c == Boolean.class || c == boolean.class) {
            return "y".equals(value);
        } else if (IDatabaseObject.class.isAssignableFrom(c)) {
            try {
                IDatabaseObject obj = (IDatabaseObject) c.newInstance();
                return obj.toObject(value);
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                Constructor constructor = c.getConstructor(String.class);
                return constructor.newInstance(value);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        throw new ParseException("Class unknown: " + c.getName() + " Cannot parse value.", 0);
    }
    
    /**
     * 
     * @param <T> The type of the class
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
    
    /**
     * Returns a string with the first character to uppercase.
     * @param s String to be modified
     * @return String s with first character in uppercase
     */
    private static String firstToUpper(String s) {
        char c = s.charAt(0);
        c = Character.toUpperCase(c);
        return c + s.substring(1);
    }
    
    /**
     * Gets the name of the get method for field f.
     * @param f Field to get the method name for
     * @return Name of the getter method of the corresponding field
     */
    private static String getMethodName(Field f) {
        if (f.getType() == boolean.class || f.getType() == Boolean.class) {
            return IS + firstToUpper(f.getName());
        } else {
            return GET + firstToUpper(f.getName());
        }
    }
    
    /**
     * 
     * @param <T> The type of the item
     * @param item
     * @param method
     * @param c
     * @return
     * @throws NoSuchMethodException 
     */
    private static <T> Method getMethod(T item, String method, Class c) throws NoSuchMethodException {
        for(Method m : item.getClass().getMethods()) {
            if(m.isAnnotationPresent(MethodMapping.class)) {
                if(method.equals(m.getAnnotation(MethodMapping.class).mapping()) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == c)
                    return m;
            } else if(method.equals(m.getName()) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == c) {
                return m;
            }
        }
        throw new NoSuchMethodException();
    }
}