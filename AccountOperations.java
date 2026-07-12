/**
 * Interface defining the contract for core bank account transactions.
 * Highlights the OOP concept of Abstraction.
 * 
 * Part of the Banking Information System Project.
 */
public interface AccountOperations {
    /**
     * Deposits a positive amount into the account.
     * 
     * @param amount The amount to deposit
     * @throws InvalidTransactionException if deposit amount is invalid
     */
    void deposit(double amount) throws InvalidTransactionException;

    /**
     * Withdraws an amount from the account if rules allow.
     * 
     * @param amount The amount to withdraw
     * @throws InsufficientFundsException if balance falls below threshold
     * @throws InvalidTransactionException if withdrawal amount is invalid
     */
    void withdraw(double amount) throws InsufficientFundsException, InvalidTransactionException;
}
