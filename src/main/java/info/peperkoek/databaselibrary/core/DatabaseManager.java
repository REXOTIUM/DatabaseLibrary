package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.enums.Database;
import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public final class DatabaseManager {
    private static final String MSSQL_DB = "jdbc:sqlserver://{0};{1}";
    private static final String MYSQL_DB = "jdbc:mysql://{0};{1}";
    private static final String ORACLE_DB = "jdbc:oracle:thin:@{0};{1}";
    private static final String DEFAULT_ERROR = "Hoe de hel krijg je dit voor elkaar??? ";
    
    /**
     * 
     * @param databaseUrl
     * @param database
     * @return 
     */
    public static DataAccessObject getManager(String databaseUrl, Database database) {
        switch(database){
            case MSSQL:
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, databaseUrl, ""));
            case ORACLE:
                
            case MYSQL:
                
            default:
                throw new DatabaseRuntimeException(DEFAULT_ERROR + database.name());
        }
    }
    
    /**
     * 
     * @param user
     * @param password
     * @param databaseUrl
     * @param database
     * @return 
     */
    public static DataAccessObject getManager(String user, String password, String databaseUrl, Database database) {
        switch(database){
            case MSSQL:
                String end = "user=" + user + ";password=" + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, databaseUrl, end));
            case ORACLE:
                
            case MYSQL:
                
            default:
                throw new DatabaseRuntimeException(DEFAULT_ERROR + database.name());
        }
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
        switch(database){
            case MSSQL:
                String start = databaseUrl + "\\" + databaseName;
                String end = "user=" + user + ";password=" + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, start, end));
            case ORACLE:
                
            case MYSQL:
                
            default:
                throw new DatabaseRuntimeException(DEFAULT_ERROR + database.name());
        }
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
        switch(database){
            case MSSQL:
                String start = databaseUrl + "\\" + databaseName + ":" + port;
                String end = "user=" + user + ";password=" + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, start, end));
            case ORACLE:
                
            case MYSQL:
                
            default:
                throw new DatabaseRuntimeException(DEFAULT_ERROR + database.name());
        }
    }
}