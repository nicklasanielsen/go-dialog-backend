package facades;

import dtos.CompanyStatusTypeDTO;
import entities.CompanyStatusType;
import errorhandling.exceptions.CompanyStatusTypeCreationException;
import errorhandling.exceptions.CompanyStatusTypeEditException;
import errorhandling.exceptions.CompanyStatusTypeNotFoundException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.SanitizationException;
import java.util.ArrayList;
import java.util.List;
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
public class CompanyStatusTypeFacadeTest {

    private static EntityManagerFactory emf;
    private static CompanyStatusTypeFacade companyStatusTypeFacade;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        companyStatusTypeFacade = CompanyStatusTypeFacade.getFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
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
            em.createNamedQuery("CompanyStatusType.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("CompanyStatusType.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void get_all_empty_list() {
        // Act
        List<CompanyStatusType> actual = companyStatusTypeFacade.getAll();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_one_listed() {
        // Arrange
        CompanyStatusType expected = new CompanyStatusType("TEST", true);

        // Act
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatusType> actual = companyStatusTypeFacade.getAll();

        // Assert
        assertTrue(actual.contains(expected));
        assertEquals(1, actual.size());
    }

    @Test
    public void get_all_three_listed() {
        // Arrange
        CompanyStatusType expected_1 = new CompanyStatusType("TEST 1", true);
        CompanyStatusType expected_2 = new CompanyStatusType("TEST 2", true);
        CompanyStatusType expected_3 = new CompanyStatusType("TEST 3", true);

        // Act
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected_1);
            em.persist(expected_2);
            em.persist(expected_3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatusType> actual = companyStatusTypeFacade.getAll();

        // Assert
        assertEquals(3, actual.size());
        assertTrue(actual.contains(expected_1));
        assertTrue(actual.contains(expected_2));
        assertTrue(actual.contains(expected_3));
    }

    @Test
    public void get_all_as_dto_empty_list() {
        // Act
        List<CompanyStatusTypeDTO> actual = companyStatusTypeFacade.getAllDTOs();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_as_dto_one_listed() {
        // Arrange
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);
        CompanyStatusTypeDTO expected = new CompanyStatusTypeDTO(companyStatusType);

        // Act
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(companyStatusType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatusTypeDTO> actual = companyStatusTypeFacade.getAllDTOs();

        // Assert
        assertTrue(actual.contains(expected));
        assertEquals(1, actual.size());
    }

    @Test
    public void get_all_as_dto_three_listed() {
        // Arrange        
        List<CompanyStatusType> companyStatusTypes = new ArrayList<>();
        companyStatusTypes.add(new CompanyStatusType("TEST 1", true));
        companyStatusTypes.add(new CompanyStatusType("TEST 2", true));
        companyStatusTypes.add(new CompanyStatusType("TEST 3", true));

        List<CompanyStatusTypeDTO> expected = new ArrayList<>();
        for (CompanyStatusType companyStatusType : companyStatusTypes) {
            expected.add(new CompanyStatusTypeDTO(companyStatusType));
        }

        // Act
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            for (CompanyStatusType companyStatusType : companyStatusTypes) {
                em.persist(companyStatusType);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<CompanyStatusTypeDTO> actual = companyStatusTypeFacade.getAllDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_by_type_found() throws CompanyStatusTypeNotFoundException, SanitizationException {
        // Arrange
        String type = "TEST";
        CompanyStatusType expected = new CompanyStatusType(type, true);

        // Act
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusType actual = companyStatusTypeFacade.getByType(type);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_not_found() {
        // Arrange
        String type = "TEST";

        // Assert
        assertThrows(CompanyStatusTypeNotFoundException.class, () -> {
            // Act
            companyStatusTypeFacade.getByType(type);
        });
    }

    @Test
    public void get_dto_by_type_found() throws CompanyStatusTypeNotFoundException, SanitizationException {
        // Arrange
        String type = "TEST";
        CompanyStatusType companyStatusType = new CompanyStatusType(type, true);
        CompanyStatusTypeDTO expected = new CompanyStatusTypeDTO(companyStatusType);

        // Act
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(companyStatusType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusTypeDTO actual = companyStatusTypeFacade.getDTOByType(type);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_dto_by_type_not_found() {
        // Arrange
        String type = "TEST";

        // Assert
        assertThrows(CompanyStatusTypeNotFoundException.class, () -> {
            // Act
            companyStatusTypeFacade.getDTOByType(type);
        });
    }

    @Test
    public void get_default_found() throws CompanyStatusTypeNotFoundException, DatabaseException {
        // Arrange
        CompanyStatusType expected = new CompanyStatusType("TEST", true);

        // Act
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusType actual = companyStatusTypeFacade.getDefault();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_default_not_found() {
        // Assert
        assertThrows(CompanyStatusTypeNotFoundException.class, () -> {
            // Act
            companyStatusTypeFacade.getDefault();
        });
    }

    @Test
    public void get_default_dto_found() throws CompanyStatusTypeNotFoundException, DatabaseException {
        // Arrange
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);
        CompanyStatusTypeDTO expected = new CompanyStatusTypeDTO(companyStatusType);

        // Act
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(companyStatusType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        CompanyStatusTypeDTO actual = companyStatusTypeFacade.getDefaultDTO();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_default_dto_not_found() {
        // Assert
        assertThrows(CompanyStatusTypeNotFoundException.class, () -> {
            // Act
            companyStatusTypeFacade.getDefaultDTO();
        });
    }

    @Test
    public void create_new_non_default_success() throws SanitizationException, DatabaseException, CompanyStatusTypeCreationException {
        // Arrange
        String type = "TEST";
        boolean isDefault = true;
        CompanyStatusType expected = new CompanyStatusType(type, isDefault);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        expected.setType("NEWTEST");
        expected.setDefault(false);

        // Act
        companyStatusTypeFacade.createNew(expected.getType(), expected.isDefault());
    }

    @Test
    public void create_new_default_success() throws SanitizationException, DatabaseException, CompanyStatusTypeCreationException {
        // Arrange
        String type = "TEST";
        boolean isDefault = true;
        CompanyStatusType expected = new CompanyStatusType(type, isDefault);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        expected.setType("NEWTEST");

        // Act
        companyStatusTypeFacade.createNew(expected.getType(), expected.isDefault());
    }

    @Test
    public void create_new_type_already_in_use() {
        // Arrange
        String type = "TEST";
        boolean isDefault = false;
        CompanyStatusType expected = new CompanyStatusType(type, isDefault);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Assert
        assertThrows(CompanyStatusTypeCreationException.class, () -> {
            // Act
            companyStatusTypeFacade.createNew(expected.getType(), expected.isDefault());
        });
    }

    @Test
    public void edit_new_default_old_type() throws SanitizationException, DatabaseException, CompanyStatusTypeEditException, CompanyStatusTypeNotFoundException, CompanyStatusTypeNotFoundException {
        // Arrange
        String type = "TEST";
        boolean isDefault = false;
        CompanyStatusType expected = new CompanyStatusType(type, isDefault);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        expected.setDefault(true);

        // Act
        companyStatusTypeFacade.edit(type, expected.getType(), expected.isDefault());
        CompanyStatusType actual = companyStatusTypeFacade.getDefault();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void edit_new_default_new_type() throws SanitizationException, DatabaseException, CompanyStatusTypeEditException, CompanyStatusTypeNotFoundException {
        // Arrange
        String type = "TEST";
        boolean isDefault = false;
        CompanyStatusType expected = new CompanyStatusType(type, isDefault);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        expected.setType("TESTING");
        expected.setDefault(true);

        // Act
        companyStatusTypeFacade.edit(type, expected.getType(), expected.isDefault());
        CompanyStatusType actual = companyStatusTypeFacade.getDefault();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void edit_existing_default_new_type() throws SanitizationException, DatabaseException, CompanyStatusTypeEditException, CompanyStatusTypeNotFoundException {
        // Arrange
        String type = "TEST";
        boolean isDefault = true;
        CompanyStatusType expected = new CompanyStatusType(type, isDefault);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        expected.setType("TESTING");

        // Act
        companyStatusTypeFacade.edit(type, expected.getType(), expected.isDefault());
        CompanyStatusType actual = companyStatusTypeFacade.getDefault();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void edit_old_type_not_found() {
        // Arrange
        String oldType = "TEST";
        String newType = "TESTING";

        // Assert
        assertThrows(CompanyStatusTypeEditException.class, () -> {
            // Act
            companyStatusTypeFacade.edit(oldType, newType, true);
        });
    }

    @Test
    public void edit_new_type_already_in_use() {
        // Arrange
        String newType = "TEST";
        String oldType = "TESTING";
        boolean isDefault = false;

        CompanyStatusType oldCompanyStatusType = new CompanyStatusType(oldType, isDefault);
        CompanyStatusType newCompanyStatysType = new CompanyStatusType(newType, isDefault);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(oldCompanyStatusType);
            em.persist(newCompanyStatysType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Assert
        assertThrows(CompanyStatusTypeEditException.class, () -> {
            // Act
            companyStatusTypeFacade.edit(oldCompanyStatusType.getType(), newCompanyStatysType.getType(), oldCompanyStatusType.isDefault());
        });
    }

}
