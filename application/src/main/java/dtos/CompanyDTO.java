package dtos;

import com.google.gson.annotations.SerializedName;
import entities.Company;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "name")
    private String name;

    @SerializedName(value = "cvr")
    private String cvr;

    @SerializedName(value = "company_statuses")
    private List<CompanyStatusDTO> companyStatusDTOs = new ArrayList<>();

    public CompanyDTO(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.cvr = company.getCvr();

        company.getCompanyStatuses().forEach(companyStatus -> {
            companyStatusDTOs.add(new CompanyStatusDTO(companyStatus));
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

    public String getCvr() {
        return cvr;
    }

    public void setCvr(String cvr) {
        this.cvr = cvr;
    }

    public List<CompanyStatusDTO> getCompanyStatusDTOs() {
        return companyStatusDTOs;
    }

    public void addCompanyStatusDTO(CompanyStatusDTO companyStatusDTO) {
        if (!companyStatusDTOs.contains(companyStatusDTO)) {
            companyStatusDTOs.add(companyStatusDTO);
            // TODO; Add
        }
    }

    public void removeCompanyStatusDTO(CompanyStatusDTO companyStatusDTO) {
        if (companyStatusDTOs.contains(companyStatusDTO)) {
            companyStatusDTOs.remove(companyStatusDTO);
            // TODO; Remove
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.cvr);
        hash = 41 * hash + Objects.hashCode(this.companyStatusDTOs);
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
        final CompanyDTO other = (CompanyDTO) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.cvr, other.cvr)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.companyStatusDTOs, other.companyStatusDTOs);
    }

}
