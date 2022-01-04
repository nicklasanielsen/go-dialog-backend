package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dtos.InterviewDTO;
import entities.Company;
import entities.User;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.GoogleRecaptchaException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserNotFoundException;
import facades.CompanyFacade;
import facades.InterviewFacade;
import facades.UserFacade;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@Path("hr")
public class HRResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final InterviewFacade INTERVIEW_FACADE = InterviewFacade.getInterviewFacade(EMF);
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final CompanyFacade COMPANY_FACADE = CompanyFacade.getCompanyFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    SecurityContext securityContext;

    @Context
    ContainerRequestContext requestContext;

    @GET
    @Path("interviews/upcoming")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response getUpcoming() throws SanitizationException, UserNotFoundException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        Company company = user.getCompany();

        List<InterviewDTO> interviewDTOs = INTERVIEW_FACADE.getUpcomingDTOsByCompany(company);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTOs))
                .build();
    }

    @GET
    @Path("interviews/previous")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response getPrevious() throws SanitizationException, UserNotFoundException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        Company company = user.getCompany();

        List<InterviewDTO> interviewDTOs = INTERVIEW_FACADE.getPreviousDTOsByCompany(company);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTOs))
                .build();
    }

    @POST
    @Path("invite")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @RolesAllowed("HR")
    public Response invite(String jsonString) throws API_Exception, SanitizationException, UserNotFoundException, GoogleRecaptchaException {
        UUID userID;
        String email;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            userID = UUID.fromString(securityContext.getUserPrincipal().getName());
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            email = jsonObject.get("email").getAsString();
        } catch (JsonSyntaxException | NullPointerException | JSONException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                throw new SanitizationException("Invalid UUID");
            }

            throw new API_Exception();
        }

        User user = USER_FACADE.getById(userID);
        Company company = user.getCompany();

        COMPANY_FACADE.inviteUser(company, email);

        Status status = Status.OK;
        String message = "Invitation send.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

}
