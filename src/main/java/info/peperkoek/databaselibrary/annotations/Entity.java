package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 * Annotation for the database manager to know the database table name corresponding with the entity
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Entity {
    /**
     * Table name for the entity
     * @return Table name
     */
    String tableName();
}