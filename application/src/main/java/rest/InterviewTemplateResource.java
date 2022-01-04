package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dtos.InterviewTemplateDTO;
import entities.InterviewQuestionTemplate;
import entities.InterviewTemplate;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.GoogleRecaptchaException;
import errorhandling.exceptions.InterviewQuestionTemplateNotFoundException;
import errorhandling.exceptions.InterviewTemplateNotFoundException;
import errorhandling.exceptions.SanitizationException;
import facades.InterviewQuestionTemplateFacade;
import facades.InterviewTemplateFacade;
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
import utils.EMF_Creator;
import utils.GoogleRecaptcha;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("interview/template")
public class InterviewTemplateResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final InterviewTemplateFacade INTERVIEW_TEMPLATE_FACADE = InterviewTemplateFacade.getInterviewTemplateFacade(EMF);
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
        List<InterviewTemplateDTO> interviewTemplateDTOs = INTERVIEW_TEMPLATE_FACADE.getAllDTOs();

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewTemplateDTOs))
                .build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response create(String jsonString) throws API_Exception, DatabaseException, GoogleRecaptchaException {
        String name;
        int amountOfManagers, amountOfEmployees;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            name = jsonObject.get("name").getAsString();
            amountOfManagers = jsonObject.get("amount_of_managers").getAsInt();
            amountOfEmployees = jsonObject.get("amount_of_employees").getAsInt();
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        }

        INTERVIEW_TEMPLATE_FACADE.create(name, amountOfManagers, amountOfEmployees);

        Status status = Status.OK;
        String message = "Samtale skabelon oprettet.";

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
    public Response delete(@PathParam("id") String id) throws SanitizationException, DatabaseException, GoogleRecaptchaException {
        UUID templateId;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            templateId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        INTERVIEW_TEMPLATE_FACADE.delete(templateId);

        Status status = Status.OK;
        String message = "Samtale skabelon slettet.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response getById(@PathParam("id") String idString) throws SanitizationException, InterviewTemplateNotFoundException {
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        InterviewTemplateDTO interviewTemplateDTO = INTERVIEW_TEMPLATE_FACADE.getDTOById(id);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewTemplateDTO))
                .build();
    }

    @GET
    @Path("{template}/add_question/{question}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response addQuestion(@PathParam("template") String template, @PathParam("question") String question) throws SanitizationException, InterviewTemplateNotFoundException, InterviewQuestionTemplateNotFoundException, DatabaseException {
        UUID templateId, questionId;

        try {
            templateId = UUID.fromString(template);
            questionId = UUID.fromString(question);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        InterviewTemplate interviewTemplate = INTERVIEW_TEMPLATE_FACADE.getById(templateId);
        InterviewQuestionTemplate interviewQuestionTemplate = INTERVIEW_QUESTION_TEMPLATE_FACADE.getById(questionId);

        INTERVIEW_TEMPLATE_FACADE.addQuestion(interviewTemplate, interviewQuestionTemplate);

        Status status = Status.OK;
        String message = "Spørgsmål tilføjet til samtale skabelon.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @GET
    @Path("{template}/remove_question/{question}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response removeQuestion(@PathParam("template") String template, @PathParam("question") String question) throws SanitizationException, InterviewTemplateNotFoundException, InterviewQuestionTemplateNotFoundException, DatabaseException {
        UUID templateId, questionId;

        try {
            templateId = UUID.fromString(template);
            questionId = UUID.fromString(question);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        InterviewTemplate interviewTemplate = INTERVIEW_TEMPLATE_FACADE.getById(templateId);
        InterviewQuestionTemplate interviewQuestionTemplate = INTERVIEW_QUESTION_TEMPLATE_FACADE.getById(questionId);

        INTERVIEW_TEMPLATE_FACADE.removeQuestion(interviewTemplate, interviewQuestionTemplate);

        Status status = Status.OK;
        String message = "Spørgsmål fjernet fra samtale skabelon.";

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
    public Response edit(@PathParam("id") String idString, String jsonString) throws SanitizationException, API_Exception, InterviewTemplateNotFoundException, DatabaseException, GoogleRecaptchaException {
        UUID id;
        String name;
        int amountOfManagers, amountOfEmployees;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            name = jsonObject.get("name").getAsString();
            amountOfManagers = jsonObject.get("amount_of_managers_allowed").getAsInt();
            amountOfEmployees = jsonObject.get("amount_of_employees_allowed").getAsInt();
            id = UUID.fromString(idString);
        } catch (JsonSyntaxException | NullPointerException | JsonException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                throw new SanitizationException("Invalid UUID");
            }

            throw new API_Exception();
        }

        InterviewTemplate interviewTemplate = INTERVIEW_TEMPLATE_FACADE.getById(id);
        INTERVIEW_TEMPLATE_FACADE.edit(interviewTemplate, name, amountOfManagers, amountOfEmployees);

        Status status = Status.OK;
        String message = "Samtale skabelon opdateret.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

}
