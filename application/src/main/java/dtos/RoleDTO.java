package dtos;

import com.google.gson.annotations.SerializedName;
import entities.Role;
import java.util.Objects;

/**
 *
 * @author Nicklas Nielsen
 */
public class RoleDTO {

    @SerializedName(value = "type")
    private String type;

    @SerializedName(value = "default")
    private boolean isDefault;

    public RoleDTO(Role role) {
        type = role.getType();
        isDefault = role.isDefault();
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + (this.isDefault ? 1 : 0);
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
        final RoleDTO other = (RoleDTO) obj;
        if (this.isDefault != other.isDefault) {
            return false;
        }
        return Objects.equals(this.type, other.type);
    }

}
