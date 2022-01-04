package dtos;

import entities.Person;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Nicklas Nielsen
 */
public class PersonDTOTest {

    private Person person;
    private PersonDTO personDTO;

    @BeforeEach
    public void setUp() {
        person = new Person("Test", "Tester", "Testensen");
        personDTO = new PersonDTO(person);
    }

    @AfterEach
    public void tearDown() {
        person = null;
        personDTO = null;
    }

    @Test
    public void get_id() {
        // Arrange
        UUID expected = person.getId();

        // Act
        UUID actual = personDTO.getId();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_firstname() {
        // Arrange
        String expected = person.getFirstname();

        // Act
        String actual = personDTO.getFirstname();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_middlename() {
        // Arrange
        String expected = person.getMiddlename();

        // Act
        String actual = personDTO.getMiddlename();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_lastname() {
        // Arrange
        String expected = person.getLastname();

        // Act
        String actual = personDTO.getLastname();

        // Assert
        assertEquals(expected, actual);
    }

}
