package info.peperkoek.databaselibrary.utils;

import info.peperkoek.databaselibrary.annotations.PrimairyKey;
import info.peperkoek.databaselibrary.exceptions.DatabaseException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public final class DBUtils {
    
    private DBUtils() {
        throw new IllegalAccessError("Utility class");
    }
    
    public static String convertToString(Object object) throws DatabaseException {
        Field key = hasPrimairyKey(object);
        Class<?> type = key.getType();
    }
    
    private static Field hasPrimairyKey(Object object) throws DatabaseException {
        Field[] fields = object.getClass().getFields();
        if(fields.length == 0)
            throw new DatabaseException("Class does not contain fields.");
        Field key = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(PrimairyKey.class)) {
                key = field;
                break;
            }
        }
        if(key == null)
            throw new DatabaseException("Primairy key not found. Cannot convert class.");
        if(Modifier.isPublic(key.getModifiers()))
            throw new DatabaseException("Primairy key is not public. Cannot read primairy key.");
        return key;
    }
}