# Enterprise Banking Information System (v2.0.0)

A file-persisted, modular, and secure command line banking portal built in Java. Designed and structured as a professional **Industrial Internship Project Report** suitable for **USC / UCT** academic evaluations.

---

## 📂 Project Structure

```text
BankingInformationSystem/
│
├── AccountOperations.java          # Core interface declaring transactional methods.
├── BankAccount.java                # Abstract parent class implementing AccountOperations.
├── SavingsAccount.java             # Subclass extending BankAccount with savings balance limits.
├── CurrentAccount.java             # Subclass extending BankAccount with commercial balance limits.
│
├── Customer.java                   # Model encapsulating personal, contact, and identity details.
├── Transaction.java                # Model logging transaction history (balance transitions, status).
│
├── SystemLockedException.java      # Custom exception thrown during security lockouts.
├── InsufficientFundsException.java # Custom exception thrown for overdrafts.
├── AccountNotFoundException.java   # Custom exception thrown for missing lookups.
├── InvalidTransactionException.java# Custom exception thrown for logical transaction errors.
│
├── AccountNumberGenerator.java     # Thread-safe utility persisting unique 10-digit generation sequences.
├── Validation.java                 # Format validators for Email, Phone, Aadhaar, and PAN.
├── FileManager.java                # IO utility parsing accounts/transactions CSV databases.
├── Admin.java                      # Administrative credential manager with change password capability.
├── Utils.java                      # UI helper formatting console loading cursors, status tables.
│
├── Bank.java                       # Main Controller managing accounts (Map) and transactions (List).
├── Main.java                       # Driver class managing console loops, inputs, and exceptions.
│
├── accounts.txt                    # Persistent database of registered accounts.
├── transactions.txt                # Persistent transactional ledger.
├── acc_sequence.txt                # Persistent account generation counter.
├── admin_pass.txt                  # Persistent encrypted admin password.
│
├── README.md                       # Installation manual and system details (this file).
└── Design_Documentation.md         # Full project report containing HLD, LLD, Diagrams, and 15 Test Cases.
```

---

## ⚡ Features & Capabilities

1. **Dashboard & Summary Report**: Shows total balances, sums of deposits/withdrawals, active customer counts, and system status metrics.
2. **Brute-Force Login Lockout**: Automatically locks the administrative panel if an invalid password is typed 3 consecutive times.
3. **Password Management**: Allows updating the administrative password and persists it dynamically in `admin_pass.txt`.
4. **Audit Trail Logging**: Stores transaction statuses (`SUCCESS` or `FAILED`) along with `balanceBefore` and `balanceAfter` transitions.
5. **Mini Statement Enquiry**: Outputs a list of the last 10 transactions for any account.
6. **Multi-Parameter Search**: Supports querying profiles by Account Number, Holder Name, Phone, or Aadhaar Card number.
7. **Simulated Console Delays**: Renders a spinning cursor in the terminal during database updates and logins.
8. **Regex Sanitation**: Validates formats for phone number lengths, Aadhaar formatting, PAN styling, and email syntax.

---

## ⚙️ Compilation & Running

Ensure you have Java SE JDK 8 or higher installed on your computer.

### Step 1: Open terminal in workspace
```cmd
cd d:\BankingInformationSystem.java
```

### Step 2: Compile the Code
```cmd
javac *.java
```

### Step 3: Run the main application
```cmd
java Main
```

---

## 🧠 OOP Architecture

* **Abstraction**: Enforced by the `AccountOperations` interface and abstract class `BankAccount`.
* **Encapsulation**: Demographics in `Customer` and balances in `BankAccount` are marked `private` or `protected` and modified via validation setters.
* **Inheritance**: `SavingsAccount` and `CurrentAccount` extend `BankAccount` to inherit common details while establishing subclass-specific balance boundaries.
* **Polymorphism**: The controller stores accounts in a polymorphic map `Map<String, BankAccount>`. At runtime, operations automatically invoke overridden methods in `SavingsAccount` or `CurrentAccount`.
