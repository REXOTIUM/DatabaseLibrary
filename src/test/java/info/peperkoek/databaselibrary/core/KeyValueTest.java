package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.interfaces.DatabaseObject;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Rick Pijnenburg
 */
public class KeyValueTest {
    private static final int KEY = 0;
    private static final DatabaseObject OBJECT = () -> "Object";
    private static final KeyValue INSTANCE = new KeyValue(KEY, OBJECT);

    /**
     * Test of getKey method, of class KeyValue.
     */
    @Test
    public void testGetKey() {
        int result = INSTANCE.getKey();
        assertEquals(KEY, result);
    }

    /**
     * Test of getValue method, of class KeyValue.
     */
    @Test
    public void testGetValue() {
        Object result = INSTANCE.getValue();
        assertEquals(OBJECT, result);
    }
}