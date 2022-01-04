package dtos;

import com.google.gson.annotations.SerializedName;
import entities.Person;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class PersonDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "firstname")
    private String firstname;

    @SerializedName(value = "middlename")
    private String middlename;

    @SerializedName(value = "lastname")
    private String lastname;

    @SerializedName(value = "fullname")
    private String fullname;

    public PersonDTO(Person person) {
        if (person != null) {
            id = person.getId();
            firstname = person.getFirstname();
            middlename = person.getMiddlename();
            lastname = person.getLastname();
        } else {
            firstname = "UNKNOWN";
            middlename = "UNKNOWN";
            lastname = "UNKNOWN";
        }

        if (!middlename.isEmpty()) {
            fullname = String.format("%s %s %s", firstname, middlename, lastname);
        } else {
            fullname = String.format("%s %s", firstname, lastname);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.firstname);
        hash = 97 * hash + Objects.hashCode(this.middlename);
        hash = 97 * hash + Objects.hashCode(this.lastname);
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
        final PersonDTO other = (PersonDTO) obj;
        if (!Objects.equals(this.firstname, other.firstname)) {
            return false;
        }
        if (!Objects.equals(this.middlename, other.middlename)) {
            return false;
        }
        if (!Objects.equals(this.lastname, other.lastname)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
