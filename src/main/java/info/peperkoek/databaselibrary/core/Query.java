package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;
import java.util.*;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class Query {
    private final String statement;
    private Collection<KeyValue> elements;
    
    /**
     * Constructs a query with a statement that is to be executed by the DAO
     * 
     * Note places to be filled in by other objects need to be marked with :-:
     * 
     * @param statement to be executed by the DAO
     */
    public Query(String statement) {
        this.statement = statement;
    }
    
    /**
     * Get the statement of this query
     * 
     * @return the statement in this query
     */
    public String getStatement() {
        return statement;
    }
    
    /**
     * Get the complete query of this query (statement with elements intergrated)
     * 
     * @return complete query
     */
    public String getQuery() {
        String[] parts = statement.split(":-:");
        if(parts.length <= elements.size())
            throw new DatabaseRuntimeException("Element mismatch. Amount of elements to be inserted too big. Unable to process query further.");
        if((parts.length - 1) != elements.size())
            throw new DatabaseRuntimeException("Element mismatch. Amount of elements to be inserted too small. Not enough elements to fill all gaps in the query.");
        StringBuilder sb = new StringBuilder();
        
        //TODO: fix that elements are placed in the query.
        return sb.toString();
    }
    
    /**
     * Adds an element to the query. Element will be placed on the available spot with number place
     * 
     * @param place place where the element needs to be inserted
     * @param element element to be placed
     */
    public void addElement(int place, Object element) {
        if(place < 0)
            return;
        this.elements.add(new KeyValue(place, element));
    }
}