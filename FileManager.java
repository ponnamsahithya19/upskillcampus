import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles persistent file storage operations for accounts and transactions.
 * Protects data integrity using standard stream writing.
 * 
 * Part of the Banking Information System Project.
 */
public class FileManager {
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";

    /**
     * Saves all bank accounts to accounts.txt.
     * 
     * @param accounts Map of active accounts
     */
    public static void saveAccounts(Map<String, BankAccount> accounts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (BankAccount acc : accounts.values()) {
                String customerData = acc.getCustomer().serialize();
                String line = acc.getAccountNumber() + ";" + 
                             acc.getAccountType() + ";" + 
                             acc.getBalance() + ";" + 
                             acc.getDateOfCreation().toString() + ";" + 
                             customerData;
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save accounts to file: " + e.getMessage());
        }
    }

    /**
     * Loads bank accounts from accounts.txt.
     * 
     * @return Map of loaded BankAccounts
     */
    public static Map<String, BankAccount> loadAccounts() {
        Map<String, BankAccount> accounts = new HashMap<>();
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) {
            return accounts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length < 5) continue;

                String accNum = parts[0];
                String type = parts[1];
                double balance = Double.parseDouble(parts[2]);
                LocalDateTime creationDate = LocalDateTime.parse(parts[3]);
                Customer customer = Customer.deserialize(parts[4]);

                BankAccount account;
                if (type.equalsIgnoreCase("Savings")) {
                    account = new SavingsAccount(accNum, customer, balance, creationDate);
                } else {
                    account = new CurrentAccount(accNum, customer, balance, creationDate);
                }

                accounts.put(accNum, account);
                // Synchronize Account Number Generator
                AccountNumberGenerator.synchronize(accNum);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("[WARNING] Error loading accounts database: " + e.getMessage());
        }
        return accounts;
    }

    /**
     * Saves all transactions to transactions.txt.
     * 
     * @param transactions List of transactions
     */
    public static void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction txn : transactions) {
                writer.write(txn.serialize());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save transactions to file: " + e.getMessage());
        }
    }

    /**
     * Loads transactions from transactions.txt.
     */
    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                transactions.add(Transaction.deserialize(line));
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("[WARNING] Error loading transaction logs: " + e.getMessage());
        }
        return transactions;
    }
}
