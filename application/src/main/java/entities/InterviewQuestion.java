package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "interview_questions")
public class InterviewQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String question;

    @OneToMany(mappedBy = "interviewQuestion", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<InterviewQuestionAnswer> interviewQuestionAnswers;

    @ManyToOne
    private Interview interview;

    public InterviewQuestion(InterviewQuestionTemplate interviewQuestionTemplate) {
        id = UUID.randomUUID().toString();
        question = interviewQuestionTemplate.getQuestion();
        interviewQuestionAnswers = new ArrayList<>();
    }

    public InterviewQuestion() {

    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<InterviewQuestionAnswer> getInterviewQuestionAnswers() {
        return interviewQuestionAnswers;
    }

    public void addInterviewQuestionAnswer(InterviewQuestionAnswer interviewQuestionAnswer) {
        if (!interviewQuestionAnswers.contains(interviewQuestionAnswer)) {
            interviewQuestionAnswers.add(interviewQuestionAnswer);
            interviewQuestionAnswer.setInterviewQuestion(this);
        }
    }

    public void removeInterviewQuestionAnswer(InterviewQuestionAnswer interviewQuestionAnswer) {
        if (interviewQuestionAnswers.contains(interviewQuestionAnswer)) {
            interviewQuestionAnswers.remove(interviewQuestionAnswer);
            interviewQuestionAnswer.setInterviewQuestion(null);
        }
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.question);
        hash = 41 * hash + Objects.hashCode(this.interviewQuestionAnswers);
        hash = 41 * hash + Objects.hashCode(this.interview);
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
        final InterviewQuestion other = (InterviewQuestion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.question, other.question)) {
            return false;
        }
        if (this.interviewQuestionAnswers.size() != other.interviewQuestionAnswers.size()) {
            return false;
        }
        if (!this.interviewQuestionAnswers.stream().noneMatch(answer -> (!other.interviewQuestionAnswers.contains(answer)))) {
            return false;
        }
        return Objects.equals(this.interview, other.interview);
    }

}
