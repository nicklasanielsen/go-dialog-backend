package dtos;

import entities.Person;
import entities.Role;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserDTOTest {

    private User user;
    private UserDTO userDTO;

    private List<Role> roles;
    private Person person;

    @BeforeEach
    public void setUp() {
        user = new User("test@test.test", "testing123");
        person = new Person("Test", "Tester", "Testensen");
        user.setPerson(person);

        roles = new ArrayList<>();
        roles.add(new Role("Test", true));
        roles.add(new Role("Testing", false));

        roles.forEach(role -> {
            user.addRole(role);
        });

        userDTO = new UserDTO(user);
    }

    @AfterEach
    public void tearDown() {
        user = null;
        userDTO = null;
        roles = null;
        person = null;
    }

    @Test
    public void get_id() {
        // Arrange
        UUID expected = user.getId();

        // Act
        UUID actual = userDTO.getId();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_person_dto() {
        // Arrange
        PersonDTO expected = new PersonDTO(user.getPerson());

        // Act
        PersonDTO actual = userDTO.getPersonDTO();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_role_dtos() {
        // Arrange
        List<RoleDTO> expected = new ArrayList<>();
        roles.forEach(role -> {
            expected.add(new RoleDTO(role));
        });

        // Act
        List<RoleDTO> actual = userDTO.getRoleDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_email() {
        // Arrange
        String expected = user.getEmail();

        // Act
        String actual = userDTO.getEmail();

        // Assert
        assertEquals(expected, actual);
    }

}
