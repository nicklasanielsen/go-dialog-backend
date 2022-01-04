package errorhandling.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import errorhandling.exceptions.API_Exception;
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
public class API_ExceptionMapper implements ExceptionMapper<API_Exception> {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Response toResponse(API_Exception exception) {
        Status status = Status.BAD_REQUEST;
        String message = "Malformed JSON Supplied";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", status.getStatusCode());
        jsonObject.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonObject))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
