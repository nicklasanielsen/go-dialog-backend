package facades;

import dtos.RoleDTO;
import entities.Role;
import errorhandling.exceptions.RoleNotFoundException;
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
public class RoleFacadeTest {

    private static EntityManagerFactory emf;
    private static RoleFacade roleFacade;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        roleFacade = RoleFacade.getRoleFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void get_all_empty_list() {
        // Act
        List<Role> actual = roleFacade.getAll();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_one_listed() {
        // Arrange
        Role expected = new Role("TEST");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Role> actual = roleFacade.getAll();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_three_listed() {
        // Arrange
        List<Role> expected = new ArrayList<>();
        expected.add(new Role("TEST"));
        expected.add(new Role("TESTING"));
        expected.add(new Role("TESTTEST"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(role -> {
                em.persist(role);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Role> actual = roleFacade.getAll();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_empty_list() {
        // Act
        List<RoleDTO> actual = roleFacade.getAllDTOs();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_one_listed() {
        // Arrange
        Role role = new Role("TEST");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        RoleDTO expected = new RoleDTO(role);

        // Act
        List<RoleDTO> actual = roleFacade.getAllDTOs();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_three_listed() {
        // Arrange
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("TEST"));
        roles.add(new Role("TESTING"));
        roles.add(new Role("TESTTEST"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            roles.forEach(role -> {
                em.persist(role);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<RoleDTO> expected = new ArrayList<>();
        roles.forEach(role -> {
            expected.add(new RoleDTO(role));
        });

        // Act
        List<RoleDTO> actual = roleFacade.getAllDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_by_type_found() throws RoleNotFoundException, SanitizationException {
        // Arrange
        Role expected = new Role("TEST");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        Role actual = roleFacade.getByType(expected.getType());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_not_found() {
        // Arrange
        String type = "UNKNOWN";

        // Assert
        assertThrows(RoleNotFoundException.class, () -> {
            // Act
            roleFacade.getByType(type);
        });
    }

    @Test
    public void get_by_type_sanitization_exception() {
        // Arrange
        String type = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            roleFacade.getByType(type);
        });
    }

    @Test
    public void get_dto_by_type_found() throws RoleNotFoundException, SanitizationException {
        // Arrange
        Role role = new Role("TEST");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        RoleDTO expected = new RoleDTO(role);

        // Act
        RoleDTO actual = roleFacade.getDTOByType(role.getType());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_dto_by_type_not_found() {
        // Arrange
        String type = "UNKNOWN";

        // Assert
        assertThrows(RoleNotFoundException.class, () -> {
            // Act
            roleFacade.getDTOByType(type);
        });
    }

    @Test
    public void get_dto_by_type_sanitization_exception() {
        // Arrange
        String type = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            roleFacade.getDTOByType(type);
        });
    }

    @Test
    public void get_all_defaults_empty_list() {
        // Act
        List<Role> actual = roleFacade.getAllDefaults();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_defaults_one_listed() {
        // Arrange
        Role expected = new Role("TEST", true);
        Role role = new Role("TESTING");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.persist(role);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Role> actual = roleFacade.getAllDefaults();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_defaults_three_listed() {
        // Arrange
        List<Role> expected = new ArrayList<>();
        expected.add(new Role("TEST", true));
        expected.add(new Role("TESTING", true));
        expected.add(new Role("TESTTEST", true));

        Role role = new Role("NORMAL");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(role);
            expected.forEach(r -> {
                em.persist(r);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Role> actual = roleFacade.getAllDefaults();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_default_dtos_empty_list() {
        // Act
        List<RoleDTO> actual = roleFacade.getAllDefaultDTOs();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_default_dtos_one_listed() {
        // Arrange
        Role role = new Role("TEST", true);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        RoleDTO expected = new RoleDTO(role);

        // Act
        List<RoleDTO> actual = roleFacade.getAllDefaultDTOs();

        // Asssert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_default_dtos_three_listed() {
        // Arrange
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("TEST", true));
        roles.add(new Role("TESTING", true));
        roles.add(new Role("TESTTEST", true));

        Role role = new Role("NORMAL");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(role);
            roles.forEach(r -> {
                em.persist(r);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<RoleDTO> expected = new ArrayList<>();
        roles.forEach(r -> {
            expected.add(new RoleDTO(r));
        });

        // Act
        List<RoleDTO> actual = roleFacade.getAllDefaultDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

}
