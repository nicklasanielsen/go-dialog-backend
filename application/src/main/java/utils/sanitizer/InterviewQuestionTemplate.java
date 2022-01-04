package utils.sanitizer;

import static utils.sanitizer.Tools.removeSpaces;

/**
 *
 * @author Nicklas Nielsen
 */
public class InterviewQuestionTemplate {

    public static String sanitizeName(String input) {
        String sanitized = removeSpaces(input);

        return sanitized;
    }

    public static String sanitizeQuestion(String input) {
        String sanitized = removeSpaces(input);

        return sanitized;
    }

}
