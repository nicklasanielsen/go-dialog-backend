package errorhandling.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import errorhandling.exceptions.AuthenticationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Nicklas Nielsen
 */
@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Response toResponse(AuthenticationException exception) {
        Response.Status status = Response.Status.UNAUTHORIZED;
        String message = "Forkert e-mail eller adgangskode, eller kontoen er ikke aktiveret";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
