import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Utility class to auto-generate unique 10-digit account numbers.
 * Persists the running sequence to a sequence file to prevent duplicate IDs.
 * 
 * Part of the Banking Information System Project.
 */
public class AccountNumberGenerator {
    private static final String SEQUENCE_FILE = "acc_sequence.txt";
    private static final long START_SEQUENCE = 1000000001L;
    private static long currentSequence = START_SEQUENCE;

    // Load sequence value from file on class initialization
    static {
        loadSequence();
    }

    /**
     * Loads the last generated sequence number from the file.
     * Initializes to START_SEQUENCE if file is not found or is empty.
     */
    private static void loadSequence() {
        File file = new File(SEQUENCE_FILE);
        if (!file.exists()) {
            currentSequence = START_SEQUENCE;
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                currentSequence = Long.parseLong(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            currentSequence = START_SEQUENCE;
        }
    }

    /**
     * Saves the current sequence number to the file.
     */
    private static void saveSequence() {
        try (FileWriter writer = new FileWriter(SEQUENCE_FILE)) {
            writer.write(String.valueOf(currentSequence));
        } catch (IOException e) {
            System.err.println("[WARNING] Failed to save account number sequence: " + e.getMessage());
        }
    }

    /**
     * Generates a new, thread-safe, unique 10-digit account number.
     * 
     * @return Generated 10-digit account number as String
     */
    public static synchronized String generate() {
        String newAccountNumber = String.valueOf(currentSequence);
        currentSequence++;
        saveSequence();
        return newAccountNumber;
    }

    /**
     * Synchronizes sequence pointer to skip past any manually loaded file values
     * to avoid overlap issues.
     * 
     * @param loadedNumber An existing account number in system
     */
    public static synchronized void synchronize(String loadedNumber) {
        try {
            long val = Long.parseLong(loadedNumber);
            if (val >= currentSequence) {
                currentSequence = val + 1;
                saveSequence();
            }
        } catch (NumberFormatException e) {
            // Ignore if non-standard account format loaded
        }
    }
}
