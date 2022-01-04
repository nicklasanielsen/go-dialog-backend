package errorhandling.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Nicklas Nielsen
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Response toResponse(Throwable exception) {
        Status status;
        String message;

        if (exception instanceof WebApplicationException) {
            status = Status.METHOD_NOT_ALLOWED;
            message = status.getReasonPhrase();
        } else {
            status = Status.SERVICE_UNAVAILABLE;
            message = status.getReasonPhrase();
        }

        System.out.println(exception);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", status.getStatusCode());
        jsonObject.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonObject))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
