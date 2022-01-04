package facades;

import dtos.CompanyDTO;
import entities.Company;
import errorhandling.exceptions.CompanyNotFoundException;
import errorhandling.exceptions.SanitizationException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyFacadeTest {

    private static EntityManagerFactory emf;
    private static CompanyFacade companyFacade;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        companyFacade = CompanyFacade.getCompanyFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void tearDownClass() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void get_all_empty_list() {
        // Act
        List<Company> actual = companyFacade.getAll();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_one_listed() {
        // Arrange
        Company expected = new Company("TEST", "11111111");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Company> actual = companyFacade.getAll();

        // Assert
        assertTrue(actual.contains(expected));
        assertEquals(1, actual.size());
    }

    @Test
    public void get_all_three_listed() {
        // Arrange
        List<Company> expected = new ArrayList<>();
        expected.add(new Company("TEST", "11111111"));
        expected.add(new Company("TEST", "22222222"));
        expected.add(new Company("TEST", "33333333"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(company -> {
                em.persist(company);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Company> actual = companyFacade.getAll();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_empty_list() {
        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOs();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dto_one_listed() {
        // Arrange
        Company company = new Company("TEST", "11111111");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(company);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyDTO expected = new CompanyDTO(company);

        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOs();

        // Assert
        assertTrue(actual.contains(expected));
        assertEquals(1, actual.size());
    }

    @Test
    public void get_all_dtos_three_listed() {
        // Arrange
        List<CompanyDTO> expected = new ArrayList<>();

        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "22222222"));
        companies.add(new Company("TEST", "33333333"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        companies.forEach(company -> {
            expected.add(new CompanyDTO(company));
        });

        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_by_id_found() throws CompanyNotFoundException {
        // Arrange
        Company expected = new Company("TEST", "12345678");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        Company actual = companyFacade.getById(expected.getId());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Assert
        assertThrows(CompanyNotFoundException.class, () -> {
            // Act
            companyFacade.getById(id);
        });
    }

    @Test
    public void get_dto_by_id_found() throws CompanyNotFoundException {
        // Arrange
        Company company = new Company("TEST", "12345678");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(company);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyDTO expected = new CompanyDTO(company);

        // Act
        CompanyDTO actual = companyFacade.getDTOById(company.getId());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_dto_by_id_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Assert
        assertThrows(CompanyNotFoundException.class, () -> {
            // Act
            companyFacade.getDTOById(id);
        });
    }

    @Test
    public void get_all_by_name_empty_list() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "22222222"));
        companies.add(new Company("TEST", "33333333"));
        Company singleCompany = new Company("TESTING", "44444444");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Company> actual = companyFacade.getAllByName("NONE");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_name_one_listed() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "22222222"));
        companies.add(new Company("TEST", "33333333"));
        Company singleCompany = new Company("TESTING", "44444444");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Company> actual = companyFacade.getAllByName("TESTING");

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(singleCompany));
    }

    @Test
    public void get_all_by_name_three_listed() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "22222222"));
        companies.add(new Company("TEST", "33333333"));
        Company singleCompany = new Company("TESTING", "44444444");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Company> actual = companyFacade.getAllByName("TEST");

        // Assert
        assertEquals(3, actual.size());
        assertTrue(actual.containsAll(companies));
    }

    @Test
    public void get_all_by_name_sanitization_exception() {
        // Arrange
        String name = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            companyFacade.getAllByName(name);
        });
    }

    @Test
    public void get_all_dtos_by_name_empty_list() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "22222222"));
        companies.add(new Company("TEST", "33333333"));
        Company singleCompany = new Company("TESTING", "44444444");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOsByName("NONE");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_name_one_listed() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "22222222"));
        companies.add(new Company("TEST", "33333333"));
        Company singleCompany = new Company("TESTING", "44444444");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyDTO expected = new CompanyDTO(singleCompany);

        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOsByName("TESTING");

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_name_three_lsited() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "22222222"));
        companies.add(new Company("TEST", "33333333"));
        Company singleCompany = new Company("TESTING", "44444444");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyDTO> expected = new ArrayList<>();
        companies.forEach(company -> {
            expected.add(new CompanyDTO(company));
        });

        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOsByName("TEST");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_name_sanitization_exception() {
        // Arrange
        String name = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            companyFacade.getAllDTOsByName(name);
        });
    }

    @Test
    public void get_all_by_cvr_empty_list() throws SanitizationException {
        // Arrange
        String cvr = "12345678";

        // Act
        List<Company> actual = companyFacade.getAllByCvr(cvr);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_cvr_one_listed() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "11111111"));
        Company singleCompany = new Company("TESTING", "22222222");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Company> actual = companyFacade.getAllByCvr(singleCompany.getCvr());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(singleCompany));
    }

    @Test
    public void get_all_by_cvr_three_listed() throws SanitizationException {
        // Arrange
        List<Company> expected = new ArrayList<>();
        expected.add(new Company("TEST", "11111111"));
        expected.add(new Company("TEST", "11111111"));
        expected.add(new Company("TEST", "11111111"));
        Company singleCompany = new Company("TESTING", "22222222");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Company> actual = companyFacade.getAllByCvr(expected.get(0).getCvr());

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_by_cvr_sanitization_exception() {
        // Arrange
        String cvr = "abcdefgh";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            companyFacade.getAllByCvr(cvr);
        });
    }

    @Test
    public void get_all_dtos_by_cvr_empty_list() throws SanitizationException {
        // Arrange
        String cvr = "12345678";

        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOsByCvr(cvr);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_cvr_one_listed() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "11111111"));
        Company singleCompany = new Company("TESTING", "22222222");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyDTO expected = new CompanyDTO(singleCompany);

        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOsByCvr(singleCompany.getCvr());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_cvr_three_listed() throws SanitizationException {
        // Arrange
        List<Company> companies = new ArrayList<>();
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "11111111"));
        companies.add(new Company("TEST", "11111111"));
        Company singleCompany = new Company("TESTING", "22222222");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companies.forEach(company -> {
                em.persist(company);
            });
            em.persist(singleCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyDTO> expected = new ArrayList<>();
        companies.forEach(company -> {
            expected.add(new CompanyDTO(company));
        });

        // Act
        List<CompanyDTO> actual = companyFacade.getAllDTOsByCvr(companies.get(0).getCvr());

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_cvr_sanitization_exception() {
        // Arrange
        String cvr = "abcdefgh";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            companyFacade.getAllDTOsByCvr(cvr);
        });
    }

}
