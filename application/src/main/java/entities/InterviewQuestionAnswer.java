package entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "interview_question_answers")
public class InterviewQuestionAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToOne
    private InterviewQuestion interviewQuestion;

    @ManyToOne
    private User user;
    private String answer;

    public InterviewQuestionAnswer() {
        id = UUID.randomUUID().toString();
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public InterviewQuestion getInterviewQuestion() {
        return interviewQuestion;
    }

    public void setInterviewQuestion(InterviewQuestion interviewQuestion) {
        this.interviewQuestion = interviewQuestion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.interviewQuestion);
        hash = 29 * hash + Objects.hashCode(this.user);
        hash = 29 * hash + Objects.hashCode(this.answer);
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
        final InterviewQuestionAnswer other = (InterviewQuestionAnswer) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.answer, other.answer)) {
            return false;
        }
        if (!Objects.equals(this.interviewQuestion, other.interviewQuestion)) {
            return false;
        }
        return Objects.equals(this.user, other.user);
    }

}
