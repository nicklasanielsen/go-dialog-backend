package facades;

import dtos.PersonDTO;
import entities.Person;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.PersonNotFoundException;
import errorhandling.exceptions.SanitizationException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import static utils.sanitizer.Person.sanitizeFirstname;
import static utils.sanitizer.Person.sanitizeLastname;
import static utils.sanitizer.Person.sanitizeMiddlename;

/**
 *
 * @author Nicklas Nielsen
 */
public class PersonFacade {

    private static EntityManagerFactory emf = null;
    private static PersonFacade instance = null;

    private PersonFacade() {
        // private to ensure singleton
    }

    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Person> getAll() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Person.getAll");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getAllDTOs() {
        List<PersonDTO> dtos = new ArrayList<>();

        getAll().forEach(person -> {
            dtos.add(new PersonDTO(person));
        });

        return dtos;
    }

    public Person getById(UUID id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();

        Person person = em.find(Person.class, id.toString());

        if (person == null) {
            throw new PersonNotFoundException();
        }

        return person;
    }

    public PersonDTO getDTOById(UUID id) throws PersonNotFoundException {
        return new PersonDTO(getById(id));
    }

    public List<Person> getAllByName(String firstname, String middlename, String lastname) throws SanitizationException {
        EntityManager em = getEntityManager();

        if (!firstname.isEmpty() && middlename.isEmpty() && lastname.isEmpty()) {
            // Only firstname provided
            firstname = sanitizeFirstname(firstname);

            try {
                Query query = em.createNamedQuery("Person.getByFirstname");
                query.setParameter("firstname", firstname.toUpperCase());

                return query.getResultList();
            } finally {
                em.close();
            }
        } else if (!firstname.isEmpty() && !middlename.isEmpty() && lastname.isEmpty()) {
            // Firstname and middlename provided
            firstname = sanitizeFirstname(firstname);
            middlename = sanitizeMiddlename(middlename);

            try {
                Query query = em.createNamedQuery("Person.getByFirstnameAndMiddlename");
                query.setParameter("firstname", firstname);
                query.setParameter("middlename", middlename);

                return query.getResultList();
            } finally {
                em.close();
            }
        } else if (!firstname.isEmpty() && middlename.isEmpty() && !lastname.isEmpty()) {
            // Firstname and lastname provided
            firstname = sanitizeFirstname(firstname);
            lastname = sanitizeLastname(lastname);

            try {
                Query query = em.createNamedQuery("Person.getByFirstnameAndLastname");
                query.setParameter("firstname", firstname);
                query.setParameter("lastname", lastname);

                return query.getResultList();
            } finally {
                em.close();
            }
        } else if (!firstname.isEmpty() && !middlename.isEmpty() && !lastname.isEmpty()) {
            // Fullname provided
            firstname = sanitizeFirstname(firstname);
            middlename = sanitizeMiddlename(middlename);
            lastname = sanitizeLastname(lastname);

            try {
                Query query = em.createNamedQuery("Person.getByFullname");
                query.setParameter("firstname", firstname);
                query.setParameter("middlename", middlename);
                query.setParameter("lastname", lastname);

                System.out.println(query);

                return query.getResultList();
            } finally {
                em.close();
            }
        } else if (firstname.isEmpty() && !middlename.isEmpty() && lastname.isEmpty()) {
            // Only middlename provided
            middlename = sanitizeMiddlename(middlename);

            try {
                Query query = em.createNamedQuery("Person.getByMiddlename");
                query.setParameter("middlename", middlename);

                return query.getResultList();
            } finally {
                em.close();
            }
        } else if (firstname.isEmpty() && !middlename.isEmpty() && !lastname.isEmpty()) {
            // Middlename and lastname provided
            middlename = sanitizeMiddlename(middlename);
            lastname = sanitizeLastname(lastname);

            try {
                Query query = em.createNamedQuery("Person.getByMiddlenameAndLastname");
                query.setParameter("middlename", middlename);
                query.setParameter("lastname", lastname);

                return query.getResultList();
            } finally {
                em.close();
            }
        } else if (firstname.isEmpty() && middlename.isEmpty() && !lastname.isEmpty()) {
            // Only lastname provided
            lastname = sanitizeLastname(lastname);

            em = getEntityManager();

            try {
                Query query = em.createNamedQuery("Person.getByLastname");
                query.setParameter("lastname", lastname);

                return query.getResultList();
            } finally {
                em.close();
            }
        }

        // No names provided
        return new ArrayList<>();
    }

    public List<PersonDTO> getAllDTOsByName(String firstname, String middlename, String lastname) throws SanitizationException {
        List<PersonDTO> dtos = new ArrayList<>();

        getAllByName(firstname, middlename, lastname).forEach(person -> {
            dtos.add(new PersonDTO(person));
        });

        return dtos;
    }

    public Person getByUser(User user) throws PersonNotFoundException {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Person.getByUser");
            query.setParameter("user_id", user.getId().toString());

            return (Person) query.getSingleResult();
        } catch (NoResultException e) {
            throw new PersonNotFoundException();
        }
    }

    public PersonDTO getDTOByUser(User user) throws PersonNotFoundException {
        return new PersonDTO(getByUser(user));
    }

    public Person create(String firstname, String middlename, String lastname) throws SanitizationException, DatabaseException {
        // HUST TESTS!

        firstname = sanitizeFirstname(firstname);
        middlename = sanitizeMiddlename(middlename);
        lastname = sanitizeLastname(lastname);

        EntityManager em = getEntityManager();

        Person person = new Person(firstname, middlename, lastname);

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();

            return person;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        }
    }

    public void edit() {
        // HUSK TEST
        throw new UnsupportedOperationException();
    }

}
