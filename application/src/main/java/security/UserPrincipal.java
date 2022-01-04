package security;

import entities.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserPrincipal implements Principal {

    private String id;
    private List<String> roles;

    public UserPrincipal(User user) {
        id = user.getId().toString();

        roles = new ArrayList<>();
        user.getRoles().forEach(role -> {
            roles.add(role.getType());
        });
    }

    public UserPrincipal(String id, String[] roles) {
        this.id = id;
        this.roles = Arrays.asList(roles);
    }

    @Override
    public String getName() {
        return id;
    }

    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

}
