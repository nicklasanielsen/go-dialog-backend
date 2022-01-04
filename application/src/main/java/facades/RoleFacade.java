package facades;

import dtos.RoleDTO;
import entities.Role;
import errorhandling.exceptions.RoleNotFoundException;
import errorhandling.exceptions.SanitizationException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import static utils.sanitizer.Role.sanitizeType;

/**
 *
 * @author Nicklas Nielsen
 */
public class RoleFacade {

    private static EntityManagerFactory emf = null;
    private static RoleFacade instance = null;

    private RoleFacade() {
        // private to ensure singleton
    }

    public static RoleFacade getRoleFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new RoleFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Role> getAll() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Role.getAll");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<RoleDTO> getAllDTOs() {
        List<RoleDTO> dtos = new ArrayList<>();

        getAll().forEach(role -> {
            dtos.add(new RoleDTO(role));
        });

        return dtos;
    }

    public Role getByType(String type) throws RoleNotFoundException, SanitizationException {
        type = sanitizeType(type);

        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Role.getByType");
            query.setParameter("type", type);

            return (Role) query.getSingleResult();
        } catch (NoResultException e) {
            throw new RoleNotFoundException();
        } finally {
            em.close();
        }
    }

    public RoleDTO getDTOByType(String type) throws RoleNotFoundException, SanitizationException {
        return new RoleDTO(getByType(type));
    }

    public List<Role> getAllDefaults() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Role.getDefaults");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<RoleDTO> getAllDefaultDTOs() {
        List<RoleDTO> dtos = new ArrayList<>();

        getAllDefaults().forEach(role -> {
            dtos.add(new RoleDTO(role));
        });

        return dtos;
    }

    public void create() {
        // HUSK TEST
        throw new UnsupportedOperationException();
    }

    public void edit() {
        // HUSK TEST
        throw new UnsupportedOperationException();
    }

}
