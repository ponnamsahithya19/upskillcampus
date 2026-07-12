import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting outputs, dates, currencies, and drawing console layouts.
 * Includes console loading delays, custom warning boxes, and tabulations.
 * 
 * Part of the Banking Information System Project.
 */
public class Utils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Formats a decimal currency amount.
     */
    public static String formatCurrency(double amount) {
        return "Rs. " + String.format("%,.2f", amount);
    }

    /**
     * Formats LocalDateTime objects.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * Simulates a terminal loading cursor animation.
     * 
     * @param message Text to display with loading sequence
     */
    public static void printLoading(String message) {
        System.out.print("[PROCESSING] " + message + " ");
        char[] spin = {'|', '/', '-', '\\'};
        for (int i = 0; i < 12; i++) {
            System.out.print(spin[i % 4]);
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.print("\b"); // Erase last character
        }
        System.out.println("[SUCCESS]");
    }

    /**
     * Draws a console header layout.
     */
    public static void printHeader(String text) {
        System.out.println("\n=================================================================");
        System.out.println("  " + text.toUpperCase());
        System.out.println("=================================================================");
    }

    /**
     * Prints a success alert box.
     * 
     * @param msg Success message
     */
    public static void printSuccess(String msg) {
        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("  [SUCCESS] " + msg);
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    /**
     * Prints a warning alert box.
     * 
     * @param msg Warning message
     */
    public static void printWarning(String msg) {
        System.out.println("\n*****************************************************************");
        System.out.println("  [WARNING] " + msg);
        System.out.println("*****************************************************************");
    }

    /**
     * Prints an error alert box.
     * 
     * @param msg Error message
     */
    public static void printError(String msg) {
        System.out.println("\n#################################################################");
        System.out.println("  [ERROR] " + msg);
        System.out.println("#################################################################");
    }

    /**
     * Draws a console divider line.
     */
    public static void printDivider() {
        System.out.println("-----------------------------------------------------------------");
    }
}
