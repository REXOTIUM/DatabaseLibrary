package info.peperkoek.databaselibrary.utils;

import java.util.Objects;

/**
 * Used for the easier use of classes their fields in the database managers
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
public final class KeyValue {
    private static final String TO_STRING = "Key: {0}, Value{1}";
    private String key;
    private String value;
    
    /**
     * Default constructor
     */
    public KeyValue() {
    }
    
    /**
     * 
     * @param key The key
     * @param value The value
     */
    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    /**
     * 
     * @return The key
     */
    public String getKey() {
        return key;
    }
    
    /**
     * 
     * @return The value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 
     * @param key The new key
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    /**
     * 
     * @param value The new value
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.key);
        hash = 97 * hash + Objects.hashCode(this.value);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        Boolean b = EqualsUtils.checkObject(this, obj);
        if(b != null) {
            return b;
        }
        final KeyValue other = (KeyValue) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return String.format(TO_STRING, key, value);
    }
}