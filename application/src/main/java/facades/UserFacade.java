package facades;

import com.mashape.unirest.http.exceptions.UnirestException;
import dtos.UserDTO;
import entities.Company;
import entities.Person;
import entities.Role;
import entities.User;
import errorhandling.exceptions.AccountActivationException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserCreationException;
import errorhandling.exceptions.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import static utils.sanitizer.User.sanitizeEmail;
import static utils.sanitizer.User.sanitizePassword;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserFacade {

    private static EntityManagerFactory emf = null;
    private static UserFacade instance = null;
    private static RoleFacade roleFacade = null;

    private UserFacade() {
        // private to ensure singleton
    }

    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
            roleFacade = RoleFacade.getRoleFacade(_emf);
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<User> getAll() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("User.getAll");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<UserDTO> getAllDTOs() {
        List<UserDTO> dtos = new ArrayList<>();

        getAll().forEach(user -> {
            dtos.add(new UserDTO(user));
        });

        return dtos;
    }

    public User getById(UUID id) throws UserNotFoundException {
        EntityManager em = getEntityManager();

        User user = em.find(User.class, id.toString());

        if (user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    public UserDTO getDTOById(UUID id) throws UserNotFoundException, UnirestException {
        User user = getById(id);

        return new UserDTO(user);
    }

    public User getByEmail(String email) throws UserNotFoundException, SanitizationException {
        email = sanitizeEmail(email);

        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("User.getByEmail");
            query.setParameter("email", email.toUpperCase());

            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            throw new UserNotFoundException();
        } finally {
            em.close();
        }
    }

    public UserDTO getDTOByEmail(String email) throws UserNotFoundException, SanitizationException {
        return new UserDTO(getByEmail(email));
    }

    public User create(String email, String password) throws SanitizationException, UserCreationException, DatabaseException {
        email = sanitizeEmail(email);
        password = sanitizePassword(password);

        try {
            getByEmail(email);

            throw new UserCreationException();
        } catch (UserNotFoundException e) {
            User user = new User(email, password);

            List<Role> defaultRoles = roleFacade.getAllDefaults();
            defaultRoles.forEach(role -> {
                user.addRole(role);
            });

            EntityManager em = getEntityManager();

            try {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();

                return user;
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException();
            } finally {
                em.close();
            }
        }

    }

    public void edit() {
        // HUSK TEST
        throw new UnsupportedOperationException();
    }

    public void setPerson(User user, Person person) throws DatabaseException {
        EntityManager em = getEntityManager();

        user.setPerson(person);

        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        }
    }

    public void activateUser(User user, Company company, UUID activationCode) throws AccountActivationException, DatabaseException {
        if (user.isActivated() || user.verifyActivationCode(activationCode)) {
            throw new AccountActivationException();
        }

        user.setCompany(company);
        user.activate();

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(user);
            em.merge(company);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        }

    }

    public void deactivateUser(User user) throws DatabaseException {
        user.deactivate();

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        }
    }

    public void addManager(User employee, User manager) throws DatabaseException {
        employee.addManager(manager);

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(employee);
            em.merge(manager);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        }
    }

    public void removeManager(User employee, User manager) throws DatabaseException {
        System.out.println("Size=" + employee.getManagers().size());
        employee.removeManager(manager);
        System.out.println("Size=" + employee.getManagers().size());
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(employee);
            em.merge(manager);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        }
    }

}
