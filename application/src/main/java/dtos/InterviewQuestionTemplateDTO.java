package dtos;

import com.google.gson.annotations.SerializedName;
import entities.InterviewQuestionTemplate;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class InterviewQuestionTemplateDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "name")
    private String name;

    @SerializedName(value = "question")
    private String question;

    public InterviewQuestionTemplateDTO(InterviewQuestionTemplate interviewQuestionTemplate) {
        id = interviewQuestionTemplate.getId();
        name = interviewQuestionTemplate.getName();
        question = interviewQuestionTemplate.getQuestion();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.question);
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
        final InterviewQuestionTemplateDTO other = (InterviewQuestionTemplateDTO) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.question, other.question)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
