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
public class CompanyStatusType {

    private final static int TYPE_MINIMUM_LENGTH = 4;
    private final static int TYPE_MAXIMUM_LENGTH = 255;

    public static String sanitizeType(String input) throws SanitizationException {
        String sanitized = removeSpaces(input);

        boolean vaildLength = verifyLength(sanitized, TYPE_MINIMUM_LENGTH, TYPE_MAXIMUM_LENGTH);
        if (!vaildLength) {
            throw new SanitizationException("Invalid length");
        }

        List<String> legalCharacters = Arrays.asList("A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "-");

        boolean validCharacters = verifyCharacters(sanitized, legalCharacters);
        if (!validCharacters) {
            throw new SanitizationException("Invalid characters");
        }

        return sanitized;
    }

}
