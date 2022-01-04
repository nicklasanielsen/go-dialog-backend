package dtos;

import entities.CompanyStatusType;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyStatusTypeDTOTest {

    private CompanyStatusType companyStatusType;
    private CompanyStatusTypeDTO companyStatusTypeDTO;

    @BeforeEach
    public void setUp() {
        companyStatusType = new CompanyStatusType("TEST", true);
        companyStatusTypeDTO = new CompanyStatusTypeDTO(companyStatusType);
    }

    @AfterEach
    public void tearDown() {
        companyStatusType = null;
        companyStatusTypeDTO = null;
    }

    @Test
    public void get_type() {
        // Arrange
        String expected = companyStatusType.getType();

        // Act
        String actual = companyStatusTypeDTO.getType();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_is_default() {
        // Arrange
        boolean expected = companyStatusType.isDefault();

        // Act
        boolean actual = companyStatusTypeDTO.isDefault();

        // Assert
        assertEquals(expected, actual);
    }

}
