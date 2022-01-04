package errorhandling.exceptions;

/**
 *
 * @author Nicklas Nielsen
 */
public class SanitizationException extends Exception {

    private String reason;

    public SanitizationException(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return "";
    }

}
