/**
 * Custom Exception thrown when a transaction is invalid (e.g., negative amounts,
 * self-transfers, or violating core banking parameters).
 * 
 * Part of the Banking Information System Project.
 */
public class InvalidTransactionException extends Exception {
    /**
     * Parameterized constructor.
     * 
     * @param message Detailed error message
     */
    public InvalidTransactionException(String message) {
        super(message);
    }
}
