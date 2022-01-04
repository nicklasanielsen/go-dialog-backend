package security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import errorhandling.exceptions.JWTException;
import facades.JWTFacade;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final JWTFacade JWT_FACADE = JWTFacade.getJWTFacade(EMF);

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final List<Class<? extends Annotation>> securityAnnotations
            = Arrays.asList(DenyAll.class, PermitAll.class, RolesAllowed.class);

    @Context
    private ResourceInfo resourceInfo;

    @Context
    HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (isSecuredResource()) {
            String JWT = request.getHeaderString("Authentication");

            if (JWT == null) {
                Status status = Status.UNAUTHORIZED;
                String message = status.getReasonPhrase();

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", status.getStatusCode());
                jsonResponse.addProperty("message", message);

                request.abortWith(Response.status(status)
                        .entity(GSON.toJson(jsonResponse))
                        .type(MediaType.APPLICATION_JSON)
                        .build());
                return;
            }

            try {
                UserPrincipal userPrincipal = getUserPrincipalFromTokenIfValid(JWT);
                request.setSecurityContext(new JWTSecurityContext(userPrincipal, request));
            } catch (Exception e) {
                Status status = Status.FORBIDDEN;
                String message = status.getReasonPhrase();

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", status.getStatusCode());
                jsonResponse.addProperty("message", message);

                request.abortWith(Response.status(status)
                        .entity(GSON.toJson(jsonResponse))
                        .type(MediaType.APPLICATION_JSON)
                        .build());
            }
        }
    }

    private boolean isSecuredResource() {
        boolean isAnnotationPresentForMethod, isAnnotationPresentForClass;

        for (Class<? extends Annotation> securityClass : securityAnnotations) {
            isAnnotationPresentForMethod = resourceInfo.getResourceMethod().isAnnotationPresent(securityClass);
            isAnnotationPresentForClass = resourceInfo.getResourceClass().isAnnotationPresent(securityClass);

            if (isAnnotationPresentForMethod || isAnnotationPresentForClass) {
                return true;
            }
        }

        return false;
    }

    private UserPrincipal getUserPrincipalFromTokenIfValid(String JWT) throws java.text.ParseException, JOSEException, JWTException {
        SignedJWT signedJWT = SignedJWT.parse(JWT);
        JWSVerifier verifier = new MACVerifier(SharedSecret.getSecretKey());

        if (signedJWT.verify(verifier)) {
            long currentTime = new Date().getTime();
            long JWTExpirationTime = signedJWT.getJWTClaimsSet().getExpirationTime().getTime();

            if (currentTime > JWTExpirationTime) {
                // Token is expired
                throw new JWTException("Token expired");
            }

            String jwtID = signedJWT.getJWTClaimsSet().getClaim("token_id").toString();

            boolean jwtIsRevoked = JWT_FACADE.isRevoked(jwtID);
            if (jwtIsRevoked) {
                throw new JWTException("Token revoked");
            }

            String[] roles = signedJWT.getJWTClaimsSet().getClaim("roles").toString().split(",");
            String userID = signedJWT.getJWTClaimsSet().getClaim("user_id").toString();

            return new UserPrincipal(userID, roles);
        } else {
            // Unable to extract user from token
            throw new JWTException("Unable to extract user from token");
        }
    }

}
