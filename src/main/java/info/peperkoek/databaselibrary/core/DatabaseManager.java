package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.enums.Database;
import info.peperkoek.databaselibrary.exceptions.DatabaseRuntimeException;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public final class DatabaseManager {
    private static final String EMPTY = "";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String MSSQL_DB = "jdbc:sqlserver://{0};{1}";
    private static final String MYSQL_DB = "jdbc:mysql://{0}";
    private static final String ORACLE_DB = "jdbc:oracle:thin:{0}@{1}:{2}";
    private static final String DEFAULT_ERROR = "Hoe de hel krijg je dit voor elkaar??? ";
    private static final String USER_STRING_MSSQL = "user=";
    private static final String PASSWORD_STRING_MSSQL = ";password=";
    private static final String USER_STRING_MYSQL = "?user=";
    private static final String PASSWORD_STRING_MYSQL = "&password=";
    
    private DatabaseManager() {
        throw new IllegalAccessError("Factory class");
    }
    
    /**
     * 
     * @param databaseUrl The url to the computer/server where the database is hosted
     * @param database The database manager you need
     * @return The DataAccessObject that can access the database provided in the database parameter
     * @see DataAccessObject
     */
    public static DataAccessObject getManager(String databaseUrl, Database database) {
        switch(database){
            case MSSQL:
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, databaseUrl, EMPTY));
            case MYSQL:
                return new MySQLDataAccessObject(String.format(MYSQL_DB, databaseUrl));
            case ORACLE:
                return new OracleDataAccessObject(String.format(ORACLE_DB, EMPTY, databaseUrl, EMPTY));
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
     * @see DataAccessObject
     */
    public static DataAccessObject getManager(String user, String password, String databaseUrl, Database database) {
        switch(database){
            case MSSQL:
                String endM = USER_STRING_MSSQL + user + PASSWORD_STRING_MSSQL + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, databaseUrl, endM));
            case MYSQL:
                String end = USER_STRING_MYSQL + user + PASSWORD_STRING_MYSQL + password; 
                return new MySQLDataAccessObject(String.format(MYSQL_DB, databaseUrl + end));
            case ORACLE:
                String startO = user + SLASH + password;
                return new OracleDataAccessObject(String.format(ORACLE_DB, startO, databaseUrl, EMPTY));
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
     * @see DataAccessObject
     */
    public static DataAccessObject getManager(String user, String password, String databaseUrl, String databaseName, Database database) {
        switch(database){
            case MSSQL:
                String startM = databaseUrl + "\\" + databaseName;
                String endM = USER_STRING_MSSQL + user + PASSWORD_STRING_MSSQL + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, startM, endM));
            case MYSQL:
                String end = SLASH + databaseName + USER_STRING_MYSQL + user + PASSWORD_STRING_MYSQL + password; 
                return new MySQLDataAccessObject(String.format(MYSQL_DB, databaseUrl + end));
            case ORACLE:
                String startO = user + SLASH + password;
                return new OracleDataAccessObject(String.format(ORACLE_DB, startO, databaseUrl, databaseName));
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
     * @see DataAccessObject
     */
    public static DataAccessObject getManager(String user, String password, String databaseUrl, String databaseName, int port, Database database) {
        switch(database){
            case MSSQL:
                String startM = databaseUrl + "\\" + databaseName + COLON + port;
                String endM = USER_STRING_MSSQL + user + PASSWORD_STRING_MSSQL + password;
                return new MSSQLDataAccessObject(String.format(MSSQL_DB, startM, endM));
            case MYSQL:
                String end = COLON + port + SLASH + databaseName + USER_STRING_MYSQL + user + PASSWORD_STRING_MYSQL + password;
                return new MySQLDataAccessObject(String.format(MYSQL_DB, databaseUrl + end));
            case ORACLE:
                String startO = user + SLASH + password;
                String endO = port + COLON + databaseName;
                return new OracleDataAccessObject(String.format(ORACLE_DB, startO, databaseUrl, endO));
            default:
                throw new DatabaseRuntimeException(DEFAULT_ERROR + database.name());
        }
    }
}