package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 * Map a field in a class to the corresponding database field name.
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Map {
    /**
     * Database name for this field
     * @return Mapping
     */
    String mapping();
}