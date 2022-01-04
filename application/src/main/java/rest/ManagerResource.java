package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.InterviewDTO;
import dtos.UserDTO;
import entities.User;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserNotFoundException;
import facades.ManagerFacade;
import facades.UserFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("managers")
public class ManagerResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final ManagerFacade MANAGER_FACADE = ManagerFacade.getManagerFacade(EMF);
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    SecurityContext securityContext;

    @GET
    @Path("interviews/upcoming")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response getUpcoming() throws SanitizationException, UserNotFoundException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        List<InterviewDTO> interviewDTOs = MANAGER_FACADE.getUpcomingsDTOsByManager(user);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTOs))
                .build();
    }

    @GET
    @Path("interviews/previous")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response getPrevious() throws SanitizationException, UserNotFoundException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        List<InterviewDTO> interviewDTOs = MANAGER_FACADE.getProviousDTOsByManager(user);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTOs))
                .build();
    }

    @GET
    @Path("employees")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response getEmployees() throws SanitizationException, UserNotFoundException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        List<UserDTO> employees = MANAGER_FACADE.getEmployeeDTOsByManager(user);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(employees))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response getManagersByUser(@PathParam("id") String idString) throws SanitizationException, UserNotFoundException {
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(id);

        List<UserDTO> managers = new ArrayList<>();
        user.getManagers().forEach(manager -> {
            managers.add(new UserDTO(manager));
        });

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(managers))
                .build();
    }

}
