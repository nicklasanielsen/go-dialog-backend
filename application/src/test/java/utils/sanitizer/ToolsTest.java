package utils.sanitizer;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Nicklas Nielsen
 */
public class ToolsTest {

    @Test
    public void remove_space_start() {
        // Arrange
        String expected = "test";
        String toTest = " " + expected;

        // Act
        String actual = Tools.removeSpaces(toTest);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void remove_space_end() {
        // Arrange
        String expected = "test";
        String toTest = expected + " ";

        // Act
        String actual = Tools.removeSpaces(toTest);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void remove_space_start_and_end() {
        // Arrange
        String expected = "test";
        String toTest = " " + expected + " ";

        // Act
        String actual = Tools.removeSpaces(toTest);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void remove_space_no_space() {
        // Arrange
        String expected = "test";

        // Act
        String actual = Tools.removeSpaces(expected);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void verify_length_too_short() {
        // Arrange
        String toTest = "test";
        int minimumLength = 5;
        int maximumLength = 10;

        // Act
        boolean actual = Tools.verifyLength(toTest, minimumLength, maximumLength);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void verify_length_at_minimum() {
        // Arrange
        String toTest = "test";
        int minimumLength = 4;
        int maximumLength = 10;

        // Act
        boolean actual = Tools.verifyLength(toTest, minimumLength, maximumLength);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void verify_length_acceptable() {
        // Arrange
        String toTest = "test";
        int minimumLength = 2;
        int maximumLength = 5;

        // Act
        boolean actual = Tools.verifyLength(toTest, minimumLength, maximumLength);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void verify_length_at_maximum() {
        // Arrange
        String toTest = "test";
        int minimumLength = 1;
        int maximumLength = 4;

        // Act
        boolean actual = Tools.verifyLength(toTest, minimumLength, maximumLength);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void verify_length_too_long() {
        // Arrange
        String toTest = "test";
        int minimumLength = 1;
        int maximumLength = 3;

        // Act
        boolean actual = Tools.verifyLength(toTest, minimumLength, maximumLength);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void verify_characters_accaptable() {
        // Arrange
        List<String> legalCharacters = Arrays.asList("A", "B", "C");
        String toTest = "ABA";

        // Act
        boolean actual = Tools.verifyCharacters(toTest, legalCharacters);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void verify_characters_not_accaptable() {
        // Arrange
        List<String> legalCharacters = Arrays.asList("A", "B", "C");
        String toTest = "ABCD";

        // Act
        boolean actual = Tools.verifyCharacters(toTest, legalCharacters);

        // Assert
        assertFalse(actual);
    }

}
