package dtos;

import com.google.gson.annotations.SerializedName;
import entities.CompanyStatus;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyStatusDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "company_status_type")
    private CompanyStatusTypeDTO companyStatusTypeDTO;

    @SerializedName(value = "start")
    private String start;

    @SerializedName(value = "end")
    private String end;

    public CompanyStatusDTO(CompanyStatus companyStatus) {
        this.id = companyStatus.getId();

        if (companyStatus.getCompanyStatusType() != null) {
            this.companyStatusTypeDTO = new CompanyStatusTypeDTO(companyStatus.getCompanyStatusType());
        }

        if (companyStatus.getStart() != null) {
            this.start = companyStatus.getStart().toString();
        }

        if (companyStatus.getEnd() != null) {
            this.end = companyStatus.getEnd().toString();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CompanyStatusTypeDTO getCompanyStatusTypeDTO() {
        return companyStatusTypeDTO;
    }

    public void setCompanyStatusTypeDTO(CompanyStatusTypeDTO companyStatusTypeDTO) {
        this.companyStatusTypeDTO = companyStatusTypeDTO;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
        hash = 17 * hash + Objects.hashCode(this.companyStatusTypeDTO);
        hash = 17 * hash + Objects.hashCode(this.start);
        hash = 17 * hash + Objects.hashCode(this.end);
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
        final CompanyStatusDTO other = (CompanyStatusDTO) obj;
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.end, other.end)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.companyStatusTypeDTO, other.companyStatusTypeDTO)) {
            return false;
        }
        return true;
    }

}
