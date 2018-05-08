package info.peperkoek.databaselibrary;

import info.peperkoek.databaselibrary.annotations.ForeignKey;
import info.peperkoek.databaselibrary.annotations.LinkTable;
import info.peperkoek.databaselibrary.core.KeyValue;
import info.peperkoek.databaselibrary.core.Query;
import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;
import info.peperkoek.databaselibrary.interfaces.DataAccessObject;
import info.peperkoek.databaselibrary.utils.DBUtils;
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
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class MSSQLDataAccessObject implements DataAccessObject {
    private static final Logger LOG = Logger.getLogger(MSSQLDataAccessObject.class.getName());
    private final String connectionString;
    
    /**
     * 
     * @param connectionString 
     */
    public MSSQLDataAccessObject(String connectionString) {
        this.connectionString = connectionString;
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
        Collection<T> output = new ArrayList<>();
        try {
            Constructor<T> constructor = clazz.getConstructor((Class<?>[]) null);
            String table = DBUtils.getTableName(clazz);
            String sql = String.format(SELECT_ALL, DBUtils.getColumnString(clazz), table);
            for (Map<String, String> map : query(sql)) {
                output.add(toInstance(constructor, map));
            }
            return output;
        } catch (NoSuchMethodException | SecurityException ex) {
            LOG.log(Level.SEVERE, NO_VALID_CONSTRUCTOR, ex);
            return output;
        }
    }
    
    @Override
    public <T, U> Collection<T> getObjects(Class<T> clazz, U item) {
        Collection<T> output = new ArrayList<>();
        try {
            Constructor<T> constructor = clazz.getConstructor((Class<?>[]) null);
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
            for (Map<String, String> map : query(sql)) {
                output.add(toInstance(constructor, map));
            }
            return output;
        } catch (NoSuchMethodException | SecurityException ex) {
            LOG.log(Level.SEVERE, NO_VALID_CONSTRUCTOR, ex);
            return output;
        }
    }

    @Override
    public <T> Collection<T> getObjects(Class<T> clazz, Query query) {
        Collection<T> output = new ArrayList<>();
        try {
            Constructor<T> constructor = clazz.getConstructor((Class<?>[]) null);
            for (Map<String, String> map : query(query.getQuery())) {
                output.add(toInstance(constructor, map));
            }
            return output;
        } catch (NoSuchMethodException | SecurityException ex) {
            LOG.log(Level.SEVERE, NO_VALID_CONSTRUCTOR, ex);
            return output;
        }
    }

    @Override
    public <T> boolean insertObject(T obj) {
        boolean pkAutoGen = DBUtils.IsPKAutoGenerated(obj);
        String table = DBUtils.getTableName(obj.getClass());
        List<KeyValue> kvs = DBUtils.getFields(obj, pkAutoGen);
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (KeyValue kv : kvs) {
            if (kv.getKey() == null || EMPTY.equals(kv.getValue())) {
                continue;
            }
            columns.append(kv.getKey());
            columns.append(COMMA);
            values.append(kv.getValue());
            values.append(COMMA);
        }
        columns.delete(columns.length() - 2, columns.length()); //remove excess comma
        values.delete(values.length() - 2, values.length()); //remove excess comma
        if(pkAutoGen) {
            String sql = String.format(INSERT_ITEM_OUTPUT, table, columns.toString(), DBUtils.getPrimaryKey(obj), values.toString());
            if(!DBUtils.hasLinkTable(obj.getClass()))
                return insertQuery(obj, sql);
            return insertQuery(obj, sql) && insertLinkTable(obj);
        } else {
            String sql = String.format(INSERT_ITEM, table, columns.toString(), values.toString());
            if(!DBUtils.hasLinkTable(obj.getClass()))
                return nonQuery(sql);
            return nonQuery(sql) && insertLinkTable(obj);
        }
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

    @Override
    public <T> boolean updateObject(T obj) {
        String table = DBUtils.getTableName(obj.getClass());
        List<KeyValue> kvs = DBUtils.getFields(obj, true);
        KeyValue pk = DBUtils.getPrimaryKey(obj);
        if (pk == null) {
            throw new NullPointerException(MISSING_PK);
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
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }
    
    /**
     * 
     * @param stmt
     * @param conn 
     */
    private void closeStatement(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 
     * @param stmt
     * @param conn 
     */
    private void closeStatementAndResult(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        closeStatement(stmt, conn);
    }

    private <T> boolean insertQuery(T obj, String sql) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean nonQuery(String sql) {
        LOG.log(Level.INFO, sql);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            return stmt.executeUpdate(sql) >= 0;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        } finally {
            closeStatement(stmt, conn);
        }
    }
    
    /**
     * 
     * @param query
     * @return 
     */
    private List<Map<String, String>> query(String sql) {
        LOG.log(Level.INFO, sql);
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
            LOG.log(Level.SEVERE, null, ex);
            return output;
        } finally {
            closeStatementAndResult(rs, stmt, conn);
        }
    }
    
    private <T> boolean insertLinkTable(T obj) {
        String id1 = DBUtils.getPrimaryKeyValue(obj);
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
        String column1 = DBUtils.getTableName(item.getClass()) + ID;
        if(field.getType().isArray()) {
            Object object = DBUtils.getLinkItem(item, field);
            int length = java.lang.reflect.Array.getLength(object);
            for(int i = 0; i < length; i++) {
                Object o = java.lang.reflect.Array.get(object, i);
                if(!insertLinkTableItem(o, tablename, column1, id1))
                    return false;
            }
            return true;
        } else if(Iterable.class.isAssignableFrom(field.getType())) {
            Iterator iterator = ((Iterable) DBUtils.getLinkItem(item, field)).iterator();
            while(iterator.hasNext()) {
                Object o = iterator.next();
                if(!insertLinkTableItem(o, tablename, column1, id1))
                    return false;
            }
            return true;
        } else {
            Object o = DBUtils.getLinkItem(item, field);
            return insertLinkTableItem(o, tablename, column1, id1);
        }
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
        String column2 = DBUtils.getTableName(o.getClass()) + ID;
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
    private <T> boolean removeLinkTable(T obj) {
        String column = DBUtils.getTableName(obj.getClass()) + ID;
        String id = DBUtils.getPrimaryKeyValue(obj);
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
    private <T> T toInstance(Constructor<T> constructor, Map<String, String> map) {
        try {
            T output = constructor.newInstance((Object[]) null);
            Class clazz = output.getClass();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Field field = DBUtils.getFieldFromString(clazz, entry.getKey());
                if(field == null) {
                    LOG.log(Level.INFO, "Key: {0} not found.", entry.getKey());
                    continue;
                }
                if(field.isAnnotationPresent(ForeignKey.class)) {
                    LOG.log(Level.INFO, "Foreign key found. Key name: {0}. Key value: {1}. Key class: {2}", new Object[]{entry.getKey(), entry.getValue(), field.getType().toString()});
                    //If the value is null insert null on the place of the foreign key otherwise get item from db
                    DBUtils.setField(output, field, (entry.getValue() == null) ? null : getItem(field.getType(), Integer.parseInt(entry.getValue())));
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
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * 
     * @param <T>
     * @param output
     * @param clazz
     * @param f 
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
     * @param <T>
     * @param <U>
     * @param clazz
     * @param item
     * @param linktable
     * @return 
     */
    private <T, U> T getLinkItem(Class<T> clazz, U item, String linktable) {
        return getLinkItems(clazz, item, linktable).stream().findFirst().orElse(null);
    }
    
    /**
     * 
     * @param <T>
     * @param <U>
     * @param clazz
     * @param item
     * @param linktable
     * @return 
     */
    private <T, U> Collection<T> getLinkItems(Class<T> clazz, U item, String linktable) {
        Collection<T> output = new ArrayList<>();
        String table = DBUtils.getTableName(clazz);
        String pk = DBUtils.getPrimaryKeyValue(clazz);
        String column = DBUtils.getTableName(item.getClass()) + ID;
        KeyValue kv = DBUtils.getPrimaryKey(item);
        String sql = String.format(SELECT_LINK_TABLE, table, linktable, pk, table + ID, column + EQUALS + kv.getValue());
        try {
            Constructor<T> constructor = clazz.getConstructor((Class<?>[]) null);
            for (Map<String, String> map : query(sql)) {
                output.add(toInstance(constructor, map));
            }
            return output;
        } catch (NoSuchMethodException | SecurityException ex) {
            LOG.log(Level.SEVERE, NO_VALID_CONSTRUCTOR, ex);
            return output;
        }
    }
}