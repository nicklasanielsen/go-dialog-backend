package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "interview_templates")
@NamedQueries({
    @NamedQuery(name = "InterviewTemplate.getAll", query = "SELECT i FROM InterviewTemplate i")
})
public class InterviewTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;
    private int amountOfManagersAllowed;
    private int amountOfEmployeesAllowed;

    @OneToMany(mappedBy = "interviewTemplate", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Interview> interviews;

    @ManyToMany
    @JoinTable(name = "lk_interview_templates_interview_question_templates", joinColumns = {
        @JoinColumn(name = "fk_interview_template", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "fk_interview_question_template", referencedColumnName = "id")
    })
    private List<InterviewQuestionTemplate> interviewQuestionTemplates;

    public InterviewTemplate(String name, int amountOfManagersAllowed, int amountOfEmployeesAllowed) {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.amountOfManagersAllowed = amountOfManagersAllowed;
        this.amountOfEmployeesAllowed = amountOfEmployeesAllowed;

        interviewQuestionTemplates = new ArrayList<>();
        interviews = new ArrayList<>();
    }

    public InterviewTemplate() {
        interviewQuestionTemplates = new ArrayList<>();
        interviews = new ArrayList<>();
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmountOfManagersAllowed() {
        return amountOfManagersAllowed;
    }

    public void setAmountOfManagersAllowed(int amountOfManagersAllowed) {
        this.amountOfManagersAllowed = amountOfManagersAllowed;
    }

    public int getAmountOfEmployeesAllowed() {
        return amountOfEmployeesAllowed;
    }

    public void setAmountOfEmployeesAllowed(int amountOfEmployeesAllowed) {
        this.amountOfEmployeesAllowed = amountOfEmployeesAllowed;
    }

    public List<InterviewQuestionTemplate> getInterviewQuestionTemplates() {
        return interviewQuestionTemplates;
    }

    public void addInterviewQuestionTemplate(InterviewQuestionTemplate interviewQuestionTemplate) {
        if (!interviewQuestionTemplates.contains(interviewQuestionTemplate)) {
            interviewQuestionTemplates.add(interviewQuestionTemplate);
            interviewQuestionTemplate.addInterviewTemplate(this);
        }
    }

    public void removeInterviewQuestionTemplate(InterviewQuestionTemplate interviewQuestionTemplate) {
        if (interviewQuestionTemplates.contains(interviewQuestionTemplate)) {
            interviewQuestionTemplates.remove(interviewQuestionTemplate);
            interviewQuestionTemplate.removeInterviewTemplate(this);
        }
    }

    public List<Interview> getInterviews() {
        return interviews;
    }

    public void addTemplate(Interview interview) {
        if (!interviews.contains(interview)) {
            interviews.add(interview);
        }
    }

    public void removeTemplate(Interview interview) {
        if (interviews.contains(interview)) {
            interviews.remove(interview);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final InterviewTemplate other = (InterviewTemplate) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
