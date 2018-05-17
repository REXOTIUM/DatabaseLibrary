package info.peperkoek.databaselibrary.core;

/**
 * Interface for easier convertions to database values.
 * 
 * @author Rick Pijnenburg - REXOTIUM
 * @param <T> The type of the item
 */
public interface IDatabaseObject<T> {
    
    /**
     * 
     * @return String to use for the database
     */
    public String toDatabaseString();
    
    /**
     * 
     * @param string String to convert
     * @return The object that was represented by the string
     */
    public T toObject(String string);
}