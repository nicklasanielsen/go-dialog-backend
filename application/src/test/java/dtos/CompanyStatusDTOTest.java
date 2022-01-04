package dtos;

import entities.CompanyStatus;
import entities.CompanyStatusType;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyStatusDTOTest {

    private CompanyStatus companyStatus;
    private CompanyStatusDTO companyStatusDTO;
    private CompanyStatusType companyStatusType;

    @BeforeEach
    public void setUp() {
        companyStatus = new CompanyStatus(LocalDateTime.now());
        companyStatusType = new CompanyStatusType("TEST", true);
        companyStatus.setCompanyStatusType(companyStatusType);

        companyStatusDTO = new CompanyStatusDTO(companyStatus);
    }

    @AfterEach
    public void tearDown() {
        companyStatus = null;
        companyStatusDTO = null;
        companyStatusType = null;
    }

    @Test
    public void get_id() {
        // Arrange
        UUID expected = companyStatus.getId();

        // Act
        UUID actual = companyStatusDTO.getId();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_start() {
        // Arrange
        LocalDateTime expected = companyStatus.getStart();

        // Act
        LocalDateTime actual = LocalDateTime.parse(companyStatusDTO.getStart());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_company_status_type() {
        // Arrange
        CompanyStatusTypeDTO expected = new CompanyStatusTypeDTO(companyStatusType);

        // Act
        CompanyStatusTypeDTO actual = companyStatusDTO.getCompanyStatusTypeDTO();

        // Assert
        assertEquals(expected, actual);
    }

}
