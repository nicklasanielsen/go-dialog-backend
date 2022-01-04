package dtos;

import com.google.gson.annotations.SerializedName;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserDTO {

    @SerializedName(value = "id")
    private UUID id;

    @SerializedName(value = "person")
    private PersonDTO personDTO;

    @SerializedName(value = "roles")
    private List<RoleDTO> roleDTOs;

    @SerializedName(value = "email")
    private String email;

    public UserDTO(User user) {
        id = user.getId();
        personDTO = new PersonDTO(user.getPerson());
        email = user.getEmail();

        roleDTOs = new ArrayList<>();
        user.getRoles().forEach(role -> {
            roleDTOs.add(new RoleDTO(role));
        });
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PersonDTO getPersonDTO() {
        return personDTO;
    }

    public void setPersonDTO(PersonDTO personDTO) {
        this.personDTO = personDTO;
    }

    public List<RoleDTO> getRoleDTOs() {
        return roleDTOs;
    }

    public void addRoleDTO(RoleDTO roleDTO) {
        if (!roleDTOs.contains(roleDTO)) {
            roleDTOs.add(roleDTO);
        }
    }

    public void removeRoleDTO(RoleDTO roleDTO) {
        if (roleDTOs.contains(roleDTO)) {
            roleDTOs.remove(roleDTO);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.id);
        hash = 73 * hash + Objects.hashCode(this.personDTO);
        hash = 73 * hash + Objects.hashCode(this.roleDTOs);
        hash = 73 * hash + Objects.hashCode(this.email);
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
        final UserDTO other = (UserDTO) obj;
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.personDTO, other.personDTO)) {
            return false;
        }
        return Objects.equals(this.roleDTOs, other.roleDTOs);
    }

}
