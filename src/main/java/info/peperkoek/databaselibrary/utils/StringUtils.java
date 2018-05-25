package info.peperkoek.databaselibrary.utils;

import java.util.List;

/**
 * Utility class for string manipulations for the database managers.
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
public class StringUtils {
    private static final String EMPTY = "";
    
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
    
    /**
     * Returns a stringbuilder with the contents of the kvs list in it seperated by the seperators.
     * first seperator will be used to seperate key and value.
     * second seperator will be used to seperate keyvalue 1 and keyvalue 2.
     * 
     * @param kvs The list of KeyValues
     * @param firstSeperator The first seperator. Will be between the key and the value
     * @param secondSeperator The second seperator. Will be between 2 instances of KeyValue
     * @return A stringbuilder with the string in it
     * @see KeyValue
     */
    public static StringBuilder createString(List<KeyValue> kvs, String firstSeperator, String secondSeperator) {
        StringBuilder output = new StringBuilder();
        for (KeyValue kv : kvs) {
            if (kv.getValue() == null || EMPTY.equals(kv.getValue())) {
                continue;
            }
            output.append(kv.getKey());
            output.append(firstSeperator);
            output.append(kv.getValue());
            output.append(secondSeperator);
        }
        return output;
    }
}