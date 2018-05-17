package info.peperkoek.databaselibrary.enums;

/**
 * Loglevel for database managers. These set the logging output for the managers.
 * 
 * Levels go from debug(lowest) to none(highest)
 * 
 * @author Rick Pijnenburg - REXOTIUM
 */
public enum LogLevel {
    /**
     * All possible messages can be found in the logs.
     */
    DEBUG,
    /**
     * Queries and keys can be found in the logs.
     */
    INFO,
    /**
     * Only queries can be found in the logs.
     */
    QUERY,
    /**
     * The database managers dont log anything (exceptions that occur in any util classes will still be shown).
     */
    NONE 
}