package info.peperkoek.databaselibrary.core;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
abstract class DataAccessObject implements IDataAccessObject {
    protected static final String A = "A.";
    protected static final String EMPTY = "";
    protected static final String ID = "_id";
    protected static final String EQUALS = " = ";
    protected static final String COMMA = ", ";
    protected static final String AND = " AND ";
    protected static final String MISSING_PK = "Primary key can not be found. Is annotation PrimaryKey applied?";
    protected static final String NO_VALID_CONSTRUCTOR = "Cannot find empty public constructor. Empty meaning no arguments.";
    protected static final String SELECT_ALL = "SELECT %s FROM %s";
    protected static final String SELECT_ID = "SELECT id FROM %s";
    protected static final String SELECT_ID_WHERE = "SELECT id FROM %s WHERE %s";
    protected static final String SELECT_WHERE = "SELECT %s FROM %s WHERE %s";
    protected static final String SELECT_WHERE_PK = "SELECT %s FROM %s WHERE %s = %s";
    protected static final String SELECT_TOP = "SELECT TOP %s %s FROM %s";
    protected static final String SELECT_TOP_ORDER = "SELECT TOP %s %s FROM %s ORDER BY %s";
    protected static final String DELETE_ITEM = "DELETE FROM %s WHERE %s";
    protected static final String INSERT_ITEM = "INSERT INTO %s (%s) VALUES (%s)";
    protected static final String INSERT_ITEM_OUTPUT = "INSERT INTO %s (%s) OUTPUT INSERTED.%s VALUES (%s)";
    protected static final String UPDATE_ITEM = "UPDATE %s SET %s WHERE %s";
    protected static final String INSERT_LINK_TABLE = "INSERT INTO %s (%s, %s) VALUES (%s, %s)";
    protected static final String SELECT_LINK_TABLE = "SELECT * from %s A join %s B on A.%s = B.%s where %s";
}