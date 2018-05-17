package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 * Annotation to let the database manager know this is a foreign key.
 * 
 * Note: this will be a class with the @entity annotation.
 * @see Entity
 * @author Rick Pijnenburg - REXOTIUM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {
}