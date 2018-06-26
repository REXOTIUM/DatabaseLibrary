package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.interfaces.IDataAccessObject;
import info.peperkoek.databaselibrary.utils.KeyValue;
import info.peperkoek.databaselibrary.utils.Query;
import info.peperkoek.databaselibrary.annotations.ForeignKey;
import info.peperkoek.databaselibrary.annotations.LinkTable;
import info.peperkoek.databaselibrary.enums.LogLevel;
import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;
import info.peperkoek.databaselibrary.utils.StringUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Data Access Object. Use for accessing data in the database.
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
public abstract class DataAccessObject implements IDataAccessObject {
    protected static final String EMPTY = "";
    protected static final String ID = "_id";
    protected static final String EQUALS = " = ";
    protected static final String COMMA = ", ";
    protected static final String AND = " AND ";
    protected static final String MISSING_PK = "Primary key can not be found. Is annotation PrimaryKey applied?";
    protected static final String NO_VALID_CONSTRUCTOR = "Cannot find empty public constructor. Empty meaning no arguments.";
    protected static final String SELECT_ALL = "SELECT %s FROM %s";
    protected static final String SELECT_WHERE = "SELECT %s FROM %s WHERE %s";
    protected static final String SELECT_WHERE_PK = "SELECT %s FROM %s WHERE %s = %s";
    protected static final String SELECT_TOP_MSSQL = "SELECT TOP %s %s FROM %s";
    protected static final String SELECT_TOP_ORDER_MSSQL = "SELECT TOP %s %s FROM %s ORDER BY %s";
    protected static final String SELECT_TOP_MYSQL = "SELECT %s FROM %s LIMIT %s";
    protected static final String SELECT_TOP_ORDER_MYSQL = "SELECT %s FROM %s ORDER BY %s LIMIT %s";
    protected static final String SELECT_TOP_ORACLE = "SELECT %s FROM %s WHERE ROWNUM <= %s";
    protected static final String SELECT_TOP_ORDER_ORACLE = "SELECT %s FROM %s WHERE ROWNUM <= %s ORDER BY %s";
    protected static final String DELETE_ITEM = "DELETE FROM %s WHERE %s";
    protected static final String INSERT_ITEM = "INSERT INTO %s (%s) VALUES (%s)";
    protected static final String INSERT_ITEM_OUTPUT_MSSQL = "INSERT INTO %s (%s) OUTPUT INSERTED.%s VALUES (%s)";
    protected static final String UPDATE_ITEM = "UPDATE %s SET %s WHERE %s";
    protected static final String INSERT_LINK_TABLE = "INSERT INTO %s (%s, %s) VALUES (%s, %s)";
    protected static final String SELECT_LINK_TABLE = "SELECT %s from %s A join %s B on A.%s = B.%s where %s";
    protected final String connectionString;
    protected final Logger logger;
    protected LogLevel logLevel;
    
    /**
     * 
     * @param connectionString The string to establish the connection to the database
     * @param logger The logger to log messages in.
     */
    protected DataAccessObject(String connectionString, Logger logger) {
        this.connectionString = connectionString;
        this.logger = logger;
        this.logLevel = LogLevel.DEBUG;
    }
    
    @Override
    public <T> boolean doesObjectExist(T item) {
        String table = DBUtils.getTableName(item.getClass());
        List<KeyValue> kvs = DBUtils.getFields(item, false);
        String field = DBUtils.getPrimaryKeyName(item.getClass());
        StringBuilder set = StringUtils.createString(kvs, EQUALS, AND);
        if (set.length() > 5) {
            set.delete(set.length() - 5, set.length());
        }
        String sql;
        if (set.length() > 0) {
            sql = String.format(SELECT_WHERE, field, table, set.toString());
        } else {
            sql = String.format(SELECT_ALL, field, table);
        }
        return !query(sql).isEmpty();
    }
    
    @Override
    public <T, U> T getObject(Class<T> clazz, U item) {
        return getObjects(clazz, item).stream().findFirst().orElse(null);
    }

    @Override
    public <T> T getObject(Class<T> clazz, Query query) {
        return getObjects(clazz, query).stream().findFirst().orElse(null);
    }

    @Override
    public <T> Collection<T> getObjects(Class<T> clazz) {
        String table = DBUtils.getTableName(clazz);
        String sql = String.format(SELECT_ALL, DBUtils.getColumnString(clazz), table);
        return queryToObject(clazz, sql);
    }
    
    @Override
    public <T, U> Collection<T> getObjects(Class<T> clazz, U item) {
        String table = DBUtils.getTableName(clazz);
        List<KeyValue> kvs = DBUtils.getFields(item, false);
        StringBuilder set = StringUtils.createString(kvs, EQUALS, AND);
        if (set.length() > 5) {
            set.delete(set.length() - 5, set.length());
        }
        String sql;
        if (set.length() > 0) {
            sql = String.format(SELECT_WHERE, DBUtils.getColumnString(clazz), table, set.toString());
        } else {
            sql = String.format(SELECT_ALL, DBUtils.getColumnString(clazz), table);
        }
        return queryToObject(clazz, sql);
    }

    @Override
    public <T> Collection<T> getObjects(Class<T> clazz, Query query) {
        return queryToObject(clazz, query.getQuery());
    }
    
    @Override
    public <T> boolean updateObject(T obj) {
        String table = DBUtils.getTableName(obj.getClass());
        List<KeyValue> kvs = DBUtils.getFields(obj, true);
        KeyValue pk = DBUtils.getPrimaryKey(obj);
        if (pk == null) {
            throw new DatabaseRuntimeException(MISSING_PK);
        }
        StringBuilder set = StringUtils.createString(kvs, EQUALS, COMMA);
        set.delete(set.length() - 2, set.length());
        String sql = String.format(UPDATE_ITEM, table, set.toString(), pk.getKey() + EQUALS + pk.getValue());
        if(!DBUtils.hasLinkTable(obj.getClass()))
            return nonQuery(sql);
        return nonQuery(sql) && removeLinkTable(obj) && insertLinkTable(obj);
    }

    @Override
    public <T> boolean updateObjects(T[] obj) {
        for(T item : obj) {
            if(!updateObject(item))
                return false;
        }
        return true;
    }

    @Override
    public <T> boolean updateObjects(Collection<T> obj) {
        for(T item : obj) {
            if(!updateObject(item))
                return false;
        }
        return true;
    }

    @Override
    public <T> boolean removeObject(T obj) {
        String table = DBUtils.getTableName(obj.getClass());
        KeyValue pk = DBUtils.getPrimaryKey(obj);
        if (pk == null) {
            throw new DatabaseRuntimeException(MISSING_PK);
        }
        String sql = String.format(DELETE_ITEM, table, pk.getKey() + EQUALS + pk.getValue());
        if(DBUtils.hasLinkTable(obj.getClass()))
            return removeLinkTable(obj) && nonQuery(sql);
        return nonQuery(sql);
    }

    @Override
    public <T> boolean removeObjects(T[] obj) {
        for(T item : obj) {
            if(!removeObject(item))
                return false;
        }
        return true;
    }

    @Override
    public <T> boolean removeObjects(Collection<T> obj) {
        for(T item : obj) {
            if(!removeObject(item))
                return false;
        }
        return true;
    }
    
    @Override
    public void setLogLevel(LogLevel level) {
        this.logLevel = level;
    }
    
    /**
     * 
     * @return Connection with the connection string supplied in the constructor
     * @throws SQLException 
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }
    
    /**
     * 
     * @param <T>
     * @param obj
     * @return 
     */
    protected <T> boolean insertLinkTable(T obj) {
        String id1 = DBUtils.getPrimaryKeyValue(obj);
        log(Level.FINEST, "Insert link table: Id of object: " + obj, id1);
        if(id1 == null)
            return false;
        for(Field f : DBUtils.getAllFields(obj.getClass())) {
            if(f.isAnnotationPresent(LinkTable.class) && !insertLinkTableField(obj, f, id1))
                return false;
        }
        return true;
    }
    
