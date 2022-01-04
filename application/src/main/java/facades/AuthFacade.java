package facades;

import entities.Role;
import entities.User;
import errorhandling.exceptions.AccountActivationException;
import errorhandling.exceptions.AccountRecoveryException;
import errorhandling.exceptions.AuthenticationException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserNotFoundException;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import utils.Email;
import static utils.sanitizer.User.sanitizePassword;

/**
 *
 * @author Nicklas Nielsen
 */
public class AuthFacade {

    private static EntityManagerFactory emf = null;
    private static AuthFacade instance = null;
    private static UserFacade userFacade = null;
    private static RoleFacade roleFacade = null;

    private AuthFacade() {
        // private to ensure singleton
    }

    public static AuthFacade getAuthFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AuthFacade();
            userFacade = UserFacade.getUserFacade(_emf);
            roleFacade = RoleFacade.getRoleFacade(_emf);
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public User login(String email, String password) throws SanitizationException, AuthenticationException {
        password = sanitizePassword(password);

        try {
            User user = userFacade.getByEmail(email);
            if (user.isActive() && user.verifyPassword(password)) {
                return user;
            }

            throw new AuthenticationException();
        } catch (UserNotFoundException e) {
            throw new AuthenticationException();
        }
    }

    public void requestAccountRecovery(String email) throws SanitizationException, DatabaseException {
        try {
            User user = userFacade.getByEmail(email);

            if (user.isActive() && user.isActivated()) {
                user.requestRecoveryCode();

                EntityManager em = getEntityManager();

                try {
                    em.getTransaction().begin();
                    em.merge(user);
                    em.getTransaction().commit();

                    Email.accountRecovery(user);
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }

                    throw new DatabaseException();
                } finally {
                    em.close();
                }
            }
        } catch (UserNotFoundException e) {

        }
    }

    public void processAccountRecovery(UUID userId, UUID recoveryCode, String newPassword) throws SanitizationException, DatabaseException, AccountRecoveryException {
        newPassword = sanitizePassword(newPassword);

        try {
            User user = userFacade.getById(userId);

            if (!user.isRecoveryCodeActive() || !user.verifyRecoveryCode(recoveryCode)) {
                throw new AccountRecoveryException();
            }

            user.setPassword(newPassword);
            user.terminateRecoveryCode();

            EntityManager em = getEntityManager();

            try {
                em.getTransaction().begin();
                em.merge(user);
                em.getTransaction().commit();

                Email.passwordReset(user);
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException();
            }
        } catch (UserNotFoundException e) {
            throw new AccountRecoveryException();
        }
    }

    public void accountActivation(UUID userId, UUID activationCode) throws AccountActivationException, DatabaseException {
        try {
            User user = userFacade.getById(userId);

            if (user.isDelated() || user.getCompany() == null) {
                throw new AccountActivationException();
            } else if (user.isActivated() && user.verifyActivationCode(activationCode)) {
                return;
            } else if (!user.isActivated() && user.verifyActivationCode(activationCode)) {
                user.activate();

                List<Role> defaultRoles = roleFacade.getAllDefaults();
                defaultRoles.forEach(role -> {
                    user.addRole(role);
                });

                EntityManager em = getEntityManager();

                try {
                    em.getTransaction().begin();
                    em.merge(user);
                    em.getTransaction().commit();

                    Email.accountActivation(user);
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }

                    throw new DatabaseException();
                } finally {
                    em.close();
                }
            }

            throw new AccountActivationException();
        } catch (UserNotFoundException e) {
            throw new AccountActivationException();
        }
    }

}
