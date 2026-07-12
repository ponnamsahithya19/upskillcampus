/**
 * Custom Exception thrown when an account does not have sufficient funds 
 * to complete a withdrawal or transfer, or violates minimum balance.
 * 
 * Part of the Banking Information System Project.
 */
public class InsufficientFundsException extends Exception {
    /**
     * Parameterized constructor.
     * 
     * @param message Detailed error message
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}
