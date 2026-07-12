import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Driver console class for the Banking Information System.
 * Coordinates input validation, UI alerts, delays, and admin lockdowns.
 * 
 * Part of the Banking Information System Project.
 */
public class Main {
    private static Bank bank = new Bank();
    private static Scanner scanner = new Scanner(System.in);
    private static boolean isAdminLoggedIn = false;
    private static final String APP_VERSION = "v2.0.0 (Enterprise-Build)";

    public static void main(String[] args) {
        boolean exit = false;
        
        // Print Versioned Welcome Banner
        System.out.println("=================================================================");
        System.out.println("     WELCOME TO THE ENTERPRISE BANKING INFORMATION SYSTEM        ");
        System.out.println("     Version: " + APP_VERSION);
        System.out.println("     System Init Time: " + Utils.formatDateTime(LocalDateTime.now()));
        System.out.println("=================================================================");

        while (!exit) {
            printMenu();
            System.out.print("Select choice (1-16): ");
            int choice = readChoice();

            try {
                switch (choice) {
                    case 1:
                        adminLogin();
                        break;
                    case 2:
                        createNewAccount();
                        break;
                    case 3:
                        depositMoney();
                        break;
                    case 4:
                        withdrawMoney();
                        break;
                    case 5:
                        transferMoney();
                        break;
                    case 6:
                        checkBalance();
                        break;
                    case 7:
                        searchAccount();
                        break;
                    case 8:
                        displayAllAccountsAdmin();
                        break;
                    case 9:
                        viewTransactionHistory();
                        break;
                    case 10:
                        updateCustomerDetails();
                        break;
                    case 11:
                        deleteAccountAdmin();
                        break;
                    case 12:
                        calculateInterest();
                        break;
                    case 13:
                        viewMiniStatement();
                        break;
                    case 14:
                        viewDashboardSummary();
                        break;
                    case 15:
                        adminManagementSubmenu();
                        break;
                    case 16:
                        exit = true;
                        Utils.printLoading("Saving database blocks to HDD...");
                        bank.saveData();
                        Utils.printSuccess("Database blocks successfully persisted. System offline.");
                        break;
                    default:
                        Utils.printWarning("Invalid selection. Please choose an option between 1 and 16.");
                }
            } catch (SystemLockedException e) {
                Utils.printError("SECURITY LOCKDOWN: " + e.getMessage());
            } catch (Exception e) {
                Utils.printError("Operation failed: " + e.getMessage());
            }
            
            if (!exit) {
                System.out.println("\nPress Enter to return to the Main Menu...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    /**
     * Prints the primary application dashboard, version, and session state.
     */
    private static void printMenu() {
        String adminStatus = isAdminLoggedIn ? "ACTIVE (ADMIN)" : (Admin.isLocked() ? "LOCKED (BLOCKED)" : "GUEST");
        System.out.println("\n------------------------- SYSTEM DASHBOARD -------------------------");
        System.out.println("  Time: " + Utils.formatDateTime(LocalDateTime.now()) + " | Security Mode: " + adminStatus);
        System.out.println("--------------------------------------------------------------------");
        System.out.println("  1. Admin Login                  9. Transaction History");
        System.out.println("  2. Create New Bank Account      10. Update Customer Details");
        System.out.println("  3. Deposit Money                11. Delete Account (Admin)");
        System.out.println("  4. Withdraw Money               12. Interest Calculator (Savings)");
        System.out.println("  5. Transfer Money               13. Mini Statement (Last 10)");
        System.out.println("  6. Balance Enquiry              14. Bank Dashboard Summary");
        System.out.println("  7. Search Account               15. Admin Security Management");
        System.out.println("  8. Display All Accounts (Admin) 16. Save & Exit");
        System.out.println("--------------------------------------------------------------------");
    }

    private static int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Authenticates administrative login. Traps locked status.
     */
    private static void adminLogin() throws SystemLockedException {
        Utils.printHeader("Admin Authentication Portal");
        if (Admin.isLocked()) {
            throw new SystemLockedException("Security lockout active. Administrative actions are disabled.");
        }
        System.out.print("Enter Admin Username: ");
        String user = scanner.nextLine().trim();
        System.out.print("Enter Admin Password: ");
        String pass = scanner.nextLine().trim();

        Utils.printLoading("Checking authorizations...");
        if (Admin.login(user, pass)) {
            isAdminLoggedIn = true;
            Utils.printSuccess("Access Granted. Administrative privileges enabled.");
        } else {
            int remaining = 3 - Admin.getFailedAttempts();
            Utils.printWarning("Invalid Credentials. Attempts remaining: " + remaining);
        }
    }

    /**
     * Creates new bank accounts.
     */
    private static void createNewAccount() throws Exception {
        Utils.printHeader("Create New Bank Account");
        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Customer Age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Age must be a valid integer.");
        }

        System.out.print("Enter Gender (Male/Female/Other): ");
        String gender = scanner.nextLine().trim();

        System.out.print("Enter Phone Number (10 digits): ");
        String phone = scanner.nextLine().trim();

        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Enter Aadhaar Number (12 digits): ");
        String aadhaar = scanner.nextLine().trim();

        System.out.print("Enter PAN Card Number (10 characters): ");
        String pan = scanner.nextLine().trim();

        System.out.print("Enter Account Type (Savings/Current): ");
        String type = scanner.nextLine().trim();

        System.out.print("Enter Initial Deposit (Min: Rs.1000): ");
        double deposit = readDouble();

        Utils.printLoading("Verifying specifications and generating account keys...");
        BankAccount acc = bank.createAccount(name, age, gender, phone, email, address, aadhaar, pan, type, deposit);
        
        Utils.printSuccess("Account successfully created!");
        System.out.println("Assigned Account Number: " + acc.getAccountNumber());
    }

    private static void depositMoney() throws Exception {
        Utils.printHeader("Deposit Money");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        System.out.print("Enter Amount to Deposit: ");
        double amt = readDouble();

        Utils.printLoading("Verifying ledger tables...");
        bank.deposit(accNum, amt);
        Utils.printSuccess("Deposit of " + Utils.formatCurrency(amt) + " successfully completed.");
    }

    private static void withdrawMoney() throws Exception {
        Utils.printHeader("Withdraw Money");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        System.out.print("Enter Amount to Withdraw: ");
        double amt = readDouble();

        Utils.printLoading("Checking liquidity constraints...");
        bank.withdraw(accNum, amt);
        Utils.printSuccess("Withdrawal of " + Utils.formatCurrency(amt) + " successfully completed.");
    }

    private static void transferMoney() throws Exception {
        Utils.printHeader("Transfer Money");
        System.out.print("Enter Sender Account Number: ");
        String from = scanner.nextLine().trim();
        System.out.print("Enter Target Account Number: ");
        String to = scanner.nextLine().trim();
        System.out.print("Enter Amount to Transfer: ");
        double amt = readDouble();

        Utils.printLoading("Coordinating transactional balances...");
        bank.transfer(from, to, amt);
        Utils.printSuccess("Transferred " + Utils.formatCurrency(amt) + " successfully from " + from + " to " + to);
    }

    private static void checkBalance() throws Exception {
        Utils.printHeader("Balance Enquiry");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();

        Utils.printLoading("Querying account metadata...");
        BankAccount acc = bank.findAccount(accNum);
        System.out.println("\n---------------------------------------------");
        System.out.println("  Account Number : " + acc.getAccountNumber());
        System.out.println("  Holder Name    : " + acc.getCustomer().getName());
        System.out.println("  Account Type   : " + acc.getAccountType());
        System.out.println("  Current Balance: " + Utils.formatCurrency(acc.getBalance()));
        System.out.println("---------------------------------------------");
    }

    private static void searchAccount() {
        Utils.printHeader("Search Account Database");
        System.out.println("Criteria options:");
        System.out.println("  1. Search by Account Number");
        System.out.println("  2. Search by Customer Name");
        System.out.println("  3. Search by Phone Number");
        System.out.println("  4. Search by Aadhaar Number");
        System.out.print("Enter choice (1-4): ");
        int choice = readChoice();

        String type = "";
        if (choice == 1) type = "Number";
        else if (choice == 2) type = "Name";
        else if (choice == 3) type = "Phone";
        else if (choice == 4) type = "Aadhaar";
        else {
            Utils.printWarning("Invalid criteria chosen.");
            return;
        }

        System.out.print("Enter query: ");
        String query = scanner.nextLine().trim();

        Utils.printLoading("Querying databases...");
        List<BankAccount> results = bank.searchAccounts(query, type);
        if (results.isEmpty()) {
            System.out.println("\n[INFO] No accounts matched your search.");
        } else {
            System.out.println("\n--- Search Results (" + results.size() + " matches) ---");
            for (BankAccount acc : results) {
                acc.displayAccountInfo();
                System.out.println();
            }
        }
    }

    private static void displayAllAccountsAdmin() {
        if (!isAdminLoggedIn) {
            Utils.printError("Access Denied. Administrative authentication required. Option 1 to authorize.");
            return;
        }
        Utils.printHeader("All Registered Bank Accounts");
        bank.displayAllAccounts();
    }

    private static void viewTransactionHistory() throws Exception {
        Utils.printHeader("Transaction History");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        bank.displayTransactionHistory(accNum);
    }

    private static void updateCustomerDetails() throws Exception {
        Utils.printHeader("Update Customer Details");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();

        // Verify account exists
        BankAccount acc = bank.findAccount(accNum);

        boolean exitUpdate = false;
        while (!exitUpdate) {
            System.out.println("\n----------------- UPDATE PROFILE FIELD -----------------");
            System.out.println("  Account: " + acc.getAccountNumber() + " (" + acc.getCustomer().getName() + ")");
            System.out.println("  1. Update Customer Name          5. Update Email Address");
            System.out.println("  2. Update Customer Age           6. Update Residential Address");
            System.out.println("  3. Update Customer Gender        7. Update Aadhaar Card Number");
            System.out.println("  4. Update Phone Number           8. Update PAN Card Number");
            System.out.println("  9. Exit Update Submenu");
            System.out.println("--------------------------------------------------------");
            System.out.print("Select field to update (1-9): ");
            int option = readChoice();

            String field = "";
            String label = "";
            switch (option) {
                case 1: field = "name"; label = "Customer Name"; break;
                case 2: field = "age"; label = "Customer Age"; break;
                case 3: field = "gender"; label = "Customer Gender"; break;
                case 4: field = "phone"; label = "Phone Number (10 digits)"; break;
                case 5: field = "email"; label = "Email Address"; break;
                case 6: field = "address"; label = "Residential Address"; break;
                case 7: field = "aadhaar"; label = "Aadhaar Card Number (12 digits)"; break;
                case 8: field = "pan"; label = "PAN Card Number (10 characters)"; break;
                case 9:
                    exitUpdate = true;
                    System.out.println("Exiting profile update wizard.");
                    continue;
                default:
                    Utils.printWarning("Invalid selection. Choose 1-9.");
                    continue;
            }

            System.out.print("Enter new value for " + label + ": ");
            String newValue = scanner.nextLine().trim();

            try {
                Utils.printLoading("Committing updates and validating values...");
                bank.updateCustomerField(accNum, field, newValue);
                Utils.printSuccess(label + " successfully updated.");
            } catch (Exception e) {
                Utils.printError("Failed to update " + label + ": " + e.getMessage());
            }
        }
    }

    private static void deleteAccountAdmin() throws Exception {
        if (!isAdminLoggedIn) {
            Utils.printError("Access Denied. Administrative authentication required.");
            return;
        }

        Utils.printHeader("Delete Customer Account");
        System.out.print("Enter Account Number to Delete: ");
        String accNum = scanner.nextLine().trim();

        BankAccount acc = bank.findAccount(accNum);
        System.out.println("Warning: This will permanently delete the account owned by " + acc.getCustomer().getName());
        System.out.print("Are you sure you want to proceed? (yes/no): ");
        String confirmation = scanner.nextLine().trim();

        if (confirmation.equalsIgnoreCase("yes")) {
            Utils.printLoading("Removing keys and adjusting arrays...");
            bank.deleteAccount(accNum);
            Utils.printSuccess("Account " + accNum + " successfully deleted.");
        } else {
            System.out.println("\n[INFO] Deletion cancelled.");
        }
    }

    private static void calculateInterest() throws Exception {
        Utils.printHeader("Savings Account Interest Calculator");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();

        double yearlyInterest = bank.estimateSavingsInterest(accNum);
        System.out.println("\n---------------------------------------------");
        System.out.println("  Savings Account: " + accNum);
        System.out.println("  Annual Interest Rate: 4.0% P.A.");
        System.out.println("  Estimated Yearly Interest: " + Utils.formatCurrency(yearlyInterest));
        System.out.println("---------------------------------------------");
    }

    private static void viewMiniStatement() throws Exception {
        Utils.printHeader("Mini Statement Enquiry");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        bank.displayMiniStatement(accNum);
    }

    /**
     * Renders aggregate metrics dashboard.
     */
    private static void viewDashboardSummary() {
        bank.generateBankSummaryReport();
    }

    /**
     * Submenu handling passwords, logouts, reports, lock resets.
     */
    private static void adminManagementSubmenu() {
        Utils.printHeader("Admin Security & Reports Management");
        System.out.println("  1. Change Admin Password");
        System.out.println("  2. Reset Intruder Lock status (Unlock CLI)");
        System.out.println("  3. Generate Accounts Report (Admin)");
        System.out.println("  4. Generate Transactions Ledger Report (Admin)");
        System.out.println("  5. Logout Admin");
        System.out.print("Select admin sub-choice (1-5): ");
        int choice = readChoice();

        switch (choice) {
            case 1:
                System.out.print("Enter current password: ");
                String oldPass = scanner.nextLine().trim();
                System.out.print("Enter new password: ");
                String newPass = scanner.nextLine().trim();
                Utils.printLoading("Saving password changes...");
                if (Admin.changePassword(oldPass, newPass)) {
                    Utils.printSuccess("Password updated successfully.");
                } else {
                    Utils.printError("Failed to update password. Invalid old password.");
                }
                break;
            case 2:
                Utils.printLoading("Unlocking system blocks...");
                Admin.unlock();
                Utils.printSuccess("Administrative lock counts reset successfully.");
                break;
            case 3:
                if (!isAdminLoggedIn) {
                    Utils.printError("Administrative access required.");
                } else {
                    bank.generateAccountsReport();
                }
                break;
            case 4:
                if (!isAdminLoggedIn) {
                    Utils.printError("Administrative access required.");
                } else {
                    bank.generateTransactionsReport();
                }
                break;
            case 5:
                if (isAdminLoggedIn) {
                    isAdminLoggedIn = false;
                    Utils.printSuccess("Admin logged out successfully.");
                } else {
                    System.out.println("\n[INFO] No active admin session found.");
                }
                break;
            default:
                System.out.println("[WARNING] Invalid selection.");
        }
    }

    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid format. Enter a valid decimal: ");
            }
        }
    }
}
