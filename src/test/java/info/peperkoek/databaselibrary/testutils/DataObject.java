package info.peperkoek.databaselibrary.testutils;

import info.peperkoek.databaselibrary.interfaces.IDatabaseObject;
import java.util.Objects;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public class DataObject implements IDatabaseObject {
    private String test;

    public DataObject() {
    }

    public DataObject(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public String toDatabaseString() {
        return test;
    }

    @Override
    public Object toObject(String string) {
        DataObject o = new DataObject();
        o.test = string;
        return o;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.test);
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
        final DataObject other = (DataObject) obj;
        return Objects.equals(this.test, other.test);
    }
}