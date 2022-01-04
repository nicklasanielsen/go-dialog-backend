package facades;

import dtos.InterviewDTO;
import entities.Company;
import entities.Interview;
import entities.InterviewQuestion;
import entities.InterviewQuestionAnswer;
import entities.InterviewTemplate;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.InterviewNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import utils.Email;

/**
 *
 * @author Nicklas Nielsen
 */
public class InterviewFacade {

    private static EntityManagerFactory emf = null;
    private static InterviewFacade instance = null;

    private InterviewFacade() {
        // Private to ensure singleton
    }

    public static InterviewFacade getInterviewFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new InterviewFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Interview> getByCompany(Company company) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Interview.getByCompany");
            query.setParameter("company", company.getId().toString());

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<InterviewDTO> getDTOsByCompany(Company company) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getByCompany(company).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public List<Interview> getUpcomingByCompany(Company company) {
        List<Interview> interviews = getByCompany(company);
        List<Interview> upcoming = new ArrayList<>();

        interviews.stream().filter(interview -> (interview.getHeld().isAfter(LocalDate.now().atTime(LocalTime.MIN)))).forEachOrdered(interview -> {
            upcoming.add(interview);
        });

        return upcoming;
    }

    public List<InterviewDTO> getUpcomingDTOsByCompany(Company company) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getUpcomingByCompany(company).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public List<Interview> getPreviousByCompany(Company company) {
        List<Interview> interviews = getByCompany(company);
        List<Interview> previous = new ArrayList<>();

        interviews.stream().filter(interview -> (interview.getHeld().isBefore(LocalDate.now().atTime(LocalTime.MIN)))).forEachOrdered(interview -> {
            previous.add(interview);
        });

        return previous;
    }

    public List<InterviewDTO> getPreviousDTOsByCompany(Company company) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getPreviousByCompany(company).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public List<Interview> getByUser(User user) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Interview.getByEmployee");
            query.setParameter("employee", user.getId().toString());

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<InterviewDTO> getDTOsByUser(User user) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getByUser(user).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public List<Interview> getUpcomingsByUser(User user) {
        List<Interview> upcoming = new ArrayList<>();

        getByUser(user).stream().filter(interview -> (interview.getHeld().isAfter(LocalDate.now().atTime(LocalTime.MIN)))).forEachOrdered(interview -> {
            upcoming.add(interview);
        });

        return upcoming;
    }

    public List<InterviewDTO> getUpcomingDTOsByUser(User user) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getUpcomingsByUser(user).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public List<Interview> getPreviousByUser(User user) {
        List<Interview> previous = new ArrayList<>();

        getByUser(user).stream().filter(interview -> (interview.getHeld().isBefore(LocalDate.now().atTime(LocalTime.MIN)))).forEachOrdered(interview -> {
            previous.add(interview);
        });

        return previous;
    }

    public List<InterviewDTO> getPreviousDTOsByUser(User user) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getPreviousByUser(user).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public void create(InterviewTemplate template, LocalDateTime held, User manager, User employee) throws DatabaseException {
        Interview interview = new Interview(template, held);
        interview.addManager(manager);
        interview.addEmployee(employee);
        interview.setCompany(manager.getCompany());

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(interview);
            em.merge(manager);
            em.merge(employee);
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

    public Interview getById(UUID id) throws InterviewNotFoundException {
        EntityManager em = getEntityManager();

        try {
            Interview interview = em.find(Interview.class, id.toString());

            if (interview == null) {
                throw new InterviewNotFoundException();
            }

            return interview;
        } finally {
            em.close();
        }
    }

    public InterviewDTO getDTOById(UUID id) throws InterviewNotFoundException {
        return new InterviewDTO(getById(id));
    }

    public void delete(Interview interview) throws DatabaseException {
        interview.setDeleted(LocalDateTime.now());

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(interview);
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

    public void sendInvitation(Interview interview) {
        interview.getManagers().forEach(manager -> {
            Email.inviteToInterview(interview, manager);
        });

        interview.getEmployees().forEach(employee -> {
            Email.inviteToInterview(interview, employee);
        });
    }

    public void update(Interview interview, User user, String summary, Map<UUID, String> answersToQuestions) throws DatabaseException {
        boolean answered;
        int index;

        List<InterviewQuestionAnswer> answers = new ArrayList<>();

        interview.setSummary(summary);

        InterviewQuestionAnswer tmp;
        for (InterviewQuestion question : interview.getInterviewQuestions()) {
            System.out.println(question.getInterviewQuestionAnswers().size());
            answered = false;

            if (!answersToQuestions.containsKey(question.getId())) {
                continue;
            }

            for (InterviewQuestionAnswer answer : question.getInterviewQuestionAnswers()) {
                if (answer.getUser() != user) {
                    System.out.println("WRONG");
                    continue;
                }

                System.out.println("CORRECT");
                answer.setAnswer(answersToQuestions.get(question.getId()));

                answered = true;
                break;
            }

            if (!answered) {
                System.out.println("EJ BESVARET!");
                tmp = new InterviewQuestionAnswer();
                tmp.setUser(user);
                tmp.setAnswer(answersToQuestions.get(question.getId()));
                tmp.setInterviewQuestion(question);

                index = interview.getInterviewQuestions().indexOf(question);
                interview.getInterviewQuestions().get(index).addInterviewQuestionAnswer(tmp);
            }
        }

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            for (InterviewQuestionAnswer answer : answers) {
                em.merge(answer);
            }
            em.merge(interview);
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
