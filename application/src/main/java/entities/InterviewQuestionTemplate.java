package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "interview_question_templates")
@NamedQueries({
    @NamedQuery(name = "InterviewQuestionTemplate.getAll", query = "SELECT i FROM InterviewQuestionTemplate i")
})
public class InterviewQuestionTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;
    private String question;

    @ManyToMany(mappedBy = "interviewQuestionTemplates")
    private List<InterviewTemplate> interviewTemplates;

    public InterviewQuestionTemplate(String name, String question) {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.question = question;
        interviewTemplates = new ArrayList<>();
    }

    public InterviewQuestionTemplate() {

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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<InterviewTemplate> getInterviewTemplates() {
        return interviewTemplates;
    }

    public void addInterviewTemplate(InterviewTemplate interviewTemplate) {
        if (!interviewTemplates.contains(interviewTemplate)) {
            interviewTemplates.add(interviewTemplate);
            interviewTemplate.addInterviewQuestionTemplate(this);
        }
    }

    public void removeInterviewTemplate(InterviewTemplate interviewTemplate) {
        if (interviewTemplates.contains(interviewTemplate)) {
            interviewTemplates.remove(interviewTemplate);
            interviewTemplate.removeInterviewQuestionTemplate(this);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.id);
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
        final InterviewQuestionTemplate other = (InterviewQuestionTemplate) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
