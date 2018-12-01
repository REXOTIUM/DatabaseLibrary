package info.peperkoek.databaselibrary.utils;

import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;
import info.peperkoek.databaselibrary.testutils.DataObject;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class QueryTest {
    private static final String PARAM = "param";
    private static final String QUERY_NONE = "This is a query";
    private static final String QUERY_START = Query.QUERY_PARAMETER_PLACEHOLDER + QUERY_NONE;
    private static final String QUERY_END = QUERY_NONE + Query.QUERY_PARAMETER_PLACEHOLDER;
    private static final String QUERY_START_END = Query.QUERY_PARAMETER_PLACEHOLDER + QUERY_NONE + Query.QUERY_PARAMETER_PLACEHOLDER;
    private static final String QUERY_MULTI = QUERY_NONE + Query.QUERY_PARAMETER_PLACEHOLDER + QUERY_NONE + Query.QUERY_PARAMETER_PLACEHOLDER + QUERY_NONE + Query.QUERY_PARAMETER_PLACEHOLDER + QUERY_NONE;
    private static final String QUERY_START_END_MULTI = Query.QUERY_PARAMETER_PLACEHOLDER + QUERY_NONE + Query.QUERY_PARAMETER_PLACEHOLDER + QUERY_NONE + Query.QUERY_PARAMETER_PLACEHOLDER;
    private static final DataObject DO_ONCE = new DataObject(PARAM);
    private static final DataObject DO_TWICE = new DataObject(PARAM + PARAM);
    
    @Test
    public void statementTest() {
        Query q1 = new Query(QUERY_NONE);
        assertEquals(QUERY_NONE, q1.getStatement());
    }
    
    @Test
    public void addElementTests() {
        Query q1 = new Query(QUERY_START);
        try {
            q1.addElement(-1, DO_ONCE);
            fail("Negative position");
        } catch(DatabaseRuntimeException ex) {
            assertEquals("Negative position message mismatch", "Element could not be added, position is smaller than 0.", ex.getMessage());
        }
        try {
            q1.addElement(1, DO_ONCE);
            fail("Overflow position");
        } catch(DatabaseRuntimeException ex) {
            assertEquals("Overflow position message mismatch", "Element could not be added, position is bigger than possible positions.", ex.getMessage());
        }
        q1.addElement(0, DO_ONCE);
        try {
            q1.addElement(0, DO_ONCE);
            fail("dual position");
        } catch(DatabaseRuntimeException ex) {
            assertEquals("Dual position message mismatch", "Element could not be added, position already taken in this query.", ex.getMessage());
        }
    }
    
    @Test
    public void getObjectTest() {
        Query q1 = new Query(QUERY_START);
        q1.addElement(0, DO_ONCE);
        int pos = -1;
        try {
            q1.getObject(pos);
        } catch (DatabaseRuntimeException ex) {
            String message = "Element for position " + pos + " could not be found.";
            assertEquals("Error message mismatch", message, ex.getMessage());
        }
        assertEquals("element mismatch", PARAM, q1.getObject(0));
    }
    
    @Test
    public void getQueryTest() {
        Query q1 = new Query(QUERY_NONE);
        String expected = QUERY_NONE;
        assertEquals("Query none mismatch", expected, q1.getQuery());
        
        q1 = new Query(QUERY_START);
        q1.addElement(0, DO_ONCE);
        expected = PARAM + QUERY_NONE;
        assertEquals("Query start mismatch", expected, q1.getQuery());
        
        q1 = new Query(QUERY_END);
        q1.addElement(0, DO_ONCE);
        expected = QUERY_NONE + PARAM;
        assertEquals("Query end mismatch", expected, q1.getQuery());
        
        q1 = new Query(QUERY_START_END);
        q1.addElement(0, DO_ONCE);
        q1.addElement(1, DO_ONCE);
        expected = PARAM + QUERY_NONE + PARAM;
        assertEquals("Query start/end mismatch", expected, q1.getQuery());
        
        q1 = new Query(QUERY_MULTI);
        q1.addElement(0, DO_ONCE);
        q1.addElement(1, DO_ONCE);
        q1.addElement(2, DO_ONCE);
        expected = QUERY_NONE + PARAM + QUERY_NONE + PARAM + QUERY_NONE + PARAM + QUERY_NONE;
        assertEquals("Query multi mismatch", expected, q1.getQuery());
        
        q1 = new Query(QUERY_START_END_MULTI);
        q1.addElement(0, DO_ONCE);
        q1.addElement(1, DO_ONCE);
        q1.addElement(2, DO_ONCE);
        expected = PARAM + QUERY_NONE + PARAM + QUERY_NONE + PARAM;
        assertEquals("Query start/end multi mismatch", expected, q1.getQuery());
    }
    
    @Test
    public void hashTest() {
        Query q1 = new Query(QUERY_NONE);
        assertEquals(935096361, q1.hashCode());
    }
    
    @Test
    @SuppressWarnings({"ObjectEqualsNull", "IncompatibleEquals"})
    public void equalityTests() {
        Query q1 = new Query(QUERY_NONE);
        Query q2 = new Query(QUERY_START);
        q2.addElement(0, DO_ONCE);
        Query q3 = new Query(QUERY_END);
        q3.addElement(0, DO_ONCE);
        Query q4 = new Query(QUERY_START);
        q4.addElement(0, DO_TWICE);
        Query q5 = new Query(QUERY_END);
        q5.addElement(0, DO_ONCE);
        assertTrue("Self equality", q1.equals(q1));
        assertFalse("Null equality", q1.equals(null));
        assertFalse("Class equality", q1.equals("test"));
        assertFalse("maxPlaces equality", q1.equals(q2));
        assertFalse("Statement equality", q2.equals(q3));
        assertFalse("Param equality", q2.equals(q4));
        assertTrue("Param equality", q3.equals(q5));
    }
}