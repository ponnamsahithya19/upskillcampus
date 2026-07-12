import java.util.regex.Pattern;

/**
 * Utility class providing validation checks for various inputs.
 * Highlights input sanitation and robust banking control structures.
 * 
 * Part of the Banking Information System Project.
 */
public class Validation {
    // Regex Patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}\\d{4}[A-Z]{1}$");

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates phone format (must be exactly 10 digits).
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates Aadhaar format (must be exactly 12 digits).
     */
    public static boolean isValidAadhaar(String aadhaar) {
        if (aadhaar == null) return false;
        return AADHAAR_PATTERN.matcher(aadhaar.trim()).matches();
    }

    /**
     * Validates Permanent Account Number (PAN) format.
     */
    public static boolean isValidPAN(String pan) {
        if (pan == null) return false;
        return PAN_PATTERN.matcher(pan.trim().toUpperCase()).matches();
    }
}
