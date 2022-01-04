package dtos;

import entities.Company;
import entities.CompanyStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyDTOTest {

    private Company company;
    private CompanyDTO companyDTO;
    private CompanyStatus companyStatus;

    @BeforeEach
    public void setUp() {
        company = new Company("TEST", "12345678");
        companyStatus = new CompanyStatus(LocalDateTime.now());
        companyStatus.setCompany(company);

        companyDTO = new CompanyDTO(company);
    }

    @AfterEach
    public void tearDown() {
        company = null;
        companyDTO = null;
        companyStatus = null;
    }

    @Test
    public void get_id() {
        // Arrange
        UUID expected = company.getId();

        // Act
        UUID actual = companyDTO.getId();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_name() {
        // Arrange
        String expected = company.getName();

        // Act
        String actual = companyDTO.getName();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_cvr() {
        // Arrange
        String expected = company.getCvr();

        // Act
        String actual = companyDTO.getCvr();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_company_statuses() {
        // Arrange
        List<CompanyStatusDTO> expected = new ArrayList<>();
        company.getCompanyStatuses().forEach(status -> {
            expected.add(new CompanyStatusDTO(status));
        });

        // Act
        List<CompanyStatusDTO> actual = companyDTO.getCompanyStatusDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

}
