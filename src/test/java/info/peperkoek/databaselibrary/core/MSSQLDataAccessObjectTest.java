package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.enums.Database;
import info.peperkoek.databaselibrary.enums.LogLevel;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
public class MSSQLDataAccessObjectTest extends DataAccessObjectTest {
    
    @Before
    public void setUp() {
        dao = DatabaseManager.getManager("DatabaseLibTestUser", "DatabaseLibTestUserPassword", "database.peperkoek.info", "DatabaseLibTest", 1818, Database.MSSQL);
        dao.setLogLevel(LogLevel.DEBUG);
        createData();
        insertData();
    }
    
    @After
    public void tearDown() {
        removeData();
    }
    
    @Test
    public void mssqlTest() {
        test();
    }
}