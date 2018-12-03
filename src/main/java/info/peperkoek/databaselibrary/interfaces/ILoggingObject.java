package info.peperkoek.databaselibrary.interfaces;

import info.peperkoek.databaselibrary.enums.LogLevel;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public interface ILoggingObject {
    
    /**
     * 
     * @param level The level at which the database access object will log messages
     */
    public void setLogLevel(LogLevel level);
}