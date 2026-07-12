import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Handles Administrator security, login locks, and password changes.
 * Locks the admin panel after 3 consecutive failed login attempts.
 * 
 * Part of the Banking Information System Project.
 */
public class Admin {
    private static final String ADMIN_USER = "admin";
    private static final String PASSWORD_FILE = "admin_pass.txt";
    private static final String DEFAULT_PASS = "admin123";
    
    private static String adminPassword = DEFAULT_PASS;
    private static int failedAttempts = 0;
    private static boolean isLocked = false;

    static {
        loadPassword();
    }

    /**
     * Loads the admin password from file, falling back to default if not found.
     */
    private static void loadPassword() {
        File file = new File(PASSWORD_FILE);
        if (!file.exists()) {
            adminPassword = DEFAULT_PASS;
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String pass = reader.readLine();
            if (pass != null && !pass.trim().isEmpty()) {
                adminPassword = pass.trim();
            }
        } catch (IOException e) {
            adminPassword = DEFAULT_PASS;
        }
    }

    /**
     * Saves the new admin password to the text file.
     * 
     * @param password New password to save
     */
    private static void savePassword(String password) {
        try (FileWriter writer = new FileWriter(PASSWORD_FILE)) {
            writer.write(password.trim());
            adminPassword = password.trim();
        } catch (IOException e) {
            System.err.println("[WARNING] Failed to persist new admin password: " + e.getMessage());
        }
    }

    /**
     * Authenticates credentials. Handles brute-force locks.
     * 
     * @param username The entered username
     * @param password The entered password
     * @return true if authentication succeeds
     * @throws SystemLockedException if admin access is locked after 3 failures
     */
    public static boolean login(String username, String password) throws SystemLockedException {
        if (isLocked) {
            throw new SystemLockedException("Administrative console is LOCKED due to 3 consecutive failed login attempts.");
        }

        if (username == null || password == null) {
            return false;
        }

        boolean success = username.equals(ADMIN_USER) && password.equals(adminPassword);

        if (success) {
            failedAttempts = 0; // Reset attempts on success
            return true;
        } else {
            failedAttempts++;
            if (failedAttempts >= 3) {
                isLocked = true;
                throw new SystemLockedException("Administrative console has been LOCKED due to 3 consecutive failed login attempts.");
            }
            return false;
        }
    }

    /**
     * Checks if the admin portal is locked.
     */
    public static boolean isLocked() {
        return isLocked;
    }

    /**
     * Resets the lock state (for backend overrides or system restarts).
     */
    public static void unlock() {
        isLocked = false;
        failedAttempts = 0;
    }

    /**
     * Updates the admin password.
     * 
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if change is successful
     */
    public static boolean changePassword(String oldPassword, String newPassword) {
        if (oldPassword != null && oldPassword.equals(adminPassword)) {
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return false;
            }
            savePassword(newPassword);
            return true;
        }
        return false;
    }

    /**
     * Gets consecutive failed logins count.
     */
    public static int getFailedAttempts() {
        return failedAttempts;
    }
}
