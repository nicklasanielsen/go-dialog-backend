package errorhandling.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Nicklas Nielsen
 */
@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        Response.Status status = Response.Status.FORBIDDEN;
        String message = status.getReasonPhrase();

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
