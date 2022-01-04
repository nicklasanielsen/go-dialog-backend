package entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "persons")
@NamedQueries({
    @NamedQuery(name = "Person.deleteAllRows", query = "DELETE FROM Person"),
    @NamedQuery(name = "Person.getAll", query = "SELECT p FROM Person p"),
    @NamedQuery(name = "Person.getByUser", query = "SELECT p FROM Person p WHERE p.user.id = :user_id"),
    @NamedQuery(name = "Person.getByFirstname", query = "SELECT p FROM Person p WHERE UPPER(p.firstname) = :firstname"),
    @NamedQuery(name = "Person.getByFirstnameAndMiddlename", query = "SELECT p FROM Person p WHERE UPPER(p.firstname) = :firstname AND UPPER(p.middlename) = :middlename"),
    @NamedQuery(name = "Person.getByFirstnameAndLastname", query = "SELECT p FROM Person p WHERE UPPER(p.firstname) = :firstname AND UPPER(p.lastname) = :lastname"),
    @NamedQuery(name = "Person.getByFullname", query = "SELECT p FROM Person p WHERE UPPER(p.firstname) = :firstname AND UPPER(p.middlename) = :middlename AND UPPER(p.lastname) = :lastname"),
    @NamedQuery(name = "Person.getByMiddlename", query = "SELECT p FROM Person p WHERE UPPER(p.middlename) = :middlename"),
    @NamedQuery(name = "Person.getByMiddlenameAndLastname", query = "SELECT p FROM Person p WHERE UPPER(p.middlename) = :middlename AND UPPER(p.lastname) = :lastname"),
    @NamedQuery(name = "Person.getByLastname", query = "SELECT p FROM Person p WHERE UPPER(p.lastname) = :lastname")
})
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "middlename")
    private String middlename;

    @Column(name = "lastname")
    private String lastname;

    @OneToOne(mappedBy = "person")
    private User user;

    public Person(String firstname, String middlename, String lastname) {
        id = UUID.randomUUID().toString();
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
    }

    public Person() {

    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullname() {
        if (getMiddlename().isEmpty()) {
            return String.format("%s %s", getFirstname(), getLastname());
        }

        return String.format("%s %s %s", getFirstname(), getMiddlename(), getLastname());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.id);
        hash = 11 * hash + Objects.hashCode(this.firstname);
        hash = 11 * hash + Objects.hashCode(this.middlename);
        hash = 11 * hash + Objects.hashCode(this.lastname);

        if (this.user != null) {
            hash = 11 * hash + Objects.hashCode(this.user.getId());
        }

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
        final Person other = (Person) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.firstname, other.firstname)) {
            return false;
        }
        if (!Objects.equals(this.middlename, other.middlename)) {
            return false;
        }
        if (!Objects.equals(this.lastname, other.lastname)) {
            return false;
        }
        if (this.user != null) {
            return Objects.equals(this.user.getId(), other.user.getId());
        }

        return true;
    }

}
