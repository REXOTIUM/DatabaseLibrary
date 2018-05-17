package info.peperkoek.databaselibrary.exceptions;

/**
 *
 * @author Rick Pijnenburg - REXOTIUM
 */
public class DatabaseRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 7353342597997591647L;
    
        /**
     * Constructs a new DatabaseRuntimeException with null as its detail message. The cause is not initialized, and may be subsequently be initialized by a call to the Throwable.initCause(Throwable).
     */
    public DatabaseRuntimeException() {
        super();
    }
    
    /**
     * Constructs a new DatabaseRuntimeException with a specified detail message. The cause is not initialized, and may be subsequently be initialized by a cal to the Throwable.initCause(Throwable).
     * 
     * @param message the detail message.
     */
    public DatabaseRuntimeException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DatabaseRuntimeException with the specified cause and a detail message of (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     * This constructor is useful for exceptions that are little more than wrappers for other throwables (for example, PrivilegedActionException).
     * 
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DatabaseRuntimeException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new DatabaseRuntimeException with aa specified detail message and cause.
     * 
     * Note that the detail message associated with the cause is not automatically incorporated in this exceptions detail message.
     * 
     * @param message the detail message.
     * @param cause the cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DatabaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new DatabaseRuntimeException with the specified detail message, cause, suppression enabled or disabled, and writable stack trace enabled or disabled.
     * 
     * @param message the detail message.
     * @param cause the cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writeableStackTrace whether or not the stack trace should be writable
     */
    public DatabaseRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
        super(message, cause, enableSuppression, writeableStackTrace);
    }
}