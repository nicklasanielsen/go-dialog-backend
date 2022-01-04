package errorhandling.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import errorhandling.exceptions.CompanyStatusTypeNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Nicklas Nielsen
 */
@Provider
public class CompanyStatusTypeNotFoundExceptionMapper implements ExceptionMapper<CompanyStatusTypeNotFoundException> {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Response toResponse(CompanyStatusTypeNotFoundException exception) {
        Status status = Status.BAD_REQUEST;
        String message = status.getReasonPhrase();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", status.getStatusCode());
        jsonObject.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonObject))
                .build();
    }

}
