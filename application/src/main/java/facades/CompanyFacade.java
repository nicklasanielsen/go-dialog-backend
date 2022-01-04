package facades;

import dtos.CompanyDTO;
import dtos.UserDTO;
import entities.Company;
import entities.User;
import errorhandling.exceptions.CompanyNotFoundException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.SanitizationException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import utils.Email;
import static utils.sanitizer.Company.sanitizeCvr;
import static utils.sanitizer.Company.sanitizeName;
import static utils.sanitizer.User.sanitizeEmail;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyFacade {

    private static EntityManagerFactory emf = null;
    private static CompanyFacade instance = null;

    private CompanyFacade() {
        // private to ensure singleton
    }

    public static CompanyFacade getCompanyFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new CompanyFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Company> getAll() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Company.getAll");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<CompanyDTO> getAllDTOs() {
        List<CompanyDTO> dtos = new ArrayList<>();

        getAll().forEach(company -> {
            dtos.add(new CompanyDTO(company));
        });

        return dtos;
    }

    public Company getById(UUID id) throws CompanyNotFoundException {
        EntityManager em = getEntityManager();

        Company company = em.find(Company.class, id.toString());

        if (company == null) {
            throw new CompanyNotFoundException();
        }

        return company;
    }

    public CompanyDTO getDTOById(UUID id) throws CompanyNotFoundException {
        Company company = getById(id);

        return new CompanyDTO(company);
    }

    public List<Company> getAllByName(String name) throws SanitizationException {
        name = sanitizeName(name);

        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Company.getByName");
            query.setParameter("name", name);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<CompanyDTO> getAllDTOsByName(String name) throws SanitizationException {
        List<CompanyDTO> dtos = new ArrayList<>();
        name = sanitizeName(name);

        for (Company company : getAllByName(name)) {
            dtos.add(new CompanyDTO(company));
        }

        return dtos;
    }

    public List<Company> getAllByCvr(String cvr) throws SanitizationException {
        cvr = sanitizeCvr(cvr);

        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Company.getByCvr");
            query.setParameter("cvr", cvr);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<CompanyDTO> getAllDTOsByCvr(String cvr) throws SanitizationException {
        List<CompanyDTO> dtos = new ArrayList<>();
        cvr = sanitizeCvr(cvr);

        for (Company company : getAllByCvr(cvr)) {
            dtos.add(new CompanyDTO(company));
        }

        return dtos;
    }

    public Company create(String cvr, String name) throws SanitizationException, DatabaseException {
        // HUSK AT SKRIVE TESTS!

        cvr = sanitizeCvr(cvr);
        name = sanitizeName(name);

        EntityManager em = getEntityManager();

        Company company = new Company(name, cvr);

        try {
            em.getTransaction().begin();
            em.persist(company);
            em.getTransaction().commit();

            return company;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        } finally {
            em.close();
        }
    }

    public void delete(UUID id) throws DatabaseException {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            Company company = em.find(Company.class, id.toString());
            em.remove(company);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        }
    }

    public List<User> getManagersByCompany(Company company) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("User.getByRoleAndCompany");
            query.setParameter("role", "MANAGER");
            query.setParameter("company", company.getId().toString());

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<UserDTO> getManagerDTOsByCompany(Company company) {
        List<UserDTO> managerDTOs = new ArrayList<>();

        getManagersByCompany(company).forEach(manager -> {
            managerDTOs.add(new UserDTO(manager));
        });

        return managerDTOs;
    }

    public List<User> getEmployeesByCompany(Company company) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("User.getByRoleAndCompany");
            query.setParameter("role", "USER");
            query.setParameter("company", company.getId().toString());

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<UserDTO> getEmployeeDTOsByCompany(Company company) {
        List<UserDTO> employeeDTOs = new ArrayList<>();

        getEmployeesByCompany(company).forEach(employee -> {
            employeeDTOs.add(new UserDTO(employee));
        });

        return employeeDTOs;
    }

    public void inviteUser(Company company, String email) throws SanitizationException {
        email = sanitizeEmail(email);

        Email.inviteToPlatform(email, company);
    }

    public void edit(Company company, String name, String cvr) throws SanitizationException, DatabaseException {
        name = sanitizeName(name);
        cvr = sanitizeCvr(cvr);

        company.setName(name);
        company.setCvr(cvr);

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(company);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        } finally {
            em.close();
        }

    }

}
