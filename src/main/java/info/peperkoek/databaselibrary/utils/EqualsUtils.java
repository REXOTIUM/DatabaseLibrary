package info.peperkoek.databaselibrary.utils;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public class EqualsUtils {
    
    /**
     * 
     * @param self The object calling this function
     * @param obj The object which is the argument in the callings objects equals function
     * @return True if objects are the same. False if object obj is null of classes dont match. Null otherwise.
     */
    public static Boolean checkObject(Object self, Object obj) {
        if (self == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (self.getClass() != obj.getClass()) {
            return false;
        }
        return null;
    }
}