package security;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Nicklas Nielsen
 */
public class JWTSecurityContext implements SecurityContext {

    private UserPrincipal userPrincipal;
    private ContainerRequestContext request;

    public JWTSecurityContext(UserPrincipal userPrincipal, ContainerRequestContext request) {
        this.userPrincipal = userPrincipal;
        this.request = request;
    }

    @Override
    public boolean isUserInRole(String role) {
        return userPrincipal.isUserInRole(role);
    }

    @Override
    public boolean isSecure() {
        return request.getUriInfo().getBaseUri().getScheme().equals("https");
    }

    @Override
    public UserPrincipal getUserPrincipal() {
        return userPrincipal;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT"; // Only for INFO
    }

    public ContainerRequestContext getRequest() {
        return request;
    }

}
