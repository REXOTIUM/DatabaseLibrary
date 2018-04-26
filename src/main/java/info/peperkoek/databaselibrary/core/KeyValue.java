
package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.interfaces.DatabaseObject;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + this.key;
        hash = 31 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KeyValue other = (KeyValue) obj;
        if (this.key != other.key) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }
}