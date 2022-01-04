package dtos;

import com.google.gson.annotations.SerializedName;
import entities.InterviewQuestionAnswer;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class InterviewQuestionAnswerDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "user_id")
    private UUID userId;

    @SerializedName(value = "user_fullname")
    private String userFullname;

    @SerializedName(value = "answer")
    private String answer;

    public InterviewQuestionAnswerDTO(InterviewQuestionAnswer interviewQuestionAnswer) {
        id = interviewQuestionAnswer.getId();
        userId = interviewQuestionAnswer.getUser().getId();
        userFullname = interviewQuestionAnswer.getUser().getFullname();
        answer = interviewQuestionAnswer.getAnswer();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.userId);
        hash = 97 * hash + Objects.hashCode(this.userFullname);
        hash = 97 * hash + Objects.hashCode(this.answer);
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
        final InterviewQuestionAnswerDTO other = (InterviewQuestionAnswerDTO) obj;
        if (!Objects.equals(this.userFullname, other.userFullname)) {
            return false;
        }
        if (!Objects.equals(this.answer, other.answer)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        return true;
    }

}
