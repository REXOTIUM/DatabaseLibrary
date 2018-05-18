package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.enums.Database;
import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public final class DatabaseManager {
    private static final String MSSQL_DB = "jdbc:sqlserver://{0};{1}";
    private static final String MYSQL_DB = "jdbc:mysql://{0};{1}";
    private static final String ORACLE_DB = "jdbc:oracle:thin:@{0};{1}";
    private static final String DEFAULT_ERROR = "Hoe de hel krijg je dit voor elkaar??? ";
    private static final String USER_STRING = "user=";
    private static final String PASSWORD_STRING = ";password=";
    
    private DatabaseManager() {
        throw new IllegalAccessError("Factory class");
    }
    
    /**
     * 
     * @param databaseUrl The url to the computer/server where the database is hosted
     * @param database The database manager you need
     * @return The DataAccessObject that can access the database provided in the database parameter
     * @see IDataAccessObject
     */
    public static IDataAccessObject getManager(String databaseUrl, Database database) {
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
     * @param user Username to login to the database
     * @param password Password to login to the database
     * @param databaseUrl The url to the computer/server where the database is hosted
     * @param database The database manager you need
     * @return The DataAccessObject that can access the database provided in the database parameter
     * @see IDataAccessObject
     */
    public static IDataAccessObject getManager(String user, String password, String databaseUrl, Database database) {
        switch(database){
            case MSSQL:
                String end = USER_STRING + user + PASSWORD_STRING + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, databaseUrl, end));
            case ORACLE:
                
            case MYSQL:
                
            default:
                throw new DatabaseRuntimeException(DEFAULT_ERROR + database.name());
        }
    }
    
    /**
     * 
     * @param user Username to login to the database
     * @param password Password to login to the database
     * @param databaseUrl The url to the computer/server where the database is hosted
     * @param databaseName The specific name of the database
     * @param database The database manager you need
     * @return The DataAccessObject that can access the database provided in the database parameter
     * @see IDataAccessObject
     */
    public static IDataAccessObject getManager(String user, String password, String databaseUrl, String databaseName, Database database) {
        switch(database){
            case MSSQL:
                String start = databaseUrl + "\\" + databaseName;
                String end = USER_STRING + user + PASSWORD_STRING + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, start, end));
            case ORACLE:
                
            case MYSQL:
                
            default:
                throw new DatabaseRuntimeException(DEFAULT_ERROR + database.name());
        }
    }
    
    /**
     * 
     * @param user Username to login to the database
     * @param password Password to login to the database
     * @param databaseUrl The url to the computer/server where the database is hosted
     * @param databaseName The specific name of the database
     * @param port The port on which to connect to the database
     * @param database The database manager you need
     * @return The DataAccessObject that can access the database provided in the database parameter
     * @see IDataAccessObject
     */
    public static IDataAccessObject getManager(String user, String password, String databaseUrl, String databaseName, int port, Database database) {
        switch(database){
            case MSSQL:
                String start = databaseUrl + "\\" + databaseName + ":" + port;
                String end = USER_STRING + user + PASSWORD_STRING + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, start, end));
            case ORACLE:
                
            case MYSQL:
                
            default:
                throw new DatabaseRuntimeException(DEFAULT_ERROR + database.name());
        }
    }
}