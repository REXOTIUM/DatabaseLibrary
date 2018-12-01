package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.enums.Database;
import info.peperkoek.databaselibrary.enums.LogLevel;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class MySQLDataAccessObjectTest extends DataAccessObjectTest {
    
    @Before
    public void setup() {
        dao = DatabaseManager.getManager("DatabaseLibTestUser", "DatabaseLibTestUserPassword", "database.peperkoek.info", "databaselibtest", 1993, Database.MYSQL);
        dao.setLogLevel(LogLevel.DEBUG);
        createData();
        insertData();
    }
    
    @After
    public void breakdown() {
        removeData();
    }
    
    @Test
    public void mssqlTest() {
        test();
    }
}