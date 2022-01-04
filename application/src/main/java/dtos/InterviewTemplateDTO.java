package dtos;

import com.google.gson.annotations.SerializedName;
import entities.InterviewTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class InterviewTemplateDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "name")
    private String name;

    @SerializedName(value = "amount_of_managers_allowed")
    private int amountOfManagersAllowed;

    @SerializedName(value = "amount_of_employees_allowed")
    private int amountOfEmployeesAllowed;

    @SerializedName(value = "questions")
    private List<InterviewQuestionTemplateDTO> questions;

    public InterviewTemplateDTO(InterviewTemplate interviewTemplate) {
        id = interviewTemplate.getId();
        name = interviewTemplate.getName();
        amountOfManagersAllowed = interviewTemplate.getAmountOfManagersAllowed();
        amountOfEmployeesAllowed = interviewTemplate.getAmountOfEmployeesAllowed();
        questions = new ArrayList<>();

        interviewTemplate.getInterviewQuestionTemplates().forEach(template -> {
            questions.add(new InterviewQuestionTemplateDTO(template));
        });
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

    public List<InterviewQuestionTemplateDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<InterviewQuestionTemplateDTO> questions) {
        this.questions = questions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + this.amountOfManagersAllowed;
        hash = 67 * hash + this.amountOfEmployeesAllowed;
        hash = 67 * hash + Objects.hashCode(this.questions);
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
        final InterviewTemplateDTO other = (InterviewTemplateDTO) obj;
        if (this.amountOfManagersAllowed != other.amountOfManagersAllowed) {
            return false;
        }
        if (this.amountOfEmployeesAllowed != other.amountOfEmployeesAllowed) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.questions, other.questions)) {
            return false;
        }
        return true;
    }

}
