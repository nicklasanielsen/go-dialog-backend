package dtos;

import entities.Role;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Nicklas Nielsen
 */
public class RoleDTOTest {

    private Role role;
    private RoleDTO roleDTO;

    @BeforeEach
    public void setUp() {
        role = new Role("TEST", true);
        roleDTO = new RoleDTO(role);
    }

    @AfterEach
    public void tearDown() {
        role = null;
        roleDTO = null;
    }

    @Test
    public void get_type() {
        // Arrange
        String expected = role.getType();

        // Act
        String actual = roleDTO.getType();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_is_default() {
        // Arrange
        boolean expected = role.isDefault();

        // Act
        boolean actual = roleDTO.isDefault();

        // Assert
        assertEquals(expected, actual);
    }

}
