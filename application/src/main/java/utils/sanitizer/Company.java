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
public class Company {

    private final static int NAME_MINIMUM_LENGTH = 1;
    private final static int NAME_MAXIMUM_LENGTH = 255;
    private final static int CVR_MINIMUM_LENGTH = 8;
    private final static int CVR_MAXIMUM_LENGTH = 8;

    public static String sanitizeName(String input) throws SanitizationException {
        String sanitized = removeSpaces(input);

        boolean validLength = verifyLength(sanitized, NAME_MINIMUM_LENGTH, NAME_MAXIMUM_LENGTH);
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

    public static String sanitizeCvr(String input) throws SanitizationException {
        String sanitized = removeSpaces(input);

        boolean validLength = verifyLength(sanitized, CVR_MINIMUM_LENGTH, CVR_MAXIMUM_LENGTH);
        if (!validLength) {
            throw new SanitizationException("Invalid length");
        }

        List<String> legalCharacters = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

        boolean validCharacters = verifyCharacters(sanitized, legalCharacters);
        if (!validCharacters) {
            throw new SanitizationException("Invalid characters");
        }

        return sanitized;
    }

}
