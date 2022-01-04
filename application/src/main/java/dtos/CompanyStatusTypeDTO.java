package dtos;

import com.google.gson.annotations.SerializedName;
import entities.CompanyStatusType;
import java.util.Objects;

/**
 *
 * @author Nicklas Nielsen
 */
public class CompanyStatusTypeDTO {

    @SerializedName(value = "type")
    private String type;

    @SerializedName(value = "default")
    private boolean isDefault;

    public CompanyStatusTypeDTO(CompanyStatusType companyStatusType) {
        type = companyStatusType.getType();
        isDefault = companyStatusType.isDefault();
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
        hash = 17 * hash + Objects.hashCode(this.type);
        hash = 17 * hash + (this.isDefault ? 1 : 0);
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
        final CompanyStatusTypeDTO other = (CompanyStatusTypeDTO) obj;
        if (this.isDefault != other.isDefault) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }

}
