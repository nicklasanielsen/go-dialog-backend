package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import dtos.UserDTO;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserNotFoundException;
import facades.UserFacade;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("user")
public class UserResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("all")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getAll() {
        List<UserDTO> userDTOs = USER_FACADE.getAllDTOs();

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(userDTOs))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getById(@PathParam("id") String id) throws SanitizationException, UserNotFoundException, UnirestException {
        UUID uid;

        try {
            uid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        UserDTO userDTO = USER_FACADE.getDTOById(uid);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(userDTO))
                .build();
    }

    @GET
    @Path("find/email/{email}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response getByEmail(@PathParam("email") String email) throws UserNotFoundException, SanitizationException {
        UserDTO userDTO = USER_FACADE.getDTOByEmail(email);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(userDTO))
                .build();
    }

    @GET
    @Path("{employee}/add_manager/{manager}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response addManager(@PathParam("employee") String eId, @PathParam("manager") String mId) throws SanitizationException, UserNotFoundException, DatabaseException {
        UUID employeeID, managerID;

        try {
            employeeID = UUID.fromString(eId);
            managerID = UUID.fromString(mId);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User employee = USER_FACADE.getById(employeeID);
        User manager = USER_FACADE.getById(managerID);

        if (employee != manager) {
            USER_FACADE.addManager(employee, manager);
        }

        Status status = Status.OK;
        String message = "Leder tilføjet.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @GET
    @Path("{employee}/remove_manager/{manager}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response removeManager(@PathParam("employee") String eId, @PathParam("manager") String mId) throws SanitizationException, UserNotFoundException, DatabaseException {
        UUID employeeID, managerID;

        try {
            employeeID = UUID.fromString(eId);
            managerID = UUID.fromString(mId);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User employee = USER_FACADE.getById(employeeID);
        User manager = USER_FACADE.getById(managerID);

        if (employee != manager) {
            USER_FACADE.removeManager(employee, manager);
        }

        Status status = Status.OK;
        String message = "Leder fjernet.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

}
