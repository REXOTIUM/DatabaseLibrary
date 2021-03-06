package info.peperkoek.databaselibrary.core;

import java.util.Collection;
import java.util.logging.Logger;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
class MySQLDataAccessObject extends DataAccessObject {
    
    /**
     * 
     * @param connectionString The string to establish the connection to the database
     */
    MySQLDataAccessObject(String connectionString) {
        super(connectionString, Logger.getLogger(MySQLDataAccessObject.class.getName()));
    }

    @Override
    public <T> boolean insertObject(T obj) {
        return createInsertQuery(obj, INSERT_ITEM, INSERT_ITEM, false);
    }

    @Override
    public <T> boolean insertObjects(T[] obj) {
        for(T item : obj) {
            if(!insertObject(item))
                return false;
        }
        return true;
    }

    @Override
    public <T> boolean insertObjects(Collection<T> obj) {
        for(T item : obj) {
            if(!insertObject(item))
                return false;
        }
        return true;
    }
}