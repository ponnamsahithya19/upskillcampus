import java.time.LocalDateTime;

/**
 * Model representing a single banking transaction.
 * Stores transaction metadata, including status (SUCCESS/FAILED) and balance transitions.
 * Supports pipe-separated serialization for persistent file logging.
 * 
 * Part of the Banking Information System Project.
 */
public class Transaction {
    private static int transactionSequence = 100001;

    private final String transactionId;
    private final String accountNumber;
    private final LocalDateTime timestamp;
    private final String transactionType;
    private final double amount;
    private final double balanceBefore;
    private final double balanceAfter;
    private final String status;

    /**
     * Parameterized Constructor for generating a new transaction.
     */
    public Transaction(String accountNumber, String transactionType, double amount, double balanceBefore, double balanceAfter, String status) {
        this.transactionId = "TXN" + (transactionSequence++);
        this.accountNumber = accountNumber;
        this.timestamp = LocalDateTime.now();
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.status = status;
    }

    /**
     * Constructor used when loading transactions from file database.
     */
    public Transaction(String transactionId, String accountNumber, LocalDateTime timestamp, String transactionType, double amount, double balanceBefore, double balanceAfter, String status) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.status = status;

        // Keep sequence ahead of loaded IDs to prevent duplicates
        try {
            int currentIdVal = Integer.parseInt(transactionId.replace("TXN", ""));
            if (currentIdVal >= transactionSequence) {
                transactionSequence = currentIdVal + 1;
            }
        } catch (Exception e) {
            // Fallback in case of custom formats
        }
    }

    // Getters

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Serializes transaction to pipe-delimited format.
     */
    public String serialize() {
        return transactionId + "|" + accountNumber + "|" + timestamp.toString() + "|" + 
               transactionType + "|" + amount + "|" + balanceBefore + "|" + balanceAfter + "|" + status;
    }

    /**
     * Deserializes pipe-delimited line into Transaction.
     */
    public static Transaction deserialize(String data) {
        String[] parts = data.split("\\|");
        String id = parts[0];
        String accNum = parts[1];
        LocalDateTime ts = LocalDateTime.parse(parts[2]);
        String type = parts[3];
        double amt = Double.parseDouble(parts[4]);
        double balBefore = Double.parseDouble(parts[5]);
        double balAfter = Double.parseDouble(parts[6]);
        String stat = parts[7];
        return new Transaction(id, accNum, ts, type, amt, balBefore, balAfter, stat);
    }

    @Override
    public String toString() {
        return Utils.formatDateTime(timestamp) + " | " + 
               String.format("%-12s", transactionType) + " | " + 
               String.format("Amt: Rs. %-9.2f", amount) + " | " + 
               String.format("Prev: Rs. %-9.2f", balanceBefore) + " | " + 
               String.format("New: Rs. %-9.2f", balanceAfter) + " | " + 
               String.format("[%s]", status);
    }
}
