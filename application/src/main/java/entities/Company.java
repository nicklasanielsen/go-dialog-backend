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
@Table(name = "companies")
@NamedQueries({
    @NamedQuery(name = "Company.deleteAllRows", query = "DELETE FROM Company"),
    @NamedQuery(name = "Company.getAll", query = "SELECT c FROM Company c"),
    @NamedQuery(name = "Company.getByName", query = "SELECT c FROM Company c WHERE c.name = :name"),
    @NamedQuery(name = "Company.getByCvr", query = "SELECT c FROM Company c WHERE c.cvr = :cvr")
})
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CompanyStatus> companyStatuses;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<User> users;

    @Column(name = "name")
    private String name;

    @Column(name = "cvr")
    private String cvr;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Interview> interviews;

    public Company(String name, String cvr) {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.cvr = cvr;

        companyStatuses = new ArrayList<>();
        users = new ArrayList<>();
        interviews = new ArrayList<>();
    }

    public Company() {
        companyStatuses = new ArrayList<>();
        users = new ArrayList<>();
        interviews = new ArrayList<>();
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public List<CompanyStatus> getCompanyStatuses() {
        return companyStatuses;
    }

    public void addCompanyStatus(CompanyStatus companyStatus) {
        if (!companyStatuses.contains(companyStatus)) {
            companyStatuses.add(companyStatus);
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
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

    public List<Interview> getInterviews() {
        return interviews;
    }

    public void addInterview(Interview interview) {
        if (!interviews.contains(interview)) {
            interviews.add(interview);
        }
    }

    public void removeInterview(Interview interview) {
        if (interviews.contains(interview)) {
            interviews.remove(interview);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.companyStatuses);
        hash = 89 * hash + Objects.hashCode(this.users);
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.cvr);
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
        final Company other = (Company) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.cvr, other.cvr)) {
            return false;
        }
        if (!Objects.equals(this.companyStatuses, other.companyStatuses)) {
            return false;
        }
        if (!Objects.equals(this.users, other.users)) {
            return false;
        }
        return true;
    }

}
