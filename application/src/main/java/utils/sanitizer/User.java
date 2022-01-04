package utils.sanitizer;

import errorhandling.exceptions.SanitizationException;
import java.util.regex.Pattern;
import static utils.sanitizer.Tools.removeSpaces;
import static utils.sanitizer.Tools.verifyLength;

/**
 *
 * @author Nicklas Nielsen
 */
public class User {

    private static final int EMAIL_MINIMUM_LENGTH = 6;
    private static final int EMAIL_MAXIMUM_LENGTH = 320;
    private static final int PASSWORD_MINIMUM_LENGTH = 8; // Passwords shorter than 8 characters are considered to be weak (NIST SP800-63B)
    private static final int PASSWORD_MAXIMUM_LENGTH = 64; // Maximum for BCrypt

    public static String sanitizeEmail(String input) throws SanitizationException {
        String sanitized = removeSpaces(input);

        boolean validLength = verifyLength(sanitized, EMAIL_MINIMUM_LENGTH, EMAIL_MAXIMUM_LENGTH);
        if (!validLength) {
            throw new SanitizationException("Invalid length");
        }

        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; // Provided by OWASP
        Pattern pattern = Pattern.compile(regex);

        boolean validEmail = pattern.matcher(sanitized).matches();
        if (!validEmail) {
            throw new SanitizationException("Invalid Email");
        }

        return sanitized;
    }

    public static String sanitizePassword(String input) throws SanitizationException {
        String sanitized = removeSpaces(input);

        boolean validLength = verifyLength(sanitized, PASSWORD_MINIMUM_LENGTH, PASSWORD_MAXIMUM_LENGTH);
        if (!validLength) {
            throw new SanitizationException("Invalid length");
        }

        return sanitized;
    }

}
