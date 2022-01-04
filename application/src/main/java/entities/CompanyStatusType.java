package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "company_status_types")
@NamedQueries({
    @NamedQuery(name = "CompanyStatusType.getAll", query = "SELECT c FROM CompanyStatusType c"),
    @NamedQuery(name = "CompanyStatusType.getByType", query = "SELECT c FROM CompanyStatusType c WHERE c.type =:type"),
    @NamedQuery(name = "CompanyStatusType.getDefault", query = "SELECT c FROM CompanyStatusType c WHERE c.isDefault = TRUE"),
    @NamedQuery(name = "CompanyStatusType.deleteAllRows", query = "DELETE FROM CompanyStatusType")
})
public class CompanyStatusType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "type")
    private String type;

    @Column(name = "is_default")
    private boolean isDefault;

    @OneToMany(mappedBy = "companyStatusType", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CompanyStatus> companyStatuses;

    public CompanyStatusType(String type, boolean isDefault) {
        id = UUID.randomUUID().toString();
        this.type = type;
        this.isDefault = isDefault;

        companyStatuses = new ArrayList<>();
    }

    public CompanyStatusType() {
        companyStatuses = new ArrayList<>();
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public List<CompanyStatus> getCompanyStatuses() {
        return companyStatuses;
    }

    public void addCompanyStatus(CompanyStatus companyStatus) {
        if (!companyStatuses.contains(companyStatus)) {
            companyStatuses.add(companyStatus);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.type);
        hash = 31 * hash + (this.isDefault ? 1 : 0);
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
        final CompanyStatusType other = (CompanyStatusType) obj;
        if (this.isDefault != other.isDefault) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }

}
