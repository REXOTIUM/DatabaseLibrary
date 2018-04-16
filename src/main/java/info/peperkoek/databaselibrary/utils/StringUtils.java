package info.peperkoek.databaselibrary.utils;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class StringUtils {
    
    private StringUtils() {
        throw new IllegalAccessError("Utility class");
    }
    
    /**
     * Counts the amount of times sequence is in the string
     * @param string To be searched for the sequence
     * @param sequence To be counted in the string 
     * @return the amount of times the sequence appears in the string or -1 if one of the arguments is null
     */
    public static int count(String string, String sequence) {
        if(string == null || sequence == null) {
            return -1;
        }
        int output = 0;
        int start = 0;
        if(string.length() == 0 || sequence.length() == 0) {
            return output;
        }
        while(start < string.length()) {
            if(string.startsWith(sequence, start)) {
                output++;
                start += sequence.length()  - 1;
            }
            start++;
        }
        return output;
    }
}