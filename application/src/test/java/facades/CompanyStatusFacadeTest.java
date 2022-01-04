package facades;

import dtos.CompanyStatusDTO;
import entities.Company;
import entities.CompanyStatus;
import entities.CompanyStatusType;
import errorhandling.exceptions.CompanyStatusNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyStatusFacadeTest {

    private static EntityManagerFactory emf;
    private static CompanyStatusFacade companyStatusFacade;

    private CompanyStatusType statusTypeTrail;
    private CompanyStatusType statusTypeActive;
    private CompanyStatusType statusTypeInactive;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        companyStatusFacade = CompanyStatusFacade.getCompanyStatusFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("CompanyStatus.deleteAllRows").executeUpdate();
            em.createNamedQuery("CompanyStatusType.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("CompanyStatus.deleteAllRows").executeUpdate();
            em.createNamedQuery("CompanyStatusType.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @BeforeEach
    public void setUp() {
        statusTypeTrail = new CompanyStatusType("TRAIL", true);
        statusTypeActive = new CompanyStatusType("ACTIVE", false);
        statusTypeInactive = new CompanyStatusType("INACTIVE", false);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(statusTypeTrail);
            em.persist(statusTypeActive);
            em.persist(statusTypeInactive);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        statusTypeTrail = null;
        statusTypeActive = null;
        statusTypeInactive = null;

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("CompanyStatus.deleteAllRows").executeUpdate();
            em.createNamedQuery("CompanyStatusType.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void get_all_empty_list() {
        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAll();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_one_listed() {
        // Arrange
        CompanyStatus expected = new CompanyStatus(LocalDateTime.now());

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAll();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_three_listed() {
        // Arrange
        List<CompanyStatus> expected = new ArrayList<>();
        expected.add(new CompanyStatus(LocalDateTime.now()));
        expected.add(new CompanyStatus(LocalDateTime.now()));
        expected.add(new CompanyStatus(LocalDateTime.now()));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAll();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_empty_list() {
        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOs();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_one_listed() {
        // Arrange
        CompanyStatus companyStatus = new CompanyStatus(LocalDateTime.now());

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(companyStatus);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusDTO expected = new CompanyStatusDTO(companyStatus);

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOs();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_three_listed() {
        List<CompanyStatus> companyStatuses = new ArrayList<>();
        companyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        companyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        companyStatuses.add(new CompanyStatus(LocalDateTime.now()));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            companyStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatusDTO> expected = new ArrayList<>();
        companyStatuses.forEach(companyStatus -> {
            expected.add(new CompanyStatusDTO(companyStatus));
        });

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_related_to_company_empty_list() {
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

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllRelatedToCompany(company);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_related_to_company_one_listed() {
        // Arrange
        Company singleCompany = new Company("SINGLE", "12345678");
        Company multiCompany = new Company("MULTI", "87654321");

        CompanyStatus singleCompanyStatus = new CompanyStatus(LocalDateTime.now());
        List<CompanyStatus> multiCompanyStatuses = new ArrayList<>();
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));

        singleCompanyStatus.setCompany(singleCompany);
        multiCompanyStatuses.forEach(companyStatus -> {
            companyStatus.setCompany(multiCompany);
        });

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(singleCompanyStatus);
            multiCompanyStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.persist(singleCompany);
            em.persist(multiCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatus expected = singleCompanyStatus;

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllRelatedToCompany(singleCompany);

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_related_to_company_three_listed() {
        // Arrange
        Company singleCompany = new Company("SINGLE", "12345678");
        Company multiCompany = new Company("MULTI", "87654321");

        CompanyStatus singleCompanyStatus = new CompanyStatus(LocalDateTime.now());
        List<CompanyStatus> multiCompanyStatuses = new ArrayList<>();
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));

        singleCompanyStatus.setCompany(singleCompany);
        multiCompanyStatuses.forEach(companyStatus -> {
            companyStatus.setCompany(multiCompany);
        });

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(singleCompanyStatus);
            multiCompanyStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.persist(singleCompany);
            em.persist(multiCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatus> expected = multiCompanyStatuses;

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllRelatedToCompany(multiCompany);

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_related_to_company_empty_list() {
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

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsRelatedToCompany(company);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_related_to_company_one_listed() {
        // Arrange
        Company singleCompany = new Company("SINGLE", "12345678");
        Company multiCompany = new Company("MULTI", "87654321");

        CompanyStatus singleCompanyStatus = new CompanyStatus(LocalDateTime.now());
        List<CompanyStatus> multiCompanyStatuses = new ArrayList<>();
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));

        singleCompanyStatus.setCompany(singleCompany);
        multiCompanyStatuses.forEach(companyStatus -> {
            companyStatus.setCompany(multiCompany);
        });

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(singleCompanyStatus);
            multiCompanyStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.persist(singleCompany);
            em.persist(multiCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusDTO expected = new CompanyStatusDTO(singleCompanyStatus);

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsRelatedToCompany(singleCompany);

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_related_to_company_three_listed() {
        // Arrange
        Company singleCompany = new Company("SINGLE", "12345678");
        Company multiCompany = new Company("MULTI", "87654321");

        CompanyStatus singleCompanyStatus = new CompanyStatus(LocalDateTime.now());
        List<CompanyStatus> multiCompanyStatuses = new ArrayList<>();
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiCompanyStatuses.add(new CompanyStatus(LocalDateTime.now()));

        singleCompanyStatus.setCompany(singleCompany);
        multiCompanyStatuses.forEach(companyStatus -> {
            companyStatus.setCompany(multiCompany);
        });

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(singleCompanyStatus);
            multiCompanyStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.persist(singleCompany);
            em.persist(multiCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatusDTO> expected = new ArrayList<>();
        multiCompanyStatuses.forEach(companyStatus -> {
            expected.add(new CompanyStatusDTO(companyStatus));
        });

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsRelatedToCompany(multiCompany);

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_by_type_empty_list() {
        // Arrange
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(companyStatusType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllByType(companyStatusType);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_type_one_listed() {
        // Arrange
        CompanyStatusType singleType = new CompanyStatusType("SINGLE", true);
        CompanyStatusType multipleType = new CompanyStatusType("MULTI", false);

        List<CompanyStatus> multiStatus = new ArrayList<>();
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));

        multiStatus.forEach(companyStatus -> {
            companyStatus.setCompanyStatusType(multipleType);
        });

        CompanyStatus expected = new CompanyStatus(LocalDateTime.now());
        expected.setCompanyStatusType(singleType);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(singleType);
            em.persist(multipleType);
            em.persist(expected);
            multiStatus.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllByType(singleType);

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_by_type_three_listed() {
        // Arrange
        CompanyStatusType singleType = new CompanyStatusType("SINGLE", true);
        CompanyStatusType multipleType = new CompanyStatusType("MULTI", false);

        List<CompanyStatus> expected = new ArrayList<>();
        expected.add(new CompanyStatus(LocalDateTime.now()));
        expected.add(new CompanyStatus(LocalDateTime.now()));
        expected.add(new CompanyStatus(LocalDateTime.now()));

        expected.forEach(companyStatus -> {
            companyStatus.setCompanyStatusType(multipleType);
        });

        CompanyStatus singleStatus = new CompanyStatus(LocalDateTime.now());
        singleStatus.setCompanyStatusType(singleType);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(singleType);
            em.persist(multipleType);
            em.persist(singleStatus);
            expected.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllByType(multipleType);

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_type_empty_list() {
        // Arrange
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(companyStatusType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsByType(companyStatusType);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_type_one_listed() {
        // Arrange
        CompanyStatusType singleType = new CompanyStatusType("SINGLE", true);
        CompanyStatusType multipleType = new CompanyStatusType("MULTI", false);

        List<CompanyStatus> multiStatus = new ArrayList<>();
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));

        multiStatus.forEach(companyStatus -> {
            companyStatus.setCompanyStatusType(multipleType);
        });

        CompanyStatus singleStatus = new CompanyStatus(LocalDateTime.now());
        singleStatus.setCompanyStatusType(singleType);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(singleType);
            em.persist(multipleType);
            em.persist(singleStatus);
            multiStatus.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusDTO expected = new CompanyStatusDTO(singleStatus);

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsByType(singleType);

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_type_three_listed() {
        // Arrange
        CompanyStatusType singleType = new CompanyStatusType("SINGLE", true);
        CompanyStatusType multipleType = new CompanyStatusType("MULTI", false);

        List<CompanyStatus> multiStatus = new ArrayList<>();
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));
        multiStatus.add(new CompanyStatus(LocalDateTime.now()));

        multiStatus.forEach(companyStatus -> {
            companyStatus.setCompanyStatusType(multipleType);
        });

        CompanyStatus singleStatus = new CompanyStatus(LocalDateTime.now());
        singleStatus.setCompanyStatusType(singleType);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(singleType);
            em.persist(multipleType);
            em.persist(singleStatus);
            multiStatus.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatusDTO> expected = new ArrayList<>();
        multiStatus.forEach(companyStatus -> {
            expected.add(new CompanyStatusDTO(companyStatus));
        });

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsByType(multipleType);

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_related_to_company_by_type_empty_list() {
        // Arrange
        Company company = new Company("TEST", "12345678");
        CompanyStatusType companyStatusType = new CompanyStatusType("TESTING", true);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(company);
            em.persist(companyStatusType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllRelatedToCompanyByType(company, companyStatusType);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_related_to_company_by_type_one_listed() {
        // Arrange
        Company singleCompany = new Company("SINGLE", "12345678");
        Company multiCompany = new Company("MULTI", "87654321");

        CompanyStatusType correctStatusType = new CompanyStatusType("CORRECT", true);
        CompanyStatusType incorrectStatusType = new CompanyStatusType("INCORRECT", false);

        CompanyStatus singleStatus = new CompanyStatus(LocalDateTime.now());
        singleStatus.setCompanyStatusType(correctStatusType);
        singleStatus.setCompany(singleCompany);

        List<CompanyStatus> multiStatuses = new ArrayList<>();
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));

        multiStatuses.stream().map(companyStatus -> {
            companyStatus.setCompanyStatusType(incorrectStatusType);
            return companyStatus;
        }).forEachOrdered(companyStatus -> {
            companyStatus.setCompany(multiCompany);
        });

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(correctStatusType);
            em.persist(incorrectStatusType);
            em.persist(singleStatus);
            multiStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.persist(singleCompany);
            em.persist(multiCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatus expected = singleStatus;

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllRelatedToCompanyByType(singleCompany, correctStatusType);

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_related_to_company_by_type_three_listed() {
        // Arrange
        Company singleCompany = new Company("SINGLE", "12345678");
        Company multiCompany = new Company("MULTI", "87654321");

        CompanyStatusType correctStatusType = new CompanyStatusType("CORRECT", true);
        CompanyStatusType incorrectStatusType = new CompanyStatusType("INCORRECT", false);

        CompanyStatus singleStatus = new CompanyStatus(LocalDateTime.now());
        singleStatus.setCompanyStatusType(correctStatusType);
        singleStatus.setCompany(singleCompany);

        List<CompanyStatus> multiStatuses = new ArrayList<>();
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));

        multiStatuses.stream().map(companyStatus -> {
            companyStatus.setCompanyStatusType(incorrectStatusType);
            return companyStatus;
        }).forEachOrdered(companyStatus -> {
            companyStatus.setCompany(multiCompany);
        });

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(correctStatusType);
            em.persist(incorrectStatusType);
            em.persist(singleStatus);
            multiStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.persist(singleCompany);
            em.persist(multiCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatus> expected = multiStatuses;

        // Act
        List<CompanyStatus> actual = companyStatusFacade.getAllRelatedToCompanyByType(multiCompany, incorrectStatusType);

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_related_to_company_by_type_empty_list() {
        // Arrange
        Company Company = new Company("TESTING", "12345678");
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(Company);
            em.persist(companyStatusType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsRelatedToCompanyByType(Company, companyStatusType);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_related_to_company_by_type_one_listed() {
        // Arrange
        Company singleCompany = new Company("SINGLE", "12345678");
        Company multiCompany = new Company("MULTI", "87654321");

        CompanyStatusType correctStatusType = new CompanyStatusType("CORRECT", true);
        CompanyStatusType incorrectStatusType = new CompanyStatusType("INCORRECT", false);

        CompanyStatus singleStatus = new CompanyStatus(LocalDateTime.now());
        singleStatus.setCompanyStatusType(correctStatusType);
        singleStatus.setCompany(singleCompany);

        List<CompanyStatus> multiStatuses = new ArrayList<>();
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));

        multiStatuses.stream().map(companyStatus -> {
            companyStatus.setCompanyStatusType(incorrectStatusType);
            return companyStatus;
        }).forEachOrdered(companyStatus -> {
            companyStatus.setCompany(multiCompany);
        });

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(correctStatusType);
            em.persist(incorrectStatusType);
            em.persist(singleStatus);
            multiStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.persist(singleCompany);
            em.persist(multiCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusDTO expected = new CompanyStatusDTO(singleStatus);

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsRelatedToCompanyByType(singleCompany, correctStatusType);

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_related_to_company_by_type_three_listed() {
        // Arrange
        Company singleCompany = new Company("SINGLE", "12345678");
        Company multiCompany = new Company("MULTI", "87654321");

        CompanyStatusType correctStatusType = new CompanyStatusType("CORRECT", true);
        CompanyStatusType incorrectStatusType = new CompanyStatusType("INCORRECT", false);

        CompanyStatus singleStatus = new CompanyStatus(LocalDateTime.now());
        singleStatus.setCompanyStatusType(correctStatusType);
        singleStatus.setCompany(singleCompany);

        List<CompanyStatus> multiStatuses = new ArrayList<>();
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));
        multiStatuses.add(new CompanyStatus(LocalDateTime.now()));

        multiStatuses.stream().map(companyStatus -> {
            companyStatus.setCompanyStatusType(incorrectStatusType);
            return companyStatus;
        }).forEachOrdered(companyStatus -> {
            companyStatus.setCompany(multiCompany);
        });

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(correctStatusType);
            em.persist(incorrectStatusType);
            em.persist(singleStatus);
            multiStatuses.forEach(companyStatus -> {
                em.persist(companyStatus);
            });
            em.persist(singleCompany);
            em.persist(multiCompany);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatusDTO> expected = new ArrayList<>();
        multiStatuses.forEach(companyStatus -> {
            expected.add(new CompanyStatusDTO(companyStatus));
        });

        // Act
        List<CompanyStatusDTO> actual = companyStatusFacade.getAllDTOsRelatedToCompanyByType(multiCompany, incorrectStatusType);

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_by_id_found() throws CompanyStatusNotFoundException {
        // Arrange
        CompanyStatus expected = new CompanyStatus(LocalDateTime.now());

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        CompanyStatus actual = companyStatusFacade.getById(expected.getId());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Assert
        assertThrows(CompanyStatusNotFoundException.class, () -> {
            // Act
            companyStatusFacade.getById(id);
        });
    }

    @Test
    public void get_dto_by_id_found() throws CompanyStatusNotFoundException {
        // Arrange
        CompanyStatus companyStatus = new CompanyStatus(LocalDateTime.now());

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(companyStatus);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusDTO expected = new CompanyStatusDTO(companyStatus);

        // Act
        CompanyStatusDTO actual = companyStatusFacade.getDTOById(companyStatus.getId());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_dto_by_id_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Assert
        assertThrows(CompanyStatusNotFoundException.class, () -> {
            // Act
            companyStatusFacade.getDTOById(id);
        });
    }

}
