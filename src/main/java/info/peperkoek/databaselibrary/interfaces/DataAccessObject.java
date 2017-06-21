package info.peperkoek.databaselibrary.interfaces;

import info.peperkoek.databaselibrary.core.Query;
import info.peperkoek.databaselibrary.exceptions.DatabaseException;
import java.util.Collection;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public interface DataAccessObject {
    
    public <T> T getObject(T item) throws DatabaseException;
    
    public <T> T getObject(Query query) throws DatabaseException;
    
    public <T> Collection<T> getObjects() throws DatabaseException;
    
    public <T> Collection<T> getObjects(Query query) throws DatabaseException;
    
    public <T> boolean insertObject(T obj) throws DatabaseException;
    
    public <T> boolean insertObjects(T[] obj) throws DatabaseException;
    
    public <T> boolean insertObjects(Collection<T> obj) throws DatabaseException;
    
    public <T> boolean updateObject(T obj) throws DatabaseException;
    
    public <T> boolean updateObjects(T[] obj) throws DatabaseException;
    
    public <T> boolean updateObjects(Collection<T> obj) throws DatabaseException;
    
    public <T> int removeObject(T obj) throws DatabaseException;
    
    public <T> int removeObjects(T[] obj) throws DatabaseException;
    
    public <T> int removeObjects(Collection<T> obj) throws DatabaseException;
}