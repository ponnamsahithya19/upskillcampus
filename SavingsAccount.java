import java.time.LocalDateTime;

/**
 * Represents a Savings Bank Account.
 * Inherits from BankAccount and overrides transaction behaviors.
 * Highlights the OOP concepts of Inheritance and Polymorphism.
 * 
 * Part of the Banking Information System Project.
 */
public class SavingsAccount extends BankAccount {
    public static final double MINIMUM_BALANCE = 1000.0;
    public static final double INTEREST_RATE = 0.04; // 4% P.A.

    /**
     * Parameterized Constructor.
     */
    public SavingsAccount(String accountNumber, Customer customer, double balance, LocalDateTime dateOfCreation) {
        super(accountNumber, customer, "Savings", balance, dateOfCreation);
    }

    /**
     * Overrides withdraw to enforce the minimum balance rule of Savings Account.
     */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidTransactionException {
        super.withdraw(amount);
        if (this.balance - amount < MINIMUM_BALANCE) {
            throw new InsufficientFundsException("Transaction Denied. Savings Account must maintain a minimum balance of Rs. " + MINIMUM_BALANCE);
        }
        this.balance -= amount;
    }

    /**
     * Calculates yearly interest based on the current balance.
     * 
     * @return Calculated yearly interest amount
     */
    public double calculateYearlyInterest() {
        return this.balance * INTEREST_RATE;
    }

    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.println("Interest Rate  : " + (INTEREST_RATE * 100) + "% P.A.");
        System.out.println("Yearly Interest: Rs. " + String.format("%.2f", calculateYearlyInterest()));
        System.out.println("============================================");
    }
}
