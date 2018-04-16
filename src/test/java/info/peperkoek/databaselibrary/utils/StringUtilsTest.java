
package info.peperkoek.databaselibrary.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class StringUtilsTest {
    
    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountNull() {
        int expResult = -1;
        int result = StringUtils.count("", null);
        assertEquals(expResult, result);
        result = StringUtils.count(null, "");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountZeroNoSequence() {
        String string = "test string";
        String sequence = "";
        int expResult = 0;
        int result = StringUtils.count(string, sequence);
        assertEquals(expResult, result);
    }
    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountZeroNoString() {
        String string = "";
        String sequence = "test";
        int expResult = 0;
        int result = StringUtils.count(string, sequence);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountZeroSingleSequence() {
        String string = "arm en hand";
        String sequence = "i";
        int expResult = 0;
        int result = StringUtils.count(string, sequence);
        assertEquals(expResult, result);
    }

    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountOneSingleSequence() {
        String string = "kinderen";
        String sequence = "i";
        int expResult = 1;
        int result = StringUtils.count(string, sequence);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountMultiSingleSequence() {
        String string = "kleinkinderen in de speeltuin";
        String sequence = "i";
        int expResult = 4;
        int result = StringUtils.count(string, sequence);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountZeroMultiSequence() {
        String string = "blutheid";
        String sequence = "uu";
        int expResult = 0;
        int result = StringUtils.count(string, sequence);
        assertEquals(expResult, result);
    }

    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountOneMultiSequence() {
        String string = "vuur";
        String sequence = "uu";
        int expResult = 1;
        int result = StringUtils.count(string, sequence);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of count method, of class StringUtils.
     */
    @Test
    public void testCountMultiMultiSequence() {
        String string = "aardbeibanaan";
        String sequence = "aa";
        int expResult = 2;
        int result = StringUtils.count(string, sequence);
        assertEquals(expResult, result);
    }
}