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
import static utils.sanitizer.CompanyStatusType.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyStatusTypeFacade {

    private static EntityManagerFactory emf = null;
    private static CompanyStatusTypeFacade instance = null;

    private CompanyStatusTypeFacade() {
        // private to ensure singleton
    }

    public static CompanyStatusTypeFacade getFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new CompanyStatusTypeFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<CompanyStatusType> getAll() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("CompanyStatusType.getAll");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<CompanyStatusTypeDTO> getAllDTOs() {
        List<CompanyStatusType> companyStatusTypes = getAll();
        List<CompanyStatusTypeDTO> companyStatusTypeDTOs = new ArrayList<>();

        companyStatusTypes.forEach(companyStatusType -> {
            companyStatusTypeDTOs.add(new CompanyStatusTypeDTO(companyStatusType));
        });

        return companyStatusTypeDTOs;
    }

    public CompanyStatusType getByType(String type) throws CompanyStatusTypeNotFoundException, SanitizationException {
        type = sanitizeType(type);

        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("CompanyStatusType.getByType");
            query.setParameter("type", type);

            return (CompanyStatusType) query.getSingleResult();
        } catch (NoResultException e) {
            throw new CompanyStatusTypeNotFoundException();
        } finally {
            em.close();
        }
    }

    public CompanyStatusTypeDTO getDTOByType(String type) throws CompanyStatusTypeNotFoundException, SanitizationException {
        CompanyStatusType companyStatusType = getByType(type);
        return new CompanyStatusTypeDTO(companyStatusType);
    }

    public CompanyStatusType getDefault() throws CompanyStatusTypeNotFoundException, DatabaseException {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("CompanyStatusType.getDefault");

            return (CompanyStatusType) query.getSingleResult();
        } catch (NoResultException e) {
            throw new CompanyStatusTypeNotFoundException();
        } catch (NonUniqueResultException exx) {
            throw new DatabaseException();
        } finally {
            em.close();
        }
    }

    public CompanyStatusTypeDTO getDefaultDTO() throws CompanyStatusTypeNotFoundException, DatabaseException {
        CompanyStatusType companyStatusType = getDefault();
        return new CompanyStatusTypeDTO(companyStatusType);
    }

    public void createNew(String type, boolean isDefault) throws SanitizationException, DatabaseException, CompanyStatusTypeCreationException {
        type = sanitizeType(type);

        try {
            getByType(type);

            throw new CompanyStatusTypeCreationException();
        } catch (CompanyStatusTypeNotFoundException e) {
            CompanyStatusType companyStatusType = new CompanyStatusType(type, isDefault);

            EntityManager em = getEntityManager();

            try {
                em.getTransaction().begin();

                if (companyStatusType.isDefault()) {
                    try {
                        CompanyStatusType existingDefault = getDefault();
                        existingDefault.setDefault(false);
                        em.merge(existingDefault);
                    } catch (CompanyStatusTypeNotFoundException ex) {

                    }
                }

                em.persist(companyStatusType);
                em.getTransaction().commit();
            } catch (Exception exx) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw new DatabaseException();
            } finally {
                em.close();
            }
        }
    }

    public void edit(String oldType, String newType, boolean isDefault) throws SanitizationException, DatabaseException, CompanyStatusTypeEditException {
        oldType = sanitizeType(oldType);
        newType = sanitizeType(newType);

        CompanyStatusType currentCompanyStatusType = null, tmpCompanyStatusType = null;

        try {
            currentCompanyStatusType = getByType(oldType);

            if (!oldType.equals(newType)) {
                tmpCompanyStatusType = getByType(newType);
            }
        } catch (CompanyStatusTypeNotFoundException e) {
            if (currentCompanyStatusType == null) {
                throw new CompanyStatusTypeEditException();
            }
        }

        if (tmpCompanyStatusType != null) {
            throw new CompanyStatusTypeEditException();
        }

        currentCompanyStatusType.setType(newType);

        if (!currentCompanyStatusType.isDefault() && isDefault) {
            try {
                tmpCompanyStatusType = getDefault();
                tmpCompanyStatusType.setDefault(false);
            } catch (CompanyStatusTypeNotFoundException e) {

            }

            currentCompanyStatusType.setDefault(true);
        }

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(currentCompanyStatusType);

            if (tmpCompanyStatusType != null) {
                em.merge(tmpCompanyStatusType);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        }
    }

}
