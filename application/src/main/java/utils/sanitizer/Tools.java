package utils.sanitizer;

import java.util.List;

/**
 *
 * @author Nicklas Nielsen
 */
public class Tools {

    protected static String removeSpaces(String input) {
        return input.trim();
    }

    protected static boolean verifyLength(String input, int minimumLength, int maximumLength) {
        return input.length() >= minimumLength && input.length() <= maximumLength;
    }

    protected static boolean verifyCharacters(String input, List<String> characters) {
        String textInUpperCase = input.toUpperCase();

        for (char character : textInUpperCase.toCharArray()) {
            if (!characters.contains(String.valueOf(character))) {
                return false;
            }
        }

        return true;
    }

}
