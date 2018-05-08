package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LinkTable {
    /**
     * Tablename for the linktable
     * @return 
     */
    String tableName();
    
    /**
     * The class of the list
     * @return 
     */
    Class clazz();
}