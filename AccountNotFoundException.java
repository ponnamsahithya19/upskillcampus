/**
 * Custom Exception thrown when an account query or action fails 
 * because the requested account number does not exist.
 * 
 * Part of the Banking Information System Project.
 */
public class AccountNotFoundException extends Exception {
    /**
     * Parameterized constructor.
     * 
     * @param message Detailed error message
     */
    public AccountNotFoundException(String message) {
        super(message);
    }
}
