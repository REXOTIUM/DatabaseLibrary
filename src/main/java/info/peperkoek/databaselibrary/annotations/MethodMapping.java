package info.peperkoek.databaselibrary.annotations;

import java.lang.annotation.*;

/**
 * Maps a method to another name. Can be used to make a getter or setter for a field.
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodMapping {
    /**
     * Name that can be used by database managers for this method
     * @return Mapping
     */
    String mapping();
}