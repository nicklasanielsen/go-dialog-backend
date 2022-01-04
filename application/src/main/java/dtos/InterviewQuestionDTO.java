package dtos;

import com.google.gson.annotations.SerializedName;
import entities.InterviewQuestion;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class InterviewQuestionDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "question")
    private String question;

    @SerializedName(value = "answers")
    private List<InterviewQuestionAnswerDTO> interviewQuestionAnswers;

    public InterviewQuestionDTO(InterviewQuestion interviewQuestion) {
        id = interviewQuestion.getId();
        question = interviewQuestion.getQuestion();

        interviewQuestionAnswers = new ArrayList<>();

        interviewQuestion.getInterviewQuestionAnswers().forEach(answer -> {
            interviewQuestionAnswers.add(new InterviewQuestionAnswerDTO(answer));
        });
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<InterviewQuestionAnswerDTO> getInterviewQuestionAnswers() {
        return interviewQuestionAnswers;
    }

    public void addInterviewQuestionAnswer(InterviewQuestionAnswerDTO interviewQuestionAnswer) {
        if (!interviewQuestionAnswers.contains(interviewQuestionAnswer)) {
            interviewQuestionAnswers.add(interviewQuestionAnswer);
        }
    }

    public void removeInterviewQuestionAnswer(InterviewQuestionAnswerDTO interviewQuestionAnswer) {
        if (interviewQuestionAnswers.contains(interviewQuestionAnswer)) {
            interviewQuestionAnswers.remove(interviewQuestionAnswer);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.question);
        hash = 89 * hash + Objects.hashCode(this.interviewQuestionAnswers);
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
        final InterviewQuestionDTO other = (InterviewQuestionDTO) obj;
        if (!Objects.equals(this.question, other.question)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.interviewQuestionAnswers, other.interviewQuestionAnswers)) {
            return false;
        }
        return true;
    }

}
