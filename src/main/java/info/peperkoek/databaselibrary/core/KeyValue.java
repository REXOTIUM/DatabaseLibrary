
package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.interfaces.DatabaseObject;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class KeyValue {
    private final int key;
    private final DatabaseObject value;
    
    /**
     * Constructs a key/value pair 
     * 
     * @param key integer to indicate the place of the value in the query
     * @param value value that is added in that place of the query
     */
    public KeyValue(int key, DatabaseObject value) {
        this.key = key;
        this.value = value;
    }
    
    /**
     * Get the value of the key of this key/value pair
     * 
     * @return key
     */
    public int getKey() {
        return key;
    }
    
    /**
     * Get the value of this key/value pair
     * 
     * @return value
     */
    public DatabaseObject getValue() {
        return value;
    }
}