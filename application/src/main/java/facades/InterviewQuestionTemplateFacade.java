package facades;

import dtos.InterviewQuestionTemplateDTO;
import entities.InterviewQuestionTemplate;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.InterviewQuestionTemplateNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import static utils.sanitizer.InterviewQuestionTemplate.sanitizeName;
import static utils.sanitizer.InterviewQuestionTemplate.sanitizeQuestion;

/**
 *
 * @author Nicklas Nielsen
 */
public class InterviewQuestionTemplateFacade {

    private static EntityManagerFactory emf = null;
    private static InterviewQuestionTemplateFacade instance = null;

    private InterviewQuestionTemplateFacade() {
        // Private to ensure singleton
    }

    public static InterviewQuestionTemplateFacade getInterviewQuestionTemplateFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new InterviewQuestionTemplateFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<InterviewQuestionTemplate> getAll() {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("InterviewQuestionTemplate.getAll");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<InterviewQuestionTemplateDTO> getAllDTOs() {
        List<InterviewQuestionTemplateDTO> interviewQuestionTemplateDTOs = new ArrayList<>();

        getAll().forEach(template -> {
            interviewQuestionTemplateDTOs.add(new InterviewQuestionTemplateDTO(template));
        });

        return interviewQuestionTemplateDTOs;
    }

    public void create(String name, String question) throws DatabaseException {
        name = sanitizeName(name);
        question = sanitizeQuestion(question);

        InterviewQuestionTemplate interviewQuestionTemplate = new InterviewQuestionTemplate(name, question);

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(interviewQuestionTemplate);
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

    public void delete(UUID id) throws DatabaseException {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            InterviewQuestionTemplate interviewQuestionTemplate = em.find(InterviewQuestionTemplate.class, id.toString());
            em.remove(interviewQuestionTemplate);
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

    public InterviewQuestionTemplate getById(UUID id) throws InterviewQuestionTemplateNotFoundException {
        EntityManager em = getEntityManager();

        try {
            InterviewQuestionTemplate interviewQuestionTemplate = em.find(InterviewQuestionTemplate.class, id.toString());

            if (interviewQuestionTemplate == null) {
                throw new InterviewQuestionTemplateNotFoundException();
            }

            return interviewQuestionTemplate;
        } finally {
            em.close();
        }
    }

    public InterviewQuestionTemplateDTO getDTOById(UUID id) throws InterviewQuestionTemplateNotFoundException {
        return new InterviewQuestionTemplateDTO(getById(id));
    }

    public void edit(InterviewQuestionTemplate template, String name, String question) throws DatabaseException {
        name = sanitizeName(name);
        question = sanitizeQuestion(question);

        template.setName(name);
        template.setQuestion(question);

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
