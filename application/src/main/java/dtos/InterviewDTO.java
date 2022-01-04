package dtos;

import com.google.gson.annotations.SerializedName;
import entities.Interview;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class InterviewDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "created")
    private String created;

    @SerializedName(value = "deleted")
    private String deleted;

    @SerializedName(value = "held")
    private String held;

    @SerializedName(value = "invitations_send")
    private String invitationSend;

    @SerializedName(value = "managers")
    private List<UserDTO> managers;

    @SerializedName(value = "employees")
    private List<UserDTO> employees;

    @SerializedName(value = "summary")
    private String summary;

    @SerializedName(value = "questions")
    private List<InterviewQuestionDTO> interviewQuestions;

    @SerializedName(value = "interview_template")
    private InterviewTemplateDTO interviewTemplate;

    public InterviewDTO(Interview interview) {
        id = interview.getId();

        if (interview.getCreated() != null) {
            created = interview.getCreated().toString();
        }

        if (interview.getDeleted() != null) {
            deleted = interview.getDeleted().toString();
        }

        if (interview.getHeld() != null) {
            held = interview.getHeld().toString();
        }

        if (interview.getInvitationSend() != null) {
            invitationSend = interview.getInvitationSend().toString();
        }

        summary = interview.getSummary();

        managers = new ArrayList<>();
        employees = new ArrayList<>();
        interviewQuestions = new ArrayList<>();

        interview.getManagers().forEach(manager -> {
            managers.add(new UserDTO(manager));
        });

        interview.getEmployees().forEach(employee -> {
            employees.add(new UserDTO(employee));
        });

        interview.getInterviewQuestions().forEach(question -> {
            interviewQuestions.add(new InterviewQuestionDTO(question));
        });

        interviewTemplate = new InterviewTemplateDTO(interview.getInterviewTemplate());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getHeld() {
        return held;
    }

    public void setHeld(String held) {
        this.held = held;
    }

    public String getInvitationSend() {
        return invitationSend;
    }

    public void setInvitationSend(String invitationSend) {
        this.invitationSend = invitationSend;
    }

    public List<UserDTO> getManagers() {
        return managers;
    }

    public void setManagers(List<UserDTO> managers) {
        this.managers = managers;
    }

    public List<UserDTO> getEmployees() {
        return employees;
    }

    public void setEmployees(List<UserDTO> employees) {
        this.employees = employees;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<InterviewQuestionDTO> getInterviewQuestions() {
        return interviewQuestions;
    }

    public void setInterviewQuestions(List<InterviewQuestionDTO> interviewQuestions) {
        this.interviewQuestions = interviewQuestions;
    }

    public InterviewTemplateDTO getInterviewTemplate() {
        return interviewTemplate;
    }

    public void setInterviewTemplate(InterviewTemplateDTO interviewTemplate) {
        this.interviewTemplate = interviewTemplate;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final InterviewDTO other = (InterviewDTO) obj;
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
        if (!Objects.equals(this.summary, other.summary)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.managers, other.managers)) {
            return false;
        }
        if (!Objects.equals(this.employees, other.employees)) {
            return false;
        }
        if (!Objects.equals(this.interviewQuestions, other.interviewQuestions)) {
            return false;
        }
        if (!Objects.equals(this.interviewTemplate, other.interviewTemplate)) {
            return false;
        }
        return true;
    }

}
