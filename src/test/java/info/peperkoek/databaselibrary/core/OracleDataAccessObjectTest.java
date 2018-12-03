package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.enums.Database;
import info.peperkoek.databaselibrary.enums.LogLevel;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 * @email m.a.a.pijnenburg@gmail.com
 */
@Ignore
public class OracleDataAccessObjectTest extends DataAccessObjectTest {
    
    @Before
    public void setUp() {
        dao = DatabaseManager.getManager("DatabaseLibTestUser", "DatabaseLibTestUserPassword", "database.peperkoek.info", "DatabaseLibTest", 6969, Database.ORACLE);
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