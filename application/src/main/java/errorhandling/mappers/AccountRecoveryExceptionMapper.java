package errorhandling.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import errorhandling.exceptions.AccountRecoveryException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Nicklas Nielsen
 */
@Provider
public class AccountRecoveryExceptionMapper implements ExceptionMapper<AccountRecoveryException>{

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    @Override
    public Response toResponse(AccountRecoveryException exception) {
        Response.Status status = Response.Status.BAD_REQUEST;
        String message = "Noget gik galt. Prøv igen om lidt, eller kontakt kundeservice.";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", status.getStatusCode());
        jsonObject.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonObject))
                .build();
    }
    
}
