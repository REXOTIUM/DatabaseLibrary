package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 * To let the database managers know to ignore this field
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ignore {
}