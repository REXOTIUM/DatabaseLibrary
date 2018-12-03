package info.peperkoek.databaselibrary.core;

import info.peperkoek.databaselibrary.enums.LogLevel;
import info.peperkoek.databaselibrary.interfaces.ILoggingObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
class LoggingObject implements ILoggingObject {
    @SuppressWarnings("NonConstantLogger")
    private final Logger logger;
    private LogLevel logLevel;
    
    LoggingObject(Logger logger, LogLevel logLevel) {
        this.logger = logger;
        this.logLevel = logLevel;
    }
    
    @Override
    public void setLogLevel(LogLevel level) {
        this.logLevel = level;
    }
    
    /**
     * 
     * @param level The level at which to log it
     * @param message The message to log
     */
    protected void log(Level level, String message) {
        if(doLog(level)) {
            logger.log(level, message);
        }
    }
    
    /**
     * 
     * @param level The level at which to log it
     * @param message The message to log
     * @param thrown The throwable to log
     */
    protected void log(Level level, String message, Throwable thrown) {
        if(doLog(level)) {
            logger.log(level, message, thrown);
        }
    }
    
    /**
     * 
     * @param level The level at which to log it
     * @param message The message to log
     * @param object The object to log
     */
    protected void log(Level level, String message, Object object) {
        if(doLog(level)) {
            logger.log(level, message, object);
        }
    }
    
    /**
     * Checks the Level given to the LogLevel field in this class
     * @param level The level to check against
     * @return Level is high enough to log.
     * @see LogLevel
     */
    private boolean doLog(Level level) {
        int loglevel;
        switch(logLevel) {
            case INFO:
                loglevel = Level.FINE.intValue();
                break;
            case QUERY:
                loglevel = Level.INFO.intValue();
                break;
            case SEVERE:
                loglevel = Level.SEVERE.intValue();
                break;
            case NONE:
                loglevel = Level.OFF.intValue();
                break;
            case DEBUG:
            default:
                loglevel = Level.ALL.intValue();
        }
        return level.intValue() >= loglevel;
    }
}