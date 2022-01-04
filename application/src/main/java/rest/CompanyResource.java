package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dtos.CompanyDTO;
import dtos.UserDTO;
import entities.Company;
import entities.User;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.CompanyNotFoundException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.GoogleRecaptchaException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserNotFoundException;
import facades.CompanyFacade;
import facades.UserFacade;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.json.JSONException;
import utils.EMF_Creator;
import utils.GoogleRecaptcha;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("company")
public class CompanyResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final CompanyFacade COMPANY_FACADE = CompanyFacade.getCompanyFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    SecurityContext securityContext;

    @Context
    ContainerRequestContext requestContext;

    @GET
    @Path("all")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getAll() {
        List<CompanyDTO> companyDTOs = COMPANY_FACADE.getAllDTOs();

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyDTOs))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getById(@PathParam("id") String id) throws CompanyNotFoundException, SanitizationException {
        UUID uid;

        try {
            uid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        CompanyDTO companyDTO = COMPANY_FACADE.getDTOById(uid);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyDTO))
                .build();
    }

    @GET
    @Path("find/name/{name}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByName(@PathParam("name") String name) throws SanitizationException {
        List<CompanyDTO> companyDTOs = COMPANY_FACADE.getAllDTOsByName(name);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyDTOs))
                .build();
    }

    @GET
    @Path("find/cvr/{cvr}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByCvr(@PathParam("cvr") String cvr) throws SanitizationException {
        List<CompanyDTO> companyDTOs = COMPANY_FACADE.getAllDTOsByCvr(cvr);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(companyDTOs))
                .build();
    }

    @GET
    @Path("managers")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response getManagers() throws SanitizationException, UserNotFoundException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        Company company = user.getCompany();

        List<UserDTO> userDTOs = COMPANY_FACADE.getManagerDTOsByCompany(company);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(userDTOs))
                .build();
    }

    @GET
    @Path("employees")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response getEmployees() throws UserNotFoundException, SanitizationException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        Company company = user.getCompany();

        List<UserDTO> userDTOs = COMPANY_FACADE.getEmployeeDTOsByCompany(company);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(userDTOs))
                .build();
    }

    @DELETE
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("id") String idString) throws SanitizationException, DatabaseException {
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        COMPANY_FACADE.delete(id);

        Status status = Status.OK;
        String message = "Virksomhed slettet.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @PUT
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response edit(@PathParam("id") String idString, String jsonString) throws SanitizationException, API_Exception, CompanyNotFoundException, DatabaseException, GoogleRecaptchaException {
        UUID id;
        String name, cvr;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            name = jsonObject.get("name").getAsString();
            cvr = jsonObject.get("cvr").getAsString();
            id = UUID.fromString(idString);
        } catch (JsonSyntaxException | NullPointerException | JSONException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                throw new SanitizationException("Invalid UUID");
            }

            throw new API_Exception();
        }

        Company company = COMPANY_FACADE.getById(id);
        COMPANY_FACADE.edit(company, name, cvr);

        Status status = Status.OK;
        String message = "Virksomhedsoplysninger opdateret.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

}
