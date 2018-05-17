package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 * Used to make use of link tables.
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LinkTable {
    /**
     * Table name for the link table
     * @return Table name
     */
    String tableName();
    
    /**
     * The class of the list
     * @return Class
     */
    Class clazz();
}