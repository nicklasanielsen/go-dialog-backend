package rest;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Nicklas Nielsen
 */
@ApplicationPath("/")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        addRestResourceClasses(resources);

        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(cors.CorsFilter.class);
        resources.add(errorhandling.mappers.API_ExceptionMapper.class);
        resources.add(errorhandling.mappers.AccountActivationExceptionMapper.class);
        resources.add(errorhandling.mappers.AccountRecoveryExceptionMapper.class);
        resources.add(errorhandling.mappers.AuthenticationExceptionMapper.class);
        resources.add(errorhandling.mappers.CompanyNotFoundException.class);
        resources.add(errorhandling.mappers.CompanyStatusNotFoundExceptionMapper.class);
        resources.add(errorhandling.mappers.CompanyStatusTypeNotFoundExceptionMapper.class);
        resources.add(errorhandling.mappers.GenericExceptionMapper.class);
        resources.add(errorhandling.mappers.NotAuthorizedExceptionMapper.class);
        resources.add(errorhandling.mappers.PersonNotFoundExceptionMapper.class);
        resources.add(errorhandling.mappers.RoleNotFoundExceptionMapper.class);
        resources.add(errorhandling.mappers.SanitizationExceptionMapper.class);
        resources.add(errorhandling.mappers.UserNotFoundExceptionMapper.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
        resources.add(rest.AuthResource.class);
        resources.add(rest.CompanyResource.class);
        resources.add(rest.CompanyStatusResource.class);
        resources.add(rest.CompanyStatusTypeResource.class);
        resources.add(rest.HRResource.class);
        resources.add(rest.InterviewQuestionTemplateResource.class);
        resources.add(rest.InterviewResource.class);
        resources.add(rest.InterviewTemplateResource.class);
        resources.add(rest.ManagerResource.class);
        resources.add(rest.PersonResource.class);
        resources.add(rest.RoleResource.class);
        resources.add(rest.UserResource.class);
        resources.add(security.JWTAuthenticationFilter.class);
        resources.add(security.RolesAllowedFilter.class);

    }
}
