package info.peperkoek.databaselibrary.core;

import java.util.Objects;


/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class KeyValue {
    private String key;
    private String value;
    
    /**
     * 
     * @return 
     */
    public String getKey() {
        return key;
    }
    
    /**
     * 
     * @return 
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 
     * @param key 
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    /**
     * 
     * @param value 
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
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return "KeyValue{" + "key=" + key + ", value=" + value + '}';
    }
}