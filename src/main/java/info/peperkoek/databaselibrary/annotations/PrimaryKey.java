package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 * Annotation to let the database manager know this is a primary key.
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
}