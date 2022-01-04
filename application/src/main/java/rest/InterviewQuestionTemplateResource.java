package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dtos.InterviewQuestionTemplateDTO;
import entities.InterviewQuestionTemplate;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.GoogleRecaptchaException;
import errorhandling.exceptions.InterviewQuestionTemplateNotFoundException;
import errorhandling.exceptions.SanitizationException;
import facades.InterviewQuestionTemplateFacade;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonException;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
@Path("interview/question/template")
public class InterviewQuestionTemplateResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final InterviewQuestionTemplateFacade INTERVIEW_QUESTION_TEMPLATE_FACADE = InterviewQuestionTemplateFacade.getInterviewQuestionTemplateFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    SecurityContext securityContext;

    @Context
    ContainerRequestContext requestContext;

    @GET
    @Path("all")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response getAll() {
        List<InterviewQuestionTemplateDTO> interviewQuestionTemplateDTOs = INTERVIEW_QUESTION_TEMPLATE_FACADE.getAllDTOs();

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewQuestionTemplateDTOs))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response getById(@PathParam("id") String idString) throws SanitizationException, InterviewQuestionTemplateNotFoundException {
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        InterviewQuestionTemplateDTO interviewQuestionTemplateDTO = INTERVIEW_QUESTION_TEMPLATE_FACADE.getDTOById(id);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewQuestionTemplateDTO))
                .build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response create(String jsonString) throws API_Exception, DatabaseException, GoogleRecaptchaException {
        String name, question;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            name = jsonObject.get("name").getAsString();
            question = jsonObject.get("question").getAsString();
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        }

        INTERVIEW_QUESTION_TEMPLATE_FACADE.create(name, question);

        Status status = Status.OK;
        String message = "Spørgsmål Oprettet.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @DELETE
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("id") String idString) throws SanitizationException, DatabaseException, GoogleRecaptchaException {
        UUID id;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        INTERVIEW_QUESTION_TEMPLATE_FACADE.delete(id);

        Status status = Status.OK;
        String message = "Spørgsmål slettet.";

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
    public Response edit(@PathParam("id") String idString, String jsonString) throws SanitizationException, API_Exception, InterviewQuestionTemplateNotFoundException, DatabaseException, GoogleRecaptchaException {
        UUID id;
        String name, question;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            name = jsonObject.get("name").getAsString();
            question = jsonObject.get("question").getAsString();
            id = UUID.fromString(idString);
        } catch (JsonSyntaxException | NullPointerException | JSONException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                throw new SanitizationException("Invalid UUID");
            }

            throw new API_Exception();
        }

        InterviewQuestionTemplate template = INTERVIEW_QUESTION_TEMPLATE_FACADE.getById(id);
        INTERVIEW_QUESTION_TEMPLATE_FACADE.edit(template, name, question);

        Status status = Status.OK;
        String message = "Spørgsmål opdateret.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

}
