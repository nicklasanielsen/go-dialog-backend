package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "interviews")
@NamedQueries({
    @NamedQuery(name = "Interview.getByCompany", query = "SELECT i FROM Interview i WHERE i.deleted = null AND i.company.id = :company"),
    @NamedQuery(name = "Interview.getByManager", query = "SELECT i FROM Interview i JOIN i.managers m WHERE i.deleted = null AND m.id = :manager"),
    @NamedQuery(name = "Interview.getByEmployee", query = "SELECT i FROM Interview i JOIN i.employees e WHERE i.deleted = null AND e.id = :employee")
})
public class Interview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private LocalDateTime created;
    private LocalDateTime deleted;
    private LocalDateTime held;
    private LocalDateTime invitationSend;

    @ManyToMany(mappedBy = "managerInterviews")
    private List<User> managers;

    @ManyToMany(mappedBy = "employeeInterviews")
    private List<User> employees;

    @ManyToOne
    private Company company;

    private String summary;

    @OneToMany(mappedBy = "interview", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<InterviewQuestion> interviewQuestions;

    @ManyToOne
    private InterviewTemplate interviewTemplate;

    public Interview(InterviewTemplate interviewTemplate, LocalDateTime held) {
        id = UUID.randomUUID().toString();
        created = LocalDateTime.now();
        this.held = held;

        managers = new ArrayList<>();
        employees = new ArrayList<>();
        interviewQuestions = new ArrayList<>();

        interviewTemplate.getInterviewQuestionTemplates().forEach(interviewQuestionTemplate -> {
            interviewQuestions.add(new InterviewQuestion(interviewQuestionTemplate));
        });

        this.interviewTemplate = interviewTemplate;
        interviewTemplate.addTemplate(this);
    }

    public Interview() {

    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getDeleted() {
        return deleted;
    }

    public void setDeleted(LocalDateTime deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getHeld() {
        return held;
    }

    public void setHeld(LocalDateTime held) {
        this.held = held;
    }

    public LocalDateTime getInvitationSend() {
        return invitationSend;
    }

    public void setInvitationSend(LocalDateTime invitationSend) {
        this.invitationSend = invitationSend;
    }

    public List<User> getManagers() {
        return managers;
    }

    public void addManager(User manager) {
        if (!managers.contains(manager)) {
            managers.add(manager);
            manager.addManagerInterview(this);
        }
    }

    public void removeManager(User manager) {
        if (managers.contains(manager)) {
            managers.remove(manager);
            manager.removeManagerInterview(this);
        }
    }

    public List<User> getEmployees() {
        return employees;
    }

    public void addEmployee(User employee) {
        if (!employees.contains(employee)) {
            employees.add(employee);
            employee.addEmployeeInterview(this);
        }
    }

    public void removeEmployee(User employee) {
        if (employees.contains(employee)) {
            employees.remove(employee);
            employee.removeEmployeeInterview(this);
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<InterviewQuestion> getInterviewQuestions() {
        return interviewQuestions;
    }

    public void addInterviewQuestion(InterviewQuestion interviewQuestion) {
        if (!interviewQuestions.contains(interviewQuestion)) {
            interviewQuestions.add(interviewQuestion);
            interviewQuestion.setInterview(this);
        }
    }

    public void removeInterviewQuestion(InterviewQuestion interviewQuestion) {
        if (interviewQuestions.contains(interviewQuestion)) {
            interviewQuestions.remove(interviewQuestion);
            interviewQuestion.setInterview(null);
        }
    }

    public InterviewTemplate getInterviewTemplate() {
        return interviewTemplate;
    }

    public void setInterviewTemplate(InterviewTemplate interviewTemplate) {
        this.interviewTemplate = interviewTemplate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
        company.addInterview(this);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.created);
        hash = 89 * hash + Objects.hashCode(this.deleted);
        hash = 89 * hash + Objects.hashCode(this.held);
        hash = 89 * hash + Objects.hashCode(this.invitationSend);
        hash = 89 * hash + Objects.hashCode(this.managers);
        hash = 89 * hash + Objects.hashCode(this.employees);
        hash = 89 * hash + Objects.hashCode(this.summary);
        hash = 89 * hash + Objects.hashCode(this.interviewQuestions);
        hash = 89 * hash + Objects.hashCode(this.interviewTemplate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Interview other = (Interview) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.summary, other.summary)) {
            return false;
        }
        if (!Objects.equals(this.created, other.created)) {
            return false;
        }
        if (!Objects.equals(this.deleted, other.deleted)) {
            return false;
        }
        if (!Objects.equals(this.held, other.held)) {
            return false;
        }
        if (!Objects.equals(this.invitationSend, other.invitationSend)) {
            return false;
        }
        if (!Objects.equals(this.managers, other.managers)) {
            return false;
        }
        if (this.managers.size() != other.managers.size()) {
            return false;
        }
        if (!this.managers.stream().noneMatch(manager -> (!other.managers.contains(manager)))) {
            return false;
        }
        if (this.employees.size() != other.employees.size()) {
            return false;
        }
        if (!this.employees.stream().noneMatch(employee -> (!other.employees.contains(employee)))) {
            return false;
        }
        if (this.interviewQuestions.size() != other.interviewQuestions.size()) {
            return false;
        }
        if (!this.interviewQuestions.stream().noneMatch(question -> (!other.interviewQuestions.contains(question)))) {
            return false;
        }
        return Objects.equals(this.interviewTemplate, other.interviewTemplate);
    }

}
