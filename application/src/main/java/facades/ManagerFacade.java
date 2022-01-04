package facades;

import dtos.InterviewDTO;
import dtos.UserDTO;
import entities.Interview;
import entities.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 *
 * @author Nicklas Nielsen
 */
public class ManagerFacade {

    private static EntityManagerFactory emf = null;
    private static ManagerFacade instance = null;

    private ManagerFacade() {
        // Private to ensure singleton
    }

    public static ManagerFacade getManagerFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ManagerFacade();
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Interview> getByManager(User manager) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Interview.getByManager");
            query.setParameter("manager", manager.getId().toString());

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<InterviewDTO> getDTOsByManager(User manager) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getByManager(manager).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public List<Interview> getUpcomingByManager(User manager) {
        List<Interview> interviews = new ArrayList<>();

        getByManager(manager).stream().filter(interview -> (interview.getHeld().isAfter(LocalDate.now().atTime(LocalTime.MIN)))).forEachOrdered(interview -> {
            interviews.add(interview);
        });

        return interviews;
    }

    public List<InterviewDTO> getUpcomingsDTOsByManager(User manager) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getUpcomingByManager(manager).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public List<Interview> getPreviousByManager(User manager) {
        List<Interview> interviews = new ArrayList<>();

        getByManager(manager).stream().filter(interview -> (interview.getHeld().isBefore(LocalDate.now().atTime(LocalTime.MIN)))).forEachOrdered(interview -> {
            interviews.add(interview);
        });

        return interviews;
    }

    public List<InterviewDTO> getProviousDTOsByManager(User manager) {
        List<InterviewDTO> interviewDTOs = new ArrayList<>();

        getPreviousByManager(manager).forEach(interview -> {
            interviewDTOs.add(new InterviewDTO(interview));
        });

        return interviewDTOs;
    }

    public List<User> getEmployeesByManager(User manager) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("User.getByManager");
            query.setParameter("manager", manager.getId().toString());

            System.out.println(query);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<UserDTO> getEmployeeDTOsByManager(User manager) {
        List<UserDTO> userDTOs = new ArrayList<>();

        getEmployeesByManager(manager).forEach(user -> {
            userDTOs.add(new UserDTO(user));
        });
        System.out.println(userDTOs);
        return userDTOs;
    }

}
