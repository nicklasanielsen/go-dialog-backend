package facades;

import dtos.InterviewTemplateDTO;
import entities.InterviewQuestionTemplate;
import entities.InterviewTemplate;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.InterviewTemplateNotFoundException;
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
public class InterviewTemplateFacade {

    private static EntityManagerFactory emf = null;
    private static InterviewTemplateFacade instance = null;

    private InterviewTemplateFacade() {
        // Pricate to ensure singleton
    }

    public static InterviewTemplateFacade getInterviewTemplateFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new InterviewTemplateFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<InterviewTemplate> getAll() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("InterviewTemplate.getAll");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<InterviewTemplateDTO> getAllDTOs() {
        List<InterviewTemplateDTO> interviewTemplateDTOs = new ArrayList<>();

        getAll().forEach(template -> {
            interviewTemplateDTOs.add(new InterviewTemplateDTO(template));
        });

        return interviewTemplateDTOs;
    }

    public void create(String name, int amountOfManagers, int amountOfEmployees) throws DatabaseException {
        InterviewTemplate interviewTemplate = new InterviewTemplate(name, amountOfManagers, amountOfEmployees);

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(interviewTemplate);
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

    public InterviewTemplate getById(UUID id) throws InterviewTemplateNotFoundException {
        EntityManager em = getEntityManager();

        try {
            InterviewTemplate interviewTemplate = em.find(InterviewTemplate.class, id.toString());

            if (interviewTemplate == null) {
                throw new InterviewTemplateNotFoundException();
            }

            return interviewTemplate;
        } finally {
            em.close();
        }
    }

    public InterviewTemplateDTO getDTOById(UUID id) throws InterviewTemplateNotFoundException {
        return new InterviewTemplateDTO(getById(id));
    }

    public void delete(UUID id) throws DatabaseException {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();

            InterviewTemplate interviewTemplate = em.find(InterviewTemplate.class, id.toString());

            em.remove(interviewTemplate);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println(e);
            throw new DatabaseException();
        } finally {
            em.close();
        }
    }

    public void addQuestion(InterviewTemplate template, InterviewQuestionTemplate question) throws DatabaseException {
        template.addInterviewQuestionTemplate(question);

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(template);
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

    public void removeQuestion(InterviewTemplate template, InterviewQuestionTemplate question) throws DatabaseException {
        template.removeInterviewQuestionTemplate(question);

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(template);
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

    public void edit(InterviewTemplate template, String name, int amountOfManagers, int amountOfEmployees) throws DatabaseException {
        template.setName(name);
        template.setAmountOfManagersAllowed(amountOfManagers);
        template.setAmountOfEmployeesAllowed(amountOfEmployees);

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(template);
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
