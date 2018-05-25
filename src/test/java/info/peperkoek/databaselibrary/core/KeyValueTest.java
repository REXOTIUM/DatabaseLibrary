package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.utils.KeyValue;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Rick Pijnenburg
 */
public class KeyValueTest {
    private static final String KEY = "Key";
    private static final String OBJECT = "Object";
    private static final KeyValue INSTANCE = new KeyValue(KEY, OBJECT);

    /**
     * Test of getKey method, of class KeyValue.
     */
    @Test
    public void testGetKey() {
        String result = INSTANCE.getKey();
        assertEquals(KEY, result);
    }

    /**
     * Test of getValue method, of class KeyValue.
     */
    @Test
    public void testGetValue() {
        String result = INSTANCE.getValue();
        assertEquals(OBJECT, result);
    }
}