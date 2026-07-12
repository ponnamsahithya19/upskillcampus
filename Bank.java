import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main Controller representing the bank business logic.
 * Manages accounts in a HashMap and transactions in an ArrayList.
 * Performs account management, reports generation, and transaction logging.
 * 
 * Part of the Banking Information System Project.
 */
public class Bank {
    private Map<String, BankAccount> accounts;
    private List<Transaction> transactions;

    /**
     * Constructor initializing collections and loading persistent file backups.
     */
    public Bank() {
        this.accounts = new HashMap<>();
        this.transactions = new ArrayList<>();
        loadData();
    }

    public final void loadData() {
        this.accounts = FileManager.loadAccounts();
        this.transactions = FileManager.loadTransactions();
    }

    public final void saveData() {
        FileManager.saveAccounts(this.accounts);
        FileManager.saveTransactions(this.transactions);
    }

    /**
     * Creates a new bank account. Logs opening deposit transaction.
     */
    public BankAccount createAccount(String name, int age, String gender, String phone, String email, 
                                     String address, String aadhaar, String pan, String type, 
                                     double initialDeposit) throws InvalidTransactionException {
        // Enforce validations
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (age < 18) {
            throw new IllegalArgumentException("Customer must be at least 18 years old to open an account.");
        }
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be empty.");
        }
        if (!Validation.isValidPhone(phone)) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        if (!Validation.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer address cannot be empty.");
        }
        if (!Validation.isValidAadhaar(aadhaar)) {
            throw new IllegalArgumentException("Aadhaar card number must be exactly 12 digits.");
        }
        if (!Validation.isValidPAN(pan)) {
            throw new IllegalArgumentException("Invalid PAN card format.");
        }
        if (initialDeposit < 1000.0) {
            throw new InvalidTransactionException("Opening balance must be at least Rs. 1,000.00.");
        }

        String accNum = AccountNumberGenerator.generate();
        Customer customer = new Customer(name.trim(), age, gender.trim(), phone.trim(), email.trim(), address.trim(), aadhaar.trim(), pan.trim().toUpperCase());

        BankAccount account;
        if (type.equalsIgnoreCase("Savings")) {
            account = new SavingsAccount(accNum, customer, initialDeposit, LocalDateTime.now());
        } else if (type.equalsIgnoreCase("Current")) {
            account = new CurrentAccount(accNum, customer, initialDeposit, LocalDateTime.now());
        } else {
            throw new IllegalArgumentException("Invalid account type.");
        }

        accounts.put(accNum, account);

        // Record successful creation transaction
        Transaction txn = new Transaction(accNum, "OPENING_DEP", initialDeposit, 0.0, initialDeposit, "SUCCESS");
        transactions.add(txn);

        saveData();
        return account;
    }

    public BankAccount findAccount(String accountNumber) throws AccountNotFoundException {
        BankAccount acc = accounts.get(accountNumber.trim());
        if (acc == null) {
            throw new AccountNotFoundException("Account number " + accountNumber + " does not exist.");
        }
        return acc;
    }

    /**
     * Deposits money. Handles transaction logging.
     */
    public void deposit(String accountNumber, double amount) throws AccountNotFoundException, InvalidTransactionException {
        BankAccount acc = findAccount(accountNumber);
        double balanceBefore = acc.getBalance();
        
        try {
            acc.deposit(amount);
            Transaction txn = new Transaction(acc.getAccountNumber(), "DEPOSIT", amount, balanceBefore, acc.getBalance(), "SUCCESS");
            transactions.add(txn);
            saveData();
        } catch (InvalidTransactionException e) {
            Transaction txn = new Transaction(acc.getAccountNumber(), "DEPOSIT", amount, balanceBefore, balanceBefore, "FAILED");
            transactions.add(txn);
            saveData();
            throw e;
        }
    }

    /**
     * Withdraws money. Handles audit failures.
     */
    public void withdraw(String accountNumber, double amount) 
            throws AccountNotFoundException, InsufficientFundsException, InvalidTransactionException {
        BankAccount acc = findAccount(accountNumber);
        double balanceBefore = acc.getBalance();

        try {
            acc.withdraw(amount);
            Transaction txn = new Transaction(acc.getAccountNumber(), "WITHDRAWAL", amount, balanceBefore, acc.getBalance(), "SUCCESS");
            transactions.add(txn);
            saveData();
        } catch (InsufficientFundsException | InvalidTransactionException e) {
            Transaction txn = new Transaction(acc.getAccountNumber(), "WITHDRAWAL", amount, balanceBefore, balanceBefore, "FAILED");
            transactions.add(txn);
            saveData();
            throw e;
        }
    }

    /**
     * Transfers money transactionally.
     */
    public void transfer(String fromAccNum, String toAccNum, double amount) 
            throws AccountNotFoundException, InsufficientFundsException, InvalidTransactionException {
        if (fromAccNum.trim().equalsIgnoreCase(toAccNum.trim())) {
            throw new InvalidTransactionException("Source and destination accounts cannot be the same.");
        }
        if (amount <= 0) {
            throw new InvalidTransactionException("Transfer amount must be greater than zero.");
        }

        BankAccount source = findAccount(fromAccNum);
        BankAccount destination = findAccount(toAccNum);

        double srcBalBefore = source.getBalance();
        double destBalBefore = destination.getBalance();

        double limit = (source instanceof SavingsAccount) ? SavingsAccount.MINIMUM_BALANCE : 0.0;
        if (source.getBalance() - amount < limit) {
            // Log failure
            Transaction txnFail = new Transaction(source.getAccountNumber(), "TRANSFER_OUT", amount, srcBalBefore, srcBalBefore, "FAILED");
            transactions.add(txnFail);
            saveData();
            throw new InsufficientFundsException("Transfer failed: Insufficient funds. Sender must maintain Rs. " + limit);
        }

        try {
            source.withdraw(amount);
            destination.deposit(amount);

            Transaction txnOut = new Transaction(source.getAccountNumber(), "TRANSFER_OUT", amount, srcBalBefore, source.getBalance(), "SUCCESS");
            Transaction txnIn = new Transaction(destination.getAccountNumber(), "TRANSFER_IN", amount, destBalBefore, destination.getBalance(), "SUCCESS");
            transactions.add(txnOut);
            transactions.add(txnIn);
            saveData();
        } catch (InsufficientFundsException | InvalidTransactionException e) {
            Transaction txnOut = new Transaction(source.getAccountNumber(), "TRANSFER_OUT", amount, srcBalBefore, srcBalBefore, "FAILED");
            transactions.add(txnOut);
            saveData();
            throw e;
        }
    }

    public void updateCustomerField(String accountNumber, String field, String newValue) throws AccountNotFoundException {
        BankAccount acc = findAccount(accountNumber);
        Customer customer = acc.getCustomer();
        String val = (newValue == null) ? "" : newValue.trim();

        switch (field.toLowerCase()) {
            case "name":
                if (val.isEmpty()) {
                    throw new IllegalArgumentException("Customer name cannot be empty.");
                }
                customer.setName(val);
                break;
            case "age":
                try {
                    int age = Integer.parseInt(val);
                    if (age < 18) {
                        throw new IllegalArgumentException("Customer must be at least 18 years old.");
                    }
                    customer.setAge(age);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Age must be a valid integer.");
                }
                break;
            case "gender":
                if (val.isEmpty()) {
                    throw new IllegalArgumentException("Gender cannot be empty.");
                }
                customer.setGender(val);
                break;
            case "phone":
                if (!Validation.isValidPhone(val)) {
                    throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
                }
                customer.setPhoneNumber(val);
                break;
            case "email":
                if (!Validation.isValidEmail(val)) {
                    throw new IllegalArgumentException("Invalid email format.");
                }
                customer.setEmail(val);
                break;
            case "address":
                if (val.isEmpty()) {
                    throw new IllegalArgumentException("Address cannot be empty.");
                }
                customer.setAddress(val);
                break;
            case "aadhaar":
                if (!Validation.isValidAadhaar(val)) {
                    throw new IllegalArgumentException("Aadhaar card number must be exactly 12 digits.");
                }
                customer.setAadhaarNumber(val);
                break;
            case "pan":
                if (!Validation.isValidPAN(val)) {
                    throw new IllegalArgumentException("Invalid PAN card format.");
                }
                customer.setPanNumber(val.toUpperCase());
                break;
            default:
                throw new IllegalArgumentException("Invalid field specified for update.");
        }
        saveData();
    }

    public void deleteAccount(String accountNumber) throws AccountNotFoundException {
        BankAccount acc = findAccount(accountNumber);
        accounts.remove(acc.getAccountNumber());
        saveData();
    }

    /**
     * Search account by Number, Name, Phone, or Aadhaar.
     */
    public List<BankAccount> searchAccounts(String query, String searchBy) {
        List<BankAccount> results = new ArrayList<>();
        String normalizedQuery = query.trim().toLowerCase();

        for (BankAccount acc : accounts.values()) {
            if (searchBy.equalsIgnoreCase("Number") && acc.getAccountNumber().equals(query.trim())) {
                results.add(acc);
            } else if (searchBy.equalsIgnoreCase("Name") && acc.getCustomer().getName().toLowerCase().contains(normalizedQuery)) {
                results.add(acc);
            } else if (searchBy.equalsIgnoreCase("Phone") && acc.getCustomer().getPhoneNumber().equals(query.trim())) {
                results.add(acc);
            } else if (searchBy.equalsIgnoreCase("Aadhaar") && acc.getCustomer().getAadhaarNumber().equals(query.trim())) {
                results.add(acc);
            }
        }
        return results;
    }

    /**
     * Displays all accounts.
     */
    public void displayAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("\nNo accounts registered in the database.");
            return;
        }

        System.out.println("\n---------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-15s | %-20s | %-12s | %-15s | %-12s | %-15s |\n", "Account No", "Customer Name", "Type", "Balance", "Age", "Phone");
        System.out.println("---------------------------------------------------------------------------------------------------------------------");
        for (BankAccount acc : accounts.values()) {
            System.out.printf("| %-15s | %-20s | %-12s | %-15s | %-12d | %-15s |\n", 
                    acc.getAccountNumber(), 
                    acc.getCustomer().getName(), 
                    acc.getAccountType(), 
                    Utils.formatCurrency(acc.getBalance()),
                    acc.getCustomer().getAge(),
                    acc.getCustomer().getPhoneNumber());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Prints complete transaction logs.
     */
    public void displayTransactionHistory(String accountNumber) throws AccountNotFoundException {
        findAccount(accountNumber);
        System.out.println("\n--- TRANSACTION HISTORY FOR ACCOUNT: " + accountNumber + " ---");
        boolean found = false;
        for (Transaction txn : transactions) {
            if (txn.getAccountNumber().equalsIgnoreCase(accountNumber.trim())) {
                System.out.println(txn.toString());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No transactions found.");
        }
    }

    /**
     * Mini statement of last 10 entries.
     */
    public void displayMiniStatement(String accountNumber) throws AccountNotFoundException {
        findAccount(accountNumber);
        System.out.println("\n--- MINI STATEMENT FOR ACCOUNT: " + accountNumber + " (LAST 10 TRANSACTIONS) ---");
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction txn : transactions) {
            if (txn.getAccountNumber().equalsIgnoreCase(accountNumber.trim())) {
                filtered.add(txn);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("No transactions logged.");
            return;
        }

        int start = Math.max(0, filtered.size() - 10);
        for (int i = start; i < filtered.size(); i++) {
            System.out.println(filtered.get(i).toString());
        }
    }

    public double estimateSavingsInterest(String accountNumber) throws AccountNotFoundException, InvalidTransactionException {
        BankAccount acc = findAccount(accountNumber);
        if (!(acc instanceof SavingsAccount)) {
            throw new InvalidTransactionException("Interest calculation is only applicable for Savings Accounts.");
        }
        return ((SavingsAccount) acc).calculateYearlyInterest();
    }

    // Reports and Dashboard Stats Generators

    public int getAccountsCount() {
        return accounts.size();
    }

    public double getTotalBalancesSum() {
        double sum = 0;
        for (BankAccount acc : accounts.values()) {
            sum += acc.getBalance();
        }
        return sum;
    }

    public int getActiveCustomersCount() {
        Set<String> uniqueCustomers = new HashSet<>();
        for (BankAccount acc : accounts.values()) {
            uniqueCustomers.add(acc.getCustomer().getAadhaarNumber());
        }
        return uniqueCustomers.size();
    }

    public int getTransactionsCount() {
        return transactions.size();
    }

    public double getTotalDeposits() {
        double sum = 0;
        for (Transaction txn : transactions) {
            if (txn.getStatus().equals("SUCCESS") && 
               (txn.getTransactionType().equals("DEPOSIT") || txn.getTransactionType().equals("OPENING_DEP") || txn.getTransactionType().equals("TRANSFER_IN"))) {
                sum += txn.getAmount();
            }
        }
        return sum;
    }

    public double getTotalWithdrawals() {
        double sum = 0;
        for (Transaction txn : transactions) {
            if (txn.getStatus().equals("SUCCESS") && 
               (txn.getTransactionType().equals("WITHDRAWAL") || txn.getTransactionType().equals("TRANSFER_OUT"))) {
                sum += txn.getAmount();
            }
        }
        return sum;
    }

    public String getSystemStatus() {
        File accountsFile = new File("accounts.txt");
        if (accountsFile.exists() && !accountsFile.canWrite()) {
            return "DATABASE READ-ONLY (WARNING)";
        }
        return "HEALTHY (ONLINE)";
    }

    /**
     * Reports generation: Bank Summary Report
     */
    public void generateBankSummaryReport() {
        Utils.printHeader("Bank System Summary Report");
        System.out.println("System Date & Time : " + Utils.formatDateTime(LocalDateTime.now()));
        System.out.println("System Health      : " + getSystemStatus());
        System.out.println("Total Accounts     : " + getAccountsCount());
        System.out.println("Active Customers   : " + getActiveCustomersCount());
        System.out.println("Total Balance Pool : " + Utils.formatCurrency(getTotalBalancesSum()));
        System.out.println("Total Txn Log Count: " + getTransactionsCount());
        System.out.println("Sum of Deposits    : " + Utils.formatCurrency(getTotalDeposits()));
        System.out.println("Sum of Withdrawals : " + Utils.formatCurrency(getTotalWithdrawals()));
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Reports generation: Total Accounts Report
     */
    public void generateAccountsReport() {
        Utils.printHeader("Total Accounts Detailed Report");
        if (accounts.isEmpty()) {
            System.out.println("No accounts registered.");
            return;
        }
        for (BankAccount acc : accounts.values()) {
            System.out.println("Acc Number : " + acc.getAccountNumber() + " | Type: " + acc.getAccountType());
            System.out.println("Holder Name: " + acc.getCustomer().getName() + " | Phone: " + acc.getCustomer().getPhoneNumber());
            System.out.println("Aadhaar ID : " + acc.getCustomer().getAadhaarNumber() + " | PAN: " + acc.getCustomer().getPanNumber());
            System.out.println("Balance    : " + Utils.formatCurrency(acc.getBalance()) + " | Opened: " + Utils.formatDateTime(acc.getDateOfCreation()));
            System.out.println("-----------------------------------------------------------------");
        }
    }

    /**
     * Reports generation: Total Transactions Report
     */
    public void generateTransactionsReport() {
        Utils.printHeader("Total Transactions Log Report");
        if (transactions.isEmpty()) {
            System.out.println("No transactions logged.");
            return;
        }
        System.out.printf("%-10s | %-12s | %-12s | %-12s | %-12s | %-10s\n", "Txn ID", "Acc Number", "Type", "Amount", "Balance After", "Status");
        System.out.println("--------------------------------------------------------------------------------");
        for (Transaction txn : transactions) {
            System.out.printf("%-10s | %-12s | %-12s | %-12s | %-12s | %-10s\n",
                    txn.getTransactionId(),
                    txn.getAccountNumber(),
                    txn.getTransactionType(),
                    Utils.formatCurrency(txn.getAmount()),
                    Utils.formatCurrency(txn.getBalanceAfter()),
                    txn.getStatus());
        }
    }
}
