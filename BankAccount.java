import java.time.LocalDateTime;

/**
 * Abstract class representing a generic bank account.
 * Implements the AccountOperations contract.
 * Highlights the OOP concepts of Inheritance, Encapsulation, and Abstraction.
 * 
 * Part of the Banking Information System Project.
 */
public abstract class BankAccount implements AccountOperations {
    private final String accountNumber;
    private Customer customer;
    protected double balance;
    private final String accountType;
    private final LocalDateTime dateOfCreation;

    /**
     * Parameterized Constructor to initialize account details.
     */
    protected BankAccount(String accountNumber, Customer customer, String accountType, double balance, LocalDateTime dateOfCreation) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.accountType = accountType;
        this.balance = balance;
        this.dateOfCreation = dateOfCreation;
    }

    // Getters and Setters

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public LocalDateTime getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     * Base implementation of deposit. Validates that the amount is positive.
     */
    @Override
    public void deposit(double amount) throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Deposit amount must be greater than zero.");
        }
        this.balance += amount;
    }

    /**
     * Base implementation of withdraw. Validates that the amount is positive.
     * Actual balance limit constraints are overridden in subclasses.
     */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be greater than zero.");
        }
    }

    /**
     * Polymorphic method to display structured account information.
     */
    public void displayAccountInfo() {
        System.out.println("============================================");
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Account Type   : " + accountType);
        System.out.println("Current Balance: Rs. " + String.format("%.2f", balance));
        System.out.println("Created Date   : " + Utils.formatDateTime(dateOfCreation));
        System.out.println("--------------------------------------------");
        System.out.println(customer.toString());
        System.out.println("============================================");
    }
}