    /**
     * 
     * @param <T>
     * @param item
     * @param field
     * @param id1
     * @return 
     */
    private <T> boolean insertLinkTableField(T item, Field field, String id1){
        String tablename = field.getAnnotation(LinkTable.class).tableName();
        log(Level.FINEST, "Insert link table field: Tablename: ", tablename);
        String column1 = DBUtils.getTableName(item.getClass()) + ID;
        log(Level.FINEST, "Insert link table field: Columnname parent: ", column1);
        if(field.getType().isArray()) {
            Object object = DBUtils.getLinkItem(item, field);
            int length = java.lang.reflect.Array.getLength(object);
            return insertLinkTableArray(object, length, tablename, column1, id1);
        } else if(Iterable.class.isAssignableFrom(field.getType())) {
            Iterator iterator = ((Iterable) DBUtils.getLinkItem(item, field)).iterator();
            return insertLinkTableCollection(iterator, tablename, column1, id1);
        } else {
            Object o = DBUtils.getLinkItem(item, field);
            return insertLinkTableItem(o, tablename, column1, id1);
        }
    }
    
    /**
     * 
     * @param array
     * @param length
     * @param tablename
     * @param column1
     * @param id1
     * @return 
     */
    private boolean insertLinkTableArray(Object array, int length, String tablename, String column1, String id1) {
        for(int i = 0; i < length; i++) {
            Object o = java.lang.reflect.Array.get(array, i);
            if(!insertLinkTableItem(o, tablename, column1, id1))
                return false;
        }
        return true;
    }
    
    /**
     * 
     * @param iterator
     * @param tablename
     * @param column1
     * @param id1
     * @return 
     */
    private boolean insertLinkTableCollection(Iterator iterator, String tablename, String column1, String id1) {
        while(iterator.hasNext()) {
            Object o = iterator.next();
            if(!insertLinkTableItem(o, tablename, column1, id1))
                return false;
        }
        return true;
    }
    
    /**
     * 
     * @param o
     * @param tablename
     * @param column1
     * @param id1
     * @return 
     */
    private boolean insertLinkTableItem(Object o, String tablename, String column1, String id1) {
        if(!insertObject(o))
            return false;
        String id2 = DBUtils.getPrimaryKeyValue(o);
        log(Level.FINEST, "Insert link table item: Id of object: " + o, id2);
        String column2 = DBUtils.getTableName(o.getClass()) + ID;
        log(Level.FINEST, "Insert link table item: Columnname child: ", column2);
        if(id2 == null)
            return false;
        return nonQuery(String.format(INSERT_LINK_TABLE, tablename, column1, column2, id1, id2));
    }
    
    /**
     * 
     * @param <T>
     * @param obj
     * @return 
     */
    protected <T> boolean removeLinkTable(T obj) {
        String column = DBUtils.getTableName(obj.getClass()) + ID;
        log(Level.FINEST, "Remove link table: Columnname: ", column);
        String id = DBUtils.getPrimaryKeyValue(obj);
        log(Level.FINEST, "Remove link table: Id of object: " + obj, id);
        if(id == null)
            return false;
        for(Field f : DBUtils.getAllFields(obj.getClass())) {
            if(!f.isAnnotationPresent(LinkTable.class))
                continue;
            String table = f.getAnnotation(LinkTable.class).tableName();
            String sql = String.format(DELETE_ITEM, table, column + EQUALS + id);
            if(!nonQuery(sql))
                return false;
        }
        return true;
    }
    
    /**
     * 
     * @param <T>
     * @param constructor
     * @param map
     * @return 
     */
    protected <T> T toInstance(Constructor<T> constructor, Map<String, String> map) {
        try {
            T output = constructor.newInstance((Object[]) null);
            Class clazz = output.getClass();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Field field = DBUtils.getFieldFromString(clazz, entry.getKey());
                if(field == null) {
                    log(Level.FINE, "Key: {0} not found.", entry.getKey());
                    continue;
                }
                if(field.isAnnotationPresent(ForeignKey.class)) {
                    log(Level.FINE, "Foreign key found. Key name: {0}. Key value: {1}. Key class: {2}", new Object[]{entry.getKey(), entry.getValue(), field.getType().toString()});
                    //If the value is null insert null on the place of the foreign key otherwise get item from db
                    DBUtils.setField(output, field, (entry.getValue() == null) ? null : getItem(field.getType(), entry.getValue()));
                } else {
                    DBUtils.setField(output, field, entry.getValue());
                }
            }
            for(Field f : DBUtils.getAllFields(clazz)) {
                if(f.isAnnotationPresent(LinkTable.class))
                    setLinkTable(output, f);
            }
            return output;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Executes a query which sets the autogenerated primary key of the object.
     * @param <T> The type of the object
     * @param obj The object
     * @param sql The query to execute
     * @param specifiedOutput needs to be set true if the sql query already specifies the column that needs to be outputed.
     * @return True if the query was executed and the primary key has been set.
     */
    protected <T> boolean insertQuery(T obj, String sql, boolean specifiedOutput) {
        log(Level.INFO, sql);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            if(specifiedOutput) {
                rs = stmt.executeQuery(sql);
            } else {
                stmt.execute(sql, Statement.RETURN_GENERATED_KEYS);
                rs = stmt.getGeneratedKeys();
            }
            if(rs.next()) {
                String s = rs.getString(1);
                DBUtils.setPrimaryKey(obj, s);
                return true;
            }
        } catch (SQLException ex) {
            log(Level.SEVERE, null, ex);
        } finally {
            closeStatementAndResult(rs, stmt, conn);
        }
        return false;
    }
    
    /**
     * 
     * @param sql The query to execute
     * @return true if the query succeeded
     */
    protected boolean nonQuery(String sql) {
        log(Level.INFO, sql);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            return stmt.executeUpdate(sql) >= 0;
        } catch (SQLException ex) {
            log(Level.SEVERE, null, ex);
            return false;
        } finally {
            closeStatement(stmt, conn);
        }
    }
    
    /**
     * 
     * @param sql The query to execute
     * @return List with a map that maps the key (columheader) to the value.
     */
    protected List<Map<String, String>> query(String sql) {
        log(Level.INFO, sql);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String, String>> output = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i <= columns; i++) {
                    map.put(rsmd.getColumnName(i), rs.getString(i));
                }
                output.add(map);
            }
            return output;
        } catch (SQLException ex) {
            log(Level.SEVERE, null, ex);
            return output;
        } finally {
            closeStatementAndResult(rs, stmt, conn);
        }
    }
    
    /**
     * Closes a statement and connection.
     * @param stmt The statement to close
     * @param conn The connection to close
     */
    protected void closeStatement(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                log(Level.SEVERE, null, ex);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Closes a result set, statement and connection.
     * @param rs The result set to close
     * @param stmt The statement to close
     * @param conn The connection to close
     */
    protected void closeStatementAndResult(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                log(Level.SEVERE, null, ex);
            }
        }
        closeStatement(stmt, conn);
    }
    
    /**
     * 
     * @param level The level at which to log it
     * @param message The message to log
     */
    protected void log(Level level, String message) {
        if(doLog(level)) {
            logger.log(level, message);
        }
    }
    
    /**
     * 
     * @param level The level at which to log it
     * @param message The message to log
     * @param thrown The throwable to log
     */
    protected void log(Level level, String message, Throwable thrown) {
        if(doLog(level)) {
            logger.log(level, message, thrown);
        }
    }
    
    /**
     * 
     * @param level The level at which to log it
     * @param message The message to log
     * @param object The object to log
     */
    protected void log(Level level, String message, Object object) {
        if(doLog(level)) {
            logger.log(level, message, object);
        }
    }
    
    /**
     * Get item which has primary key pk.
     * @param <T> The type of the class
     * @param clazz The class of the item to get
     * @param pk The primary key string
     * @return item with primary key pk or null is no item with that primary key exists.
     * @see PrimaryKey
     */
    private <T> T getItem(Class<T> clazz, String pk) {
        try {
            Constructor<T> constructor = clazz.getConstructor((Class<?>[]) null);
            String table = DBUtils.getTableName(clazz);
            String sql = String.format(SELECT_WHERE_PK, DBUtils.getColumnString(clazz), table, DBUtils.getPrimaryKeyName(clazz), pk);
            List<Map<String, String>> maps = query(sql);
            if(!maps.isEmpty())
                return toInstance(constructor, maps.get(0));
            return null;
        } catch (NoSuchMethodException | SecurityException ex) {
            log(Level.SEVERE, NO_VALID_CONSTRUCTOR, ex);
            return null;
        }
    }
    
    /**
     * Will set a field with a link table annotation
     * @param <T> The type of the output
     * @param output The item in which the field will be set
     * @param f The field that needs to be set
     * @see LinkTable
     */
    private <T> void setLinkTable(T output, Field f) {
        LinkTable lt = f.getAnnotation(LinkTable.class);
        if(f.getType().isArray()) {
            DBUtils.setField(output, f, getLinkItems(lt.clazz(), output, lt.tableName()).toArray());
        } else if(Iterable.class.isAssignableFrom(f.getType())) {
            DBUtils.setField(output, f, getLinkItems(lt.clazz(), output, lt.tableName()));
        } else {
            DBUtils.setField(output, f, getLinkItem(lt.clazz(), output, lt.tableName()));
        }
    }
    
    /**
     * 
     * @param <T> The type of the class
     * @param <U> The type of the item
     * @param clazz The class of which the link item is a instance
     * @param item The item that has the link item in its fields
     * @param linktable The name of the link table
     * @return Collection of objects with class class which belong to the item
     * @see LinkTable
     */
    private <T, U> Collection<T> getLinkItems(Class<T> clazz, U item, String linktable) {
        Collection<T> output = new ArrayList<>();
        String columnnames = DBUtils.getColumnString(clazz);
        String table = DBUtils.getTableName(clazz);
        String pk = DBUtils.getPrimaryKeyName(clazz);
        String column = DBUtils.getTableName(item.getClass()) + ID;
        KeyValue kv = DBUtils.getPrimaryKey(item);
        String sql = String.format(SELECT_LINK_TABLE, columnnames, table, linktable, pk, table + ID, column + EQUALS + kv.getValue());
        try {
            Constructor<T> constructor = clazz.getConstructor((Class<?>[]) null);
            for (Map<String, String> map : query(sql)) {
                output.add(toInstance(constructor, map));
            }
            return output;
        } catch (NoSuchMethodException | SecurityException ex) {
            log(Level.SEVERE, NO_VALID_CONSTRUCTOR, ex);
            return output;
        }
    }
    
    /**
     * 
     * @param <T> The type of the class
     * @param <U> The type of the item
     * @param clazz The class of which the link item is a instance
     * @param item The item that has the link item in its fields
     * @param linktable The name of the link table
     * @return The object with class class which belongs to the item
     * @see LinkTable
     */
    private <T, U> T getLinkItem(Class<T> clazz, U item, String linktable) {
        return getLinkItems(clazz, item, linktable).stream().findFirst().orElse(null);
    }
    
    /**
     * Checks the Level given to the LogLevel field in this class
     * @param level The level to check against
     * @return Level is high enough to log.
     * @see LogLevel
     */
    private boolean doLog(Level level) {
        int loglevel;
        switch(logLevel) {
            case INFO:
                loglevel = Level.FINE.intValue();
                break;
            case QUERY:
                loglevel = Level.INFO.intValue();
                break;
            case SEVERE:
                loglevel = Level.SEVERE.intValue();
                break;
            case NONE:
                loglevel = Level.OFF.intValue();
                break;
            case DEBUG:
            default:
                loglevel = Level.ALL.intValue();
        }
        return level.intValue() >= loglevel;
    }
    
    /**
     * 
     * @param <T> The type of the class
     * @param clazz The class of the objects to return
     * @param query The query to execute
     * @return A collection with the objects of class clazz which 
     */
    private <T> Collection<T> queryToObject(Class<T> clazz, String query) {
        Collection<T> output = new ArrayList<>();
        try {
            Constructor<T> constructor = clazz.getConstructor((Class<?>[]) null);
            for (Map<String, String> map : query(query)) {
                output.add(toInstance(constructor, map));
            }
            return output;
        } catch (NoSuchMethodException | SecurityException ex) {
            log(Level.SEVERE, NO_VALID_CONSTRUCTOR, ex);
            return output;
        }
    }
}