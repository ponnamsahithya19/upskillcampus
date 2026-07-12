import java.time.LocalDateTime;

/**
 * Represents a Current Bank Account.
 * Inherits from BankAccount and overrides transaction behaviors.
 * Highlights the OOP concepts of Inheritance and Polymorphism.
 * 
 * Part of the Banking Information System Project.
 */
public class CurrentAccount extends BankAccount {
    public static final double OPENING_MINIMUM = 1000.0;

    /**
     * Parameterized Constructor.
     */
    public CurrentAccount(String accountNumber, Customer customer, double balance, LocalDateTime dateOfCreation) {
        super(accountNumber, customer, "Current", balance, dateOfCreation);
    }

    /**
     * Overrides withdraw to enforce that the balance cannot go below zero.
     */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidTransactionException {
        super.withdraw(amount);
        if (this.balance - amount < 0.0) {
            throw new InsufficientFundsException("Transaction Denied. Insufficient funds. Current Account balance cannot become negative.");
        }
        this.balance -= amount;
    }

    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.println("Overdraft Limit: Rs. 0.00 (No overdraft allowed)");
        System.out.println("============================================");
    }
}
