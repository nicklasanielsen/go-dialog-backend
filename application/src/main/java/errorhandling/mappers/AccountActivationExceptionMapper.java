package errorhandling.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import errorhandling.exceptions.AccountActivationException;
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
public class AccountActivationExceptionMapper implements ExceptionMapper<AccountActivationException> {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Response toResponse(AccountActivationException exception) {
        Status status = Status.UNAUTHORIZED;
        String message = "Kontoaktivering mislykkedes. Prøv igen senere.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
