package facades;

import dtos.PersonDTO;
import entities.Person;
import entities.User;
import errorhandling.exceptions.PersonNotFoundException;
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
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade personFacade;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        personFacade = PersonFacade.getPersonFacade(emf);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void get_all_empty_list() {
        // Act
        List<Person> actual = personFacade.getAll();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_one_listed() {
        // Arrang
        Person expected = new Person("Test", "Tester", "Testensen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAll();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_three_listed() {
        // Arrange
        List<Person> expected = new ArrayList<>();
        expected.add(new Person("Nikolaj", "", "Larsen"));
        expected.add(new Person("Mathias", "Haugaard", "Nielsen"));
        expected.add(new Person("Nicklas", "Alexander", "Nielsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAll();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_empty_list() {
        // Act
        List<PersonDTO> actual = personFacade.getAllDTOs();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_one_listed() {
        // Arrang
        Person person = new Person("Test", "Tester", "Testensen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOs();

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_three_listed() {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Nikolaj", "", "Larsen"));
        persons.add(new Person("Mathias", "Haugaard", "Nielsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            persons.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<PersonDTO> expected = new ArrayList<>();
        persons.forEach(person -> {
            expected.add(new PersonDTO(person));
        });

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOs();

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_by_id_found() throws PersonNotFoundException {
        // Arrange
        Person expected = new Person("Nikolaj", "", "Larsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        Person actual = personFacade.getById(expected.getId());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Assert
        assertThrows(PersonNotFoundException.class, () -> {
            // Act
            personFacade.getById(id);
        });
    }

    @Test
    public void get_dto_by_id_found() throws PersonNotFoundException {
        // Arrange
        Person person = new Person("Nikolaj", "", "Larsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        // Act
        PersonDTO actual = personFacade.getDTOById(person.getId());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_dto_by_id_not_found() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Assert
        assertThrows(PersonNotFoundException.class, () -> {
            // Act
            personFacade.getDTOById(id);
        });
    }

    @Test
    public void get_all_by_name_only_firstname_empty_list() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mathias", "Haugaard", "Nielsen"));
        persons.add(new Person("Nikolaj", "", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        Person person = new Person("Test", "", "Mand");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            persons.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        String firstname = "UNKNOWN";

        // Act
        List<Person> actual = personFacade.getAllByName(firstname, "", "");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_name_only_firstname_one_listed() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mathias", "Haugaard", "Nielsen"));
        persons.add(new Person("Nikolaj", "", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        Person person = new Person("Test", "", "Mand");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            persons.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        String firstname = person.getFirstname();

        // Act
        List<Person> actual = personFacade.getAllByName(firstname, "", "");

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(person));
    }

    @Test
    public void get_all_by_name_only_firstname_three_listed() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Nicklas", "Haugaard", "Nielsen"));
        persons.add(new Person("Nicklas", "", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        Person person = new Person("Test", "", "Mand");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            persons.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        String firstname = "Nicklas";

        // Act
        List<Person> actual = personFacade.getAllByName(firstname, "", "");

        // Assert
        assertEquals(persons.size(), actual.size());
        assertTrue(actual.containsAll(persons));
    }

    @Test
    public void get_all_by_name_only_firstname_sanitization_exception() {
        // Arrange
        String firstname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, "", "");
        });
    }

    @Test
    public void get_all_by_name_only_middlename_empty_list() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mathias", "Haugaard", "Nielsen"));
        persons.add(new Person("Nikolaj", "", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        Person person = new Person("Test", "", "Mand");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            persons.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        String middlename = "UNKNOWN";

        // Act
        List<Person> actual = personFacade.getAllByName("", middlename, "");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_name_only_middlename_one_listed() throws SanitizationException {
        // Arrange
        Person expected = new Person("Mathias", "Haugaard", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName("", expected.getMiddlename(), "");

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_by_name_only_middlename_three_listed() throws SanitizationException {
        // Arrange
        List<Person> expected = new ArrayList<>();
        expected.add(new Person("Nikolaj", "Haugaard", "Larsen"));
        expected.add(new Person("Mathias", "Haugaard", "Nielsen"));
        expected.add(new Person("Nicklas", "Haugaard", "Nielsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName("", "Haugaard", "");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_by_name_only_middlename_sanitization_exception() {
        // Arrange
        String middlename = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName("", middlename, "");
        });
    }

    @Test
    public void get_all_by_name_only_lastname_empty_list() throws SanitizationException {
        // Act
        List<Person> actual = personFacade.getAllByName("", "", "Nielsen");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_name_only_lastname_one_listed() throws SanitizationException {
        // Arrange
        Person expected = new Person("Nicklas", "Alexander", "Nielsen");

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Nikolaj", "", "Larsen"));
        persons.add(new Person("Mathias", "Haugaard", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Larsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            persons.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName("", "", expected.getLastname());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_by_name_only_lastname_three_listed() throws SanitizationException {
        // Arrange
        Person person = new Person("Nicklas", "Alexander", "Nielsen");

        List<Person> expected = new ArrayList<>();
        expected.add(new Person("Nikolaj", "", "Larsen"));
        expected.add(new Person("Mathias", "Haugaard", "Larsen"));
        expected.add(new Person("Nicklas", "Alexander", "Larsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            expected.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName("", "", "Larsen");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_by_name_only_lastname_sanitization_exception() {
        // Arrange
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName("", "", lastname);
        });
    }

    @Test
    public void get_all_by_name_firstname_middlename_empty_list() throws SanitizationException {
        // Act
        List<Person> actual = personFacade.getAllByName("", "Alexander", "Nielsen");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_name_firstname_middlename_one_listed() throws SanitizationException {
        // Arrange
        Person expected = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName(expected.getFirstname(), expected.getMiddlename(), "");

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_by_name_firstname_middlename_three_listed() throws SanitizationException {
        // Arrange
        Person person = new Person("Nikolaj", "", "Larsen");
        List<Person> expected = new ArrayList<>();

        expected.add(new Person("Nikolaj", "Alexander", "Larsen"));
        expected.add(new Person("Nikolaj", "Alexander", "Nielsen"));
        expected.add(new Person("Nikolaj", "Alexander", "Hansen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            expected.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName("Nikolaj", "Alexander", "");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_by_name_firstname_middlename_sanitization_exception_firstname() {
        // Arrange
        String firstname = "#";
        String middlename = "Haugaard";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, "");
        });
    }

    @Test
    public void get_all_by_name_firstname_middlename_sanitization_exception_middlename() {
        // Arrange
        String firstname = "Mathias";
        String middlename = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, "");
        });
    }

    @Test
    public void get_all_by_name_firstname_middlename_sanitization_exception_both() {
        // Arrange
        String firstname = "#";
        String middlename = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, "");
        });
    }

    @Test
    public void get_all_by_name_firstname_lastname_empty_list() throws SanitizationException {
        // Arrange
        String firstname = "Nicklas";
        String lastname = "Nielsen";

        // Act
        List<Person> actual = personFacade.getAllByName(firstname, "", lastname);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_name_firstname_lastname_one_listed() throws SanitizationException {
        // Arrange
        Person expected = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName(expected.getFirstname(), "", expected.getLastname());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_by_name_firstname_lastname_three_listed() throws SanitizationException {
        // Arrange
        List<Person> expected = new ArrayList<>();
        expected.add(new Person("Nicklas", "", "Nielsen"));
        expected.add(new Person("Nicklas", "Haugaard", "Nielsen"));
        expected.add(new Person("Nicklas", "Alexander", "Nielsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName("Nicklas", "", "Nielsen");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_by_name_firstname_lastname_sanitization_exception_firstname() {
        // Arrange
        String firstname = "#";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, "", lastname);
        });
    }

    @Test
    public void get_all_by_name_firstname_lastname_sanitization_exception_lastname() {
        // Arrange
        String firstname = "Nicklas";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, "", lastname);
        });
    }

    @Test
    public void get_all_by_name_firstname_lastname_sanitization_exception_both() {
        // Arrange
        String firstname = "#";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, "", lastname);
        });
    }

    @Test
    public void get_all_by_name_middlename_lastname_empty_list() throws SanitizationException {
        // Arrange
        String middlename = "Alexander";
        String lastname = "Nielsen";

        // Act
        List<Person> actual = personFacade.getAllByName("", middlename, lastname);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_name_middlename_lastname_one_listed() throws SanitizationException {
        // Arrange
        Person expected = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName("", expected.getMiddlename(), expected.getLastname());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_by_name_middlename_lastname_three_listed() throws SanitizationException {
        // Arrange
        List<Person> expected = new ArrayList<>();
        expected.add(new Person("Nikolaj", "Alexander", "Nielsen"));
        expected.add(new Person("Mathias", "Alexander", "Nielsen"));
        expected.add(new Person("Nicklas", "Alexander", "Nielsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName("", "Alexander", "Nielsen");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_by_name_middlename_lastname_sanitization_exception_middlename() {
        // Arrange
        String middlename = "#";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName("", middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_middlename_lastname_sanitization_exception_lastname() {
        // Arrange
        String middlename = "Alexander";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName("", middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_middlename_lastname_sanitization_exception_both() {
        // Arrange
        String middlename = "#";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName("", middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_fullname_empty_list() throws SanitizationException {
        // Arrange
        String firstname = "Nicklas";
        String middlename = "Alexander";
        String lastname = "Nielsen";

        // Act
        List<Person> actual = personFacade.getAllByName(firstname, middlename, lastname);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_by_name_fullname_one_listed() throws SanitizationException {
        // Arrange
        Person expected = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName(expected.getFirstname(), expected.getMiddlename(), expected.getLastname());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_by_name_fullname_three_listed() throws SanitizationException {
        // Arrange
        String firstname = "Nicklas";
        String middlename = "Alexander";
        String lastname = "Nielsen";

        List<Person> expected = new ArrayList<>();
        expected.add(new Person(firstname, middlename, lastname));
        expected.add(new Person(firstname, middlename, lastname));
        expected.add(new Person(firstname, middlename, lastname));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            expected.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        List<Person> actual = personFacade.getAllByName(firstname, middlename, lastname);

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_by_name_fullname_sanitization_exception_firstname() {
        // Arrange
        String firstname = "#";
        String middlename = "Alexander";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_fullname_sanitization_exception_middlename() {
        // Arrange
        String firstname = "Nicklas";
        String middlename = "#";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_fullname_sanitization_exception_lastname() {
        // Arrange
        String firstname = "Nicklas";
        String middlename = "Alexander";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_fullname_sanitization_exception_firstname_middlename() {
        // Arrange
        String firstname = "#";
        String middlename = "#";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_fullname_sanitization_exception_firstname_lastname() {
        // Arrange
        String firstname = "#";
        String middlename = "Alexander";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_fullname_sanitization_exception_middlename_lastname() {
        // Arrange
        String firstname = "Nicklas";
        String middlename = "#";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_by_name_fullname_sanitization_exception_fullname() {
        // Arrange
        String firstname = "#";
        String middlename = "#";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_only_firstname_empty_list() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mathias", "Haugaard", "Nielsen"));
        persons.add(new Person("Nikolaj", "", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        Person person = new Person("Test", "", "Mand");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            persons.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        String firstname = "UNKNOWN";

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(firstname, "", "");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_name_only_firstname_one_listed() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mathias", "Haugaard", "Nielsen"));
        persons.add(new Person("Nikolaj", "", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        Person person = new Person("Test", "", "Mand");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            persons.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        String firstname = person.getFirstname();

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(firstname, "", "");

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_name_only_firstname_three_listed() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Nicklas", "Haugaard", "Nielsen"));
        persons.add(new Person("Nicklas", "", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        Person person = new Person("Test", "", "Mand");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            persons.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<PersonDTO> expected = new ArrayList<>();
        persons.forEach(p -> {
            expected.add(new PersonDTO(p));
        });

        String firstname = "Nicklas";

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(firstname, "", "");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_name_only_firstname_sanitization_exception() {
        // Arrange
        String firstname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, "", "");
        });
    }

    @Test
    public void get_all_dtos_by_name_only_middlename_empty_list() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mathias", "Haugaard", "Nielsen"));
        persons.add(new Person("Nikolaj", "", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        Person person = new Person("Test", "", "Mand");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            persons.forEach(p -> {
                em.persist(p);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        String firstname = "UNKNOWN";

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(firstname, "", "");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_name_only_middlename_one_listed() throws SanitizationException {
        // Arrange
        Person person1 = new Person("Test", "", "Mand");
        Person person2 = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person1);
            em.persist(person2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person2);

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("", expected.getMiddlename(), "");

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_name_only_middlename_three_listed() throws SanitizationException {
        // Arrange
        Person person1 = new Person("Test", "", "Mand");
        Person person2 = new Person("Nicklas", "Alexander", "Nielsen");
        Person person3 = new Person("Test", "Alexander", "Mand");
        Person person4 = new Person("Jess", "Alexander", "Jensen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person1);
            em.persist(person2);
            em.persist(person3);
            em.persist(person4);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        String middlename = "Alexander";

        List<PersonDTO> expected = new ArrayList<>();
        expected.add(new PersonDTO(person2));
        expected.add(new PersonDTO(person3));
        expected.add(new PersonDTO(person4));

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("", middlename, "");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_name_only_middlename_sanitization_exception() {
        // Arrange
        String middlename = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName("", middlename, "");
        });
    }

    @Test
    public void get_all_dtos_by_name_only_lastname_empty_list() throws SanitizationException {
        // Arrange
        String lastname = "Larsen";

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("", "", lastname);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_name_only_lastname_one_listed() throws SanitizationException {
        // Arrange
        Person person = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("", "", person.getLastname());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_name_only_lastname_three_listed() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Nikolaj", "", "Nielsen"));
        persons.add(new Person("Mathias", "Haugaard", "Nielsen"));
        persons.add(new Person("Nickas", "Alexander", "Nielsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            persons.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<PersonDTO> expected = new ArrayList<>();
        persons.forEach(person -> {
            expected.add(new PersonDTO(person));
        });

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("", "", "Nielsen");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_name_only_lastname_sanitization_exception() {
        // Arrange
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName("", "", lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_firstname_middlename_empty_list() throws SanitizationException {
        // Arrange
        String firstname = "Mathias";
        String middlename = "Haugaard";

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(firstname, middlename, "");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_name_firstname_middlename_one_listed() throws SanitizationException {
        // Arrange
        Person person = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(person.getFirstname(), person.getMiddlename(), "");

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_name_firstname_middlename_three_listed() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));
        persons.add(new Person("Nicklas", "Alexander", "Larsen"));
        persons.add(new Person("Nicklas", "Alexander", "Hansen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            persons.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<PersonDTO> expected = new ArrayList<>();
        persons.forEach(person -> {
            expected.add(new PersonDTO(person));
        });

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("Nicklas", "Alexander", "");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_name_firstname_middlename_sanitization_exception_firstname() {
        // Arrange
        String firstname = "#";
        String middlename = "Alexander";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, "");
        });
    }

    @Test
    public void get_all_dtos_by_name_firstname_middlename_sanitization_exception_middlename() {
        // Arrange
        String firstname = "Nicklas";
        String middlename = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, "");
        });
    }

    @Test
    public void get_all_dtos_by_name_firstname_middlename_sanitization_exception_both() {
        // Arrange
        String firstname = "#";
        String middlename = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, "");
        });
    }

    @Test
    public void get_all_dtos_by_name_firstname_lastname_empty_list() throws SanitizationException {
        // Arrange
        String firstname = "Nikolaj";
        String lastname = "Larsen";

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(firstname, "", lastname);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_name_firstname_lastname_one_listed() throws SanitizationException {
        // Arrange
        Person person = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(person.getFirstname(), "", person.getLastname());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_name_firstname_lastname_three_listed() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Nicklas", "", "Nielsen"));
        persons.add(new Person("Nicklas", "Haugaard", "Nielsen"));
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            persons.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<PersonDTO> expected = new ArrayList<>();
        persons.forEach(person -> {
            expected.add(new PersonDTO(person));
        });

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("Nicklas", "", "Nielsen");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_name_firstname_lastname_sanitization_exception_firstname() {
        // Arrange
        String firstname = "#";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, "", lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_firstname_lastname_sanitization_exception_lastname() {
        // Arrange
        String firstname = "Nicklas";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, "", lastname);
        });
    }

    @Test
    public void ger_all_dtos_by_name_firstname_lastname_sanitization_exception_both() {
        // Arrange
        String firstname = "#";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, "", lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_middlename_lastname_empty_list() throws SanitizationException {
        // Arrange
        String middlename = "Haugaard";
        String lastname = "Nielsen";

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("", middlename, lastname);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_name_middlename_lastname_one_listed() throws SanitizationException {
        // Arrange
        Person person = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("", person.getMiddlename(), person.getLastname());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_name_middlename_lastname_three_listed() throws SanitizationException {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Nicklas", "Alexander", "Nielsen"));
        persons.add(new Person("Nikolaj", "Alexander", "Nielsen"));
        persons.add(new Person("Mathias", "Alexander", "Nielsen"));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            persons.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<PersonDTO> expected = new ArrayList<>();
        persons.forEach(person -> {
            expected.add(new PersonDTO(person));
        });

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("", "Alexander", "Nielsen");

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_name_middlename_lastname_sanitization_exception_middlename() {
        // Arrange
        String middlename = "#";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName("", middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_middlename_lastname_sanitization_exception_lastname() {
        // Arrange
        String middlename = "Alexande";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName("", middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_middlename_lastname_sanitization_exception_both() {
        // Arrange
        String middlename = "#";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName("", middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_fullname_empty_list() throws SanitizationException {
        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName("Nicklas", "Alexander", "Nielsen");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void get_all_dtos_by_name_fullname_one_listed() throws SanitizationException {
        // Arrange
        Person person = new Person("Nicklas", "Alexander", "Nielsen");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(person.getFirstname(), person.getMiddlename(), person.getLastname());

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void get_all_dtos_by_name_fullname_three_listed() throws SanitizationException {
        // Arrange
        String firstname = "Nicklas";
        String middlename = "Alexander";
        String lastname = "Nielsen";

        List<Person> persons = new ArrayList<>();
        persons.add(new Person(firstname, middlename, lastname));
        persons.add(new Person(firstname, middlename, lastname));
        persons.add(new Person(firstname, middlename, lastname));

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            persons.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        List<PersonDTO> expected = new ArrayList<>();
        persons.forEach(person -> {
            expected.add(new PersonDTO(person));
        });

        // Act
        List<PersonDTO> actual = personFacade.getAllDTOsByName(firstname, middlename, lastname);

        // Assert
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void get_all_dtos_by_name_fullname_sanitization_exception_firstname() {
        // Arrange
        String firstname = "#";
        String middlename = "Haugaard";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_fullname_sanitization_exception_middlename() {
        // Arrange
        String firstname = "Mathias";
        String middlename = "#";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_fullname_sanitization_exception_lastname() {
        // Arrange
        String firstname = "Mathias";
        String middlename = "Haugaard";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_fullname_sanitization_exception_firstname_middlename() {
        // Arrange
        String firstname = "#";
        String middlename = "#";
        String lastname = "Nielsen";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_fullname_sanitization_exception_firstname_lastname() {
        // Arrange
        String firstname = "#";
        String middlename = "Haugaard";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_fullname_sanitization_exception_middlename_lastname() {
        // Arrange
        String firstname = "Mathias";
        String middlename = "#";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_all_dtos_by_name_fullname_sanitization_exception_fullname() {
        // Arrange
        String firstname = "#";
        String middlename = "#";
        String lastname = "#";

        // Assert
        assertThrows(SanitizationException.class, () -> {
            // Act
            personFacade.getAllDTOsByName(firstname, middlename, lastname);
        });
    }

    @Test
    public void get_by_user_found() throws PersonNotFoundException {
        // Arrange
        User user = new User("test@test.test", "test123");
        Person expected = new Person("Nicklas", "Alexander", "Nielsen");
        user.setPerson(expected);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.persist(expected);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        // Act
        Person actual = personFacade.getByUser(user);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_user_not_found() {
        // Arrange
        User user = new User("test@test.test", "test123");

        // Assert
        assertThrows(PersonNotFoundException.class, () -> {
            // Act
            personFacade.getByUser(user);
        });
    }

    @Test
    public void get_dto_by_user_found() throws PersonNotFoundException {
        // Arrange
        User user = new User("test@test.test", "test123");
        Person person = new Person("Nicklas", "Alexander", "Nielsen");
        user.setPerson(person);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        PersonDTO expected = new PersonDTO(person);

        // Act
        PersonDTO actual = personFacade.getDTOByUser(user);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_dto_by_user_not_found() {
        // Arrange
        User user = new User("test@test.test", "test123");

        // Assert
        assertThrows(PersonNotFoundException.class, () -> {
            // Act
            personFacade.getDTOByUser(user);
        });
    }

}
