package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Nicklas Nielsen
 */
@Entity
@Table(name = "roles")
@NamedQueries({
    @NamedQuery(name = "Role.deleteAllRows", query = "DELETE FROM Role"),
    @NamedQuery(name = "Role.getAll", query = "SELECT r FROM Role r"),
    @NamedQuery(name = "Role.getByType", query = "SELECT r FROM Role r WHERE r.type = :type"),
    @NamedQuery(name = "Role.getDefaults", query = "SELECT r FROM Role r WHERE r.isDefault = TRUE")
})
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String type;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    @Column(name = "is_default")
    private boolean isDefault;

    public Role(String type) {
        this.type = type;
        this.isDefault = false;

        users = new ArrayList<>();
    }

    public Role(String type, boolean isDefault) {
        this.type = type;
        this.isDefault = isDefault;

        users = new ArrayList<>();
    }

    public Role() {
        users = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            user.addRole(this);
        }
    }

    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
            user.removeRole(this);
        }
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.type);

        for (User user : users) {
            hash = 73 * hash + Objects.hashCode(user.getId());
        }

        hash = 73 * hash + (this.isDefault ? 1 : 0);
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
        final Role other = (Role) obj;
        if (this.isDefault != other.isDefault) {
            return false;
        }
        if (this.users.size() != other.users.size()) {
            return false;
        }
        if (!this.users.stream().noneMatch(user -> (!other.users.contains(user)))) {
            return false;
        }

        return Objects.equals(this.type, other.type);
    }

}
