package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.RoleDTO;
import errorhandling.exceptions.RoleNotFoundException;
import errorhandling.exceptions.SanitizationException;
import facades.RoleFacade;
import java.util.List;
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
@Path("role")
public class RoleResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final RoleFacade ROLE_FACADE = RoleFacade.getRoleFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("all")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getAll() {
        List<RoleDTO> roleDTOs = ROLE_FACADE.getAllDTOs();

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(roleDTOs))
                .build();
    }

    @GET
    @Path("{type}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByType(@PathParam("type") String type) throws RoleNotFoundException, SanitizationException {
        RoleDTO roleDTO = ROLE_FACADE.getDTOByType(type);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(roleDTO))
                .build();
    }

    @GET
    @Path("defaults")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getDefaults() {
        List<RoleDTO> roleDTOs = ROLE_FACADE.getAllDefaultDTOs();

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(roleDTOs))
                .build();
    }

}
