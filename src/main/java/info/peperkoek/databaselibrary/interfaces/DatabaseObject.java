package info.peperkoek.databaselibrary.interfaces;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 * @param <T>
 */
public interface DatabaseObject<T> {
    
    public String toDatabaseString();
    
    public T toObject(String string);
}