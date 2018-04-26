package info.peperkoek.databaselibrary;

import info.peperkoek.databaselibrary.enums.Database;
import info.peperkoek.databaselibrary.interfaces.DataAccessObject;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public final class DatabaseManager {
    private static final String MSSQL_DB = "jdbc:sqlserver://{0};";
    private static final String MYSQL_DB = "jdbc:mysql://{0};";
    private static final String ORACLE_DB = "jdbc:oracle:thin:@{0};";
    
    /**
     * 
     * @param user
     * @param password
     * @param databaseUrl
     * @param database
     * @return 
     */
    public static DataAccessObject getManager(String user, String password, String databaseUrl, Database database) {
        
    }
    
    /**
     * 
     * @param user
     * @param password
     * @param databaseUrl
     * @param databaseName
     * @param database
     * @return 
     */
    public static DataAccessObject getManager(String user, String password, String databaseUrl, String databaseName, Database database) {
        
    }
    
    /**
     * 
     * @param user
     * @param password
     * @param databaseUrl
     * @param databaseName
     * @param port
     * @param database
     * @return 
     */
    public static DataAccessObject getManager(String user, String password, String databaseUrl, String databaseName, int port, Database database) {
        
    }
}