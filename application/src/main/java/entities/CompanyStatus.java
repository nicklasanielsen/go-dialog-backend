package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "company_statuses")
@NamedQueries({
    @NamedQuery(name = "CompanyStatus.deleteAllRows", query = "DELETE FROM CompanyStatus"),
    @NamedQuery(name = "CompanyStatus.getAll", query = "SELECT c FROM CompanyStatus c"),
    @NamedQuery(name = "CompanyStatus.getAllRelatedToCompany", query = "SELECT c FROM CompanyStatus c JOIN c.company co WHERE co.id = :id"),
    @NamedQuery(name = "CompanyStatus.getAllByType", query = "SELECT c FROM CompanyStatus c JOIN c.companyStatusType t WHERE t.type = :type"),
    @NamedQuery(name = "CompanyStatus.getAllRelatedToCompanyByType", query = "SELECT c FROM CompanyStatus c JOIN c.company co JOIN c.companyStatusType t WHERE co.id = :id AND t.type = :type")
})
public class CompanyStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_company")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_company_status_type")
    private CompanyStatusType companyStatusType;

    @Column(name = "start_at")
    private LocalDateTime start;

    @Column(name = "end_at")
    private LocalDateTime end;

    public CompanyStatus(LocalDateTime start) {
        id = UUID.randomUUID().toString();
        this.start = start;
    }

    public CompanyStatus(LocalDateTime start, LocalDateTime end) {
        id = UUID.randomUUID().toString();
        this.start = start;
        this.end = end;
    }

    public CompanyStatus() {

    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
        company.addCompanyStatus(this);
    }

    public CompanyStatusType getCompanyStatusType() {
        return companyStatusType;
    }

    public void setCompanyStatusType(CompanyStatusType companyStatusType) {
        this.companyStatusType = companyStatusType;
        companyStatusType.addCompanyStatus(this);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.companyStatusType);
        hash = 79 * hash + Objects.hashCode(this.start);
        hash = 79 * hash + Objects.hashCode(this.end);
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
        final CompanyStatus other = (CompanyStatus) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.companyStatusType, other.companyStatusType)) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        return Objects.equals(this.end, other.end);
    }

}
