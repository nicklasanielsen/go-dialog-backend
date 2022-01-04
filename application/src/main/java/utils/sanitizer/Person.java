package utils.sanitizer;

import errorhandling.exceptions.SanitizationException;
import java.util.Arrays;
import java.util.List;
import static utils.sanitizer.Tools.removeSpaces;
import static utils.sanitizer.Tools.verifyCharacters;
import static utils.sanitizer.Tools.verifyLength;

/**
 *
 * @author Nicklas Nielsen
 */
public class Person {

    private static final int FIRSTNAME_MINIMUM_LENGTH = 2;
    private static final int FIRSTNAME_MAXIMUM_LENGTH = 64;
    private static final int MIDDLENAME_MINIMUM_LENGTH = 2;
    private static final int MIDDLENAME_MAXIMUM_LENGTH = 64;
    private static final int LASTNAME_MINIMUM_LENGTH = 2;
    private static final int LASTNAME_MAXIMUM_LENGTH = 64;

    public static String sanitizeFirstname(String input) throws SanitizationException {
        String sanitized = removeSpaces(input);

        boolean validLength = verifyLength(sanitized, FIRSTNAME_MINIMUM_LENGTH, FIRSTNAME_MAXIMUM_LENGTH);
        if (!validLength) {
            throw new SanitizationException("Invalid length");
        }

        List<String> legalCharacters = Arrays.asList("A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "'", ",", ".", " ", "-");

        boolean validCharacters = verifyCharacters(sanitized, legalCharacters);
        if (!validCharacters) {
            throw new SanitizationException("Invalid characters");
        }

        return sanitized;
    }

    public static String sanitizeMiddlename(String input) throws SanitizationException {
        String sanitized = removeSpaces(input);

        if (sanitized.isEmpty()) {
            return sanitized;
        }

        boolean validLength = verifyLength(sanitized, MIDDLENAME_MINIMUM_LENGTH, MIDDLENAME_MAXIMUM_LENGTH);
        if (!validLength) {
            throw new SanitizationException("Invalid length");
        }

        List<String> legalCharacters = Arrays.asList("A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "'", ",", ".", " ", "-");

        boolean validCharacters = verifyCharacters(sanitized, legalCharacters);
        if (!validCharacters) {
            throw new SanitizationException("Invalid characters");
        }

        return sanitized;
    }

    public static String sanitizeLastname(String input) throws SanitizationException {
        String sanitized = removeSpaces(input);

        boolean validLength = verifyLength(sanitized, LASTNAME_MINIMUM_LENGTH, LASTNAME_MAXIMUM_LENGTH);
        if (!validLength) {
            throw new SanitizationException("Invalid length");
        }

        List<String> legalCharacters = Arrays.asList("A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "'", ",", ".", " ", "-");

        boolean validCharacters = verifyCharacters(sanitized, legalCharacters);
        if (!validCharacters) {
            throw new SanitizationException("Invalid characters");
        }

        return sanitized;
    }

}
