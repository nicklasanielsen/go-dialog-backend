package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dtos.CompanyStatusTypeDTO;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.CompanyStatusTypeCreationException;
import errorhandling.exceptions.CompanyStatusTypeEditException;
import errorhandling.exceptions.CompanyStatusTypeNotFoundException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.SanitizationException;
import facades.CompanyStatusTypeFacade;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonException;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("company/status/type")
public class CompanyStatusTypeResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final CompanyStatusTypeFacade COMPANY_STATUS_TYPE_FACADE = CompanyStatusTypeFacade.getFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("all")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getAll() {
        List<CompanyStatusTypeDTO> dtos = COMPANY_STATUS_TYPE_FACADE.getAllDTOs();

        return Response.status(Status.OK).entity(GSON.toJson(dtos)).build();
    }

    @GET
    @Path("default")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getDefault() throws CompanyStatusTypeNotFoundException, CompanyStatusTypeNotFoundException, DatabaseException {
        CompanyStatusTypeDTO dto = COMPANY_STATUS_TYPE_FACADE.getDefaultDTO();

        return Response.status(Status.OK).entity(GSON.toJson(dto)).build();
    }

    @GET
    @Path("{type}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByType(@PathParam("type") String type) throws CompanyStatusTypeNotFoundException, SanitizationException {
        CompanyStatusTypeDTO dto = COMPANY_STATUS_TYPE_FACADE.getDTOByType(type);

        return Response.status(Status.OK).entity(GSON.toJson(dto)).build();
    }

    @POST
    @Path("")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response create(String jsonRequestString) throws SanitizationException, DatabaseException, CompanyStatusTypeCreationException, API_Exception {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonRequestString).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            boolean isDefault = jsonObject.get("is_default").getAsBoolean();

            COMPANY_STATUS_TYPE_FACADE.createNew(type, isDefault);
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        }

        return Response.status(Status.OK).build();
    }

    @PUT
    @Path("{type}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response edit(@PathParam("type") String type, String jsonRequestString) throws API_Exception, SanitizationException, CompanyStatusTypeEditException, DatabaseException {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonRequestString).getAsJsonObject();
            String newType = jsonObject.get("type").getAsString();
            boolean isDefault = jsonObject.get("is_default").getAsBoolean();

            COMPANY_STATUS_TYPE_FACADE.edit(type, newType, isDefault);
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        }

        return Response.status(Status.OK).build();
    }

}
