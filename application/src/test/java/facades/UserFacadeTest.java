package facades;

import dtos.UserDTO;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserCreationException;
import errorhandling.exceptions.UserNotFoundException;
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
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade userFacade;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        userFacade = UserFacade.getUserFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void get_all_empty_list() {
        // Act
        List<User> actual = userFacade.getAll();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_one_listed() {
        // Arrange
        User expected = new User("test@test.test", "test123");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<User> actual = userFacade.getAll();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_three_listed() {
        // Arrange
        List<User> expected = new ArrayList<>();
        expected.add(new User("test1@test.test", "test123"));
        expected.add(new User("test2@test.test", "test123"));
        expected.add(new User("test3@test.test", "test123"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(user -> {
                em.persist(user);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<User> actual = userFacade.getAll();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_empty_list() {
        // Act
        List<UserDTO> actual = userFacade.getAllDTOs();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_one_listed() {
        // Arrange
        User user = new User("test@test.test", "test123");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        UserDTO expected = new UserDTO(user);

        // Act
        List<UserDTO> actual = userFacade.getAllDTOs();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_three_listed() {
        // Arrange
        List<User> users = new ArrayList<>();
        users.add(new User("test1@test.test", "test123"));
        users.add(new User("test2@test.test", "test123"));
        users.add(new User("test3@test.test", "test123"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            users.forEach(user -> {
                em.persist(user);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<UserDTO> expected = new ArrayList<>();
        users.forEach(user -> {
            expected.add(new UserDTO(user));
        });

        // Act
        List<UserDTO> actual = userFacade.getAllDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_by_id_found() throws UserNotFoundException {
        // Arrange
        User expected = new User("test@test.test", "test123");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        User actual = userFacade.getById(expected.getId());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Assert
        assertThrows(UserNotFoundException.class, () -> {
            // Act
            userFacade.getById(id);
        });
    }

    @Test
    public void get_by_email_found() throws UserNotFoundException, SanitizationException {
        // Arrange
        User expected = new User("test@test.test", "test123");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        User actual = userFacade.getByEmail(expected.getEmail());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_email_not_found() {
        // Arrange
        String email = "unknown@test.test";

        // Assert
        assertThrows(UserNotFoundException.class, () -> {
            // Act
            userFacade.getByEmail(email);
        });
    }

    @Test
    public void get_by_email_sanitization_exception() {
        // Arrange
        String email = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            userFacade.getByEmail(email);
        });
    }

    @Test
    public void get_dto_by_email_found() throws UserNotFoundException, SanitizationException {
        // Arrange
        User user = new User("test@test.test", "test123");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        UserDTO expected = new UserDTO(user);

        // Act
        UserDTO actual = userFacade.getDTOByEmail(user.getEmail());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_dto_by_email_not_found() {
        // Arrange
        String email = "unknown@test.test";

        // Assert
        assertThrows(UserNotFoundException.class, () -> {
            // Act
            userFacade.getDTOByEmail(email);
        });
    }

    @Test
    public void get_dto_by_email_sanitization_exception() {
        // Arrange
        String email = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            userFacade.getDTOByEmail(email);
        });
    }

    @Test
    public void create() throws SanitizationException, UserCreationException, DatabaseException {
        // Arrange
        String email = "test@test.test";
        String password = "password123";

        // Act
        userFacade.create(email, password);
    }

    @Test
    public void create_email_already_in_use() {
        // Arrange
        String email = "test@test.test";
        String password = "password123";
        User user = new User(email, password);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Assert
        assertThrows(UserCreationException.class, () -> {
            // Act
            userFacade.create(email, password);
        });
    }

    @Test
    public void create_sanitization_exception_email() {
        // Arrange
        String email = "test@test.test";
        String password = "test123";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            userFacade.create(email, password);
        });
    }

    @Test
    public void create_sanitization_exception_password() {
        // Arrange
        String email = "test@test..test";
        String password = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            userFacade.create(email, password);
        });
    }

}
