package security;

import java.io.IOException;
import java.lang.reflect.Method;
import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Nicklas Nielsen
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class RolesAllowedFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method resourceMethod = resourceInfo.getResourceMethod();

        if (resourceMethod.isAnnotationPresent(DenyAll.class)) {
            throw new NotAuthorizedException(Status.FORBIDDEN.getReasonPhrase(), Status.FORBIDDEN);
        }

        RolesAllowed rolesAllowed = resourceMethod.getAnnotation(RolesAllowed.class);
        if (assertRole(requestContext, rolesAllowed)) {
            return;
        }

        if (resourceMethod.isAnnotationPresent(PermitAll.class)) {
            return;
        }

        if (resourceInfo.getResourceClass().isAnnotationPresent(DenyAll.class)) {
            throw new NotAuthorizedException(Status.FORBIDDEN.getReasonPhrase(), Status.FORBIDDEN);
        }

        rolesAllowed = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
        assertRole(requestContext, rolesAllowed);
    }

    private boolean assertRole(ContainerRequestContext requestContext, RolesAllowed rolesAllowed) {
        if (rolesAllowed != null) {
            String[] roles = rolesAllowed.value();

            for (String role : roles) {
                if (requestContext.getSecurityContext().isUserInRole(role)) {
                    return true;
                }
            }

            throw new NotAuthorizedException(Status.FORBIDDEN.getReasonPhrase(), Status.FORBIDDEN);
        }

        return false;
    }

}
