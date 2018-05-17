package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;
import info.peperkoek.databaselibrary.utils.StringUtils;
import java.util.*;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public class Query {
    public static final String QUERY_PARAMETER_PLACEHOLDER = ":-:";
    private final String statement;
    private final int maxPlaces;
    private Collection<QueryKeyValue> elements;
    
    /**
     * Constructs a query with a statement that is to be executed by the DAO
     * 
     * Note places to be filled in by other objects need to be marked with :-:
     * 
     * @param statement to be executed by the DAO
     */
    public Query(String statement) {
        this.statement = statement;
        this.maxPlaces = StringUtils.count(statement, QUERY_PARAMETER_PLACEHOLDER);
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
        boolean startedWith = statement.startsWith(QUERY_PARAMETER_PLACEHOLDER);
        String[] parts = statement.split(QUERY_PARAMETER_PLACEHOLDER);
        if(parts.length <= elements.size())
            throw new DatabaseRuntimeException("Element mismatch. Amount of elements to be inserted too big. Unable to process query further.");
        if((parts.length - 1) != elements.size())
            throw new DatabaseRuntimeException("Element mismatch. Amount of elements to be inserted too small. Not enough elements to fill all gaps in the query.");
        StringBuilder sb = new StringBuilder();
        int startList = 0;
        if(startedWith) {
            sb.append(getObject(0));
            startList = 1;
        }
        for (String part : parts) {
            sb.append(part);
            sb.append(getObject(startList));
            startList++;
        }
        return sb.toString();
    }
    
    /**
     * Adds an element to the query. Element will be placed on the available spot with number place
     * 
     * @param place place where the element needs to be inserted
     * @param element element to be placed
     */
    public void addElement(int place, IDatabaseObject element) {
        if(place < 0)
            throw new DatabaseRuntimeException("Element could not be added, position is smaller than 0.");
        if(place >= maxPlaces)
            throw new DatabaseRuntimeException("Element could not be added, position is bigger than possible positions.");
        if(elements.stream().anyMatch(e -> e.getKey() == place))
            throw new DatabaseRuntimeException("Element could not be added, position already taken in this query.");
        this.elements.add(new QueryKeyValue(place, element));
    }
    
    /**
     * 
     * @param place The place of the object
     * @return String representation of the object on that place
     */
    public String getObject(int place) {
        return elements.stream().filter(e -> e.getKey() == place).findFirst().orElseThrow(() -> new DatabaseRuntimeException("Element for position " + place + " could not be found.")).getValue().toDatabaseString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.statement);
        hash = 53 * hash + this.maxPlaces;
        hash = 53 * hash + Objects.hashCode(this.elements);
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
        final Query other = (Query) obj;
        if (this.maxPlaces != other.maxPlaces) {
            return false;
        }
        if (!Objects.equals(this.statement, other.statement)) {
            return false;
        }
        return Objects.equals(this.elements, other.elements);
    }
    
    private class QueryKeyValue {
        private final int key;
        private final IDatabaseObject value;

        /**
         * Constructs a key/value pair 
         * 
         * @param key integer to indicate the place of the value in the query
         * @param value value that is added in that place of the query
         */
        public QueryKeyValue(int key, IDatabaseObject value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Get the value of the key of this key/value pair
         * 
         * @return key
         */
        public int getKey() {
            return key;
        }

        /**
         * Get the value of this key/value pair
         * 
         * @return value
         */
        public IDatabaseObject getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 31 * hash + this.key;
            hash = 31 * hash + Objects.hashCode(this.value);
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
            final QueryKeyValue other = (QueryKeyValue) obj;
            if (this.key != other.key) {
                return false;
            }
            return Objects.equals(this.value, other.value);
        }
    }
}