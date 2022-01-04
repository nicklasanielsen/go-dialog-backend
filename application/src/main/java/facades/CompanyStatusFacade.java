package facades;

import dtos.CompanyStatusDTO;
import entities.Company;
import entities.CompanyStatus;
import entities.CompanyStatusType;
import errorhandling.exceptions.CompanyStatusNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyStatusFacade {

    private static EntityManagerFactory emf = null;
    private static CompanyStatusFacade instance = null;

    private CompanyStatusFacade() {
        // private to ensure singleton
    }

    public static CompanyStatusFacade getCompanyStatusFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new CompanyStatusFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<CompanyStatus> getAll() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("CompanyStatus.getAll");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<CompanyStatusDTO> getAllDTOs() {
        List<CompanyStatusDTO> dtos = new ArrayList<>();

        getAll().forEach(companyStatus -> {
            dtos.add(new CompanyStatusDTO(companyStatus));
        });

        return dtos;
    }

    public CompanyStatus getById(UUID id) throws CompanyStatusNotFoundException {
        EntityManager em = getEntityManager();

        CompanyStatus companyStatus = em.find(CompanyStatus.class, id.toString());

        if (companyStatus == null) {
            throw new CompanyStatusNotFoundException();
        }

        return companyStatus;
    }

    public CompanyStatusDTO getDTOById(UUID id) throws CompanyStatusNotFoundException {
        CompanyStatus companyStatus = getById(id);

        return new CompanyStatusDTO(companyStatus);
    }

    public List<CompanyStatus> getAllRelatedToCompany(Company company) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("CompanyStatus.getAllRelatedToCompany");
            query.setParameter("id", company.getId().toString());

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<CompanyStatusDTO> getAllDTOsRelatedToCompany(Company company) {
        List<CompanyStatusDTO> dtos = new ArrayList<>();

        getAllRelatedToCompany(company).forEach(companyStatus -> {
            dtos.add(new CompanyStatusDTO(companyStatus));
        });

        return dtos;
    }

    public List<CompanyStatus> getAllByType(CompanyStatusType companyStatusType) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("CompanyStatus.getAllByType");
            query.setParameter("type", companyStatusType.getType());

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<CompanyStatusDTO> getAllDTOsByType(CompanyStatusType companyStatusType) {
        List<CompanyStatusDTO> dtos = new ArrayList<>();

        getAllByType(companyStatusType).forEach(companyStatus -> {
            dtos.add(new CompanyStatusDTO(companyStatus));
        });

        return dtos;
    }

    public List<CompanyStatus> getAllRelatedToCompanyByType(Company company, CompanyStatusType companyStatusType) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("CompanyStatus.getAllRelatedToCompanyByType");
            query.setParameter("id", company.getId().toString());
            query.setParameter("type", companyStatusType.getType());

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<CompanyStatusDTO> getAllDTOsRelatedToCompanyByType(Company company, CompanyStatusType companyStatusType) {
        List<CompanyStatusDTO> dtos = new ArrayList<>();

        getAllRelatedToCompanyByType(company, companyStatusType).forEach(companyStatus -> {
            dtos.add(new CompanyStatusDTO(companyStatus));
        });

        return dtos;
    }

    public void create() {
        // HUSK AT LAVE TESTS!
        throw new UnsupportedOperationException();
    }

    public void edit() {
        // HUSK AT LAVE TESTS!
        throw new UnsupportedOperationException();
    }

}
