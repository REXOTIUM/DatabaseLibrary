package info.peperkoek.databaselibrary;

import info.peperkoek.databaselibrary.enums.Database;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class DatabaseManager {
    private final String user;
    private final String password;
    private final String url;
    private final String port;
    private final Database database;
    
    public DatabaseManager(String user, String password, String url, String port, Database database) {
        this.user = user;
        this.password = password;
        this.url = url;
        this.port = port;
        this.database = database;
    }
}