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
    public static final String A = "A.";
    public static final String ID = "_id";
    public static final String EMPTY = "";
    public static final String EQUALS = " = ";
    public static final String COMMA = ", ";
    public static final String AND = " AND ";
    public static final String MISSING_PK = "Primary key is null. Is annotation PrimaryKey applied?";
    public static final String NO_VALID_CONSTRUCTOR = "Cannot find empty public constructor. Empty meaning no arguments.";
    public static final String SELECT_ALL = "SELECT %s FROM %s";
    public static final String SELECT_ID = "SELECT id FROM %s";
    public static final String SELECT_ID_WHERE = "SELECT id FROM %s WHERE %s";
    public static final String SELECT_WHERE = "SELECT %s FROM %s WHERE %s";
    public static final String SELECT_WHERE_ID = "SELECT %s FROM %s WHERE id = %s";
    public static final String SELECT_TOP = "SELECT TOP %s %s FROM %s";
    public static final String SELECT_TOP_ORDER = "SELECT TOP %s %s FROM %s ORDER BY %s";
    public static final String DELETE_ITEM = "DELETE FROM %s WHERE %s";
    public static final String INSERT_ITEM = "INSERT INTO %s (%s) VALUES (%s)";
    public static final String INSERT_ITEM_OUTPUT = "INSERT INTO %s (%s) OUTPUT INSERTED.%s VALUES (%s)";
    public static final String UPDATE_ITEM = "UPDATE %s SET %s WHERE %s";
    public static final String INSERT_LINK_TABLE = "INSERT INTO %s (%s, %s) VALUES (%s, %s)";
    public static final String SELECT_LINK_TABLE = "SELECT %s from %s A join %s B on A.%s = B.%s where %s";
    
    public <T> T getObject(T item) throws DatabaseException;
    
    public <T> T getObject(Class<T> clazz, Query query) throws DatabaseException;
    
    public <T> Collection<T> getObjects(Class<T> clazz) throws DatabaseException;
    
    public <T> Collection<T> getObjects(Class<T> clazz, Query query) throws DatabaseException;
    
    /**
     * Inserts object into database.
     * @param <T>
     * @param obj
     * @return True if item is inserted.
     * @throws info.peperkoek.databaselibrary.exceptions.DatabaseException
     */
    public <T> boolean insertObject(T obj) throws DatabaseException;
    
    public <T> boolean insertObjects(T[] obj) throws DatabaseException;
    
    public <T> boolean insertObjects(Collection<T> obj) throws DatabaseException;
    
    /**
     * Updates object in the database.
     * 
     * Note: make sure the object has a primary key annotation used.
     * @param <T>
     * @param obj
     * @return True if item is updated.
     * @throws info.peperkoek.databaselibrary.exceptions.DatabaseException
     */
    public <T> boolean updateObject(T obj) throws DatabaseException;
    
    public <T> boolean updateObjects(T[] obj) throws DatabaseException;
    
    public <T> boolean updateObjects(Collection<T> obj) throws DatabaseException;
    
    /**
     * Deletes object in the database.
     * 
     * Note: make sure the object has a primary key annotation used.
     * @param <T>
     * @param obj
     * @return True if item is deleted.
     * @throws info.peperkoek.databaselibrary.exceptions.DatabaseException
     */
    public <T> boolean removeObject(T obj) throws DatabaseException;
    
    public <T> boolean removeObjects(T[] obj) throws DatabaseException;
    
    public <T> boolean removeObjects(Collection<T> obj) throws DatabaseException;
}