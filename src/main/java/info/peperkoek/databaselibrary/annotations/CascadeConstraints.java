package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CascadeConstraints {
    /**
     * 
     * @return If this will cascade in the database. Default is false.
     */
    boolean cascade() default false;
}