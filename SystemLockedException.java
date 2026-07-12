/**
 * Custom Exception thrown when the administrative operations are locked 
 * due to consecutive failed authentication attempts.
 * 
 * Part of the Banking Information System Project.
 */
public class SystemLockedException extends Exception {
    /**
     * Parameterized constructor.
     * 
     * @param message Detailed error message
     */
    public SystemLockedException(String message) {
        super(message);
    }
}
