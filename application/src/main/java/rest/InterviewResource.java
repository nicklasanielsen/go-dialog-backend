package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dtos.InterviewDTO;
import entities.Interview;
import entities.InterviewTemplate;
import entities.User;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.GoogleRecaptchaException;
import errorhandling.exceptions.InterviewNotFoundException;
import errorhandling.exceptions.InterviewQuestionNotFoundException;
import errorhandling.exceptions.InterviewTemplateNotFoundException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserNotFoundException;
import facades.InterviewFacade;
import facades.InterviewTemplateFacade;
import facades.UserFacade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Path("interview")
public class InterviewResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final InterviewFacade INTERVIEW_FACADE = InterviewFacade.getInterviewFacade(EMF);
    private static final InterviewTemplateFacade INTERVIEW_TEMPLATE_FACADE = InterviewTemplateFacade.getInterviewTemplateFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    SecurityContext securityContext;

    @Context
    ContainerRequestContext requestContext;

    @GET
    @Path("upcoming")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response getUpcoming() throws SanitizationException, UserNotFoundException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        List<InterviewDTO> interviewDTOs = INTERVIEW_FACADE.getUpcomingDTOsByUser(user);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTOs))
                .build();
    }

    @GET
    @Path("upcoming/{user}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response getUpcomingByUser(@PathParam("user") String userString) throws SanitizationException, UserNotFoundException {
        UUID userID;

        try {
            userID = UUID.fromString(userString);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        List<InterviewDTO> interviewDTOs = INTERVIEW_FACADE.getUpcomingDTOsByUser(user);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTOs))
                .build();
    }

    @GET
    @Path("previous")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response getPrevious() throws SanitizationException, UserNotFoundException {
        UUID userID;
        String id = securityContext.getUserPrincipal().getName();

        try {
            userID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        List<InterviewDTO> interviewDTOs = INTERVIEW_FACADE.getPreviousDTOsByUser(user);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTOs))
                .build();
    }

    @GET
    @Path("previous/{user}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response getPreviousByUser(@PathParam("user") String userString) throws SanitizationException, UserNotFoundException {
        UUID userID;

        try {
            userID = UUID.fromString(userString);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(userID);
        List<InterviewDTO> interviewDTOs = INTERVIEW_FACADE.getPreviousDTOsByUser(user);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTOs))
                .build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @RolesAllowed("MANAGER")
    public Response create(String jsonString) throws API_Exception, SanitizationException, UserNotFoundException, InterviewTemplateNotFoundException, DatabaseException, GoogleRecaptchaException {
        UUID managerId, employeeId, templateId;
        LocalDateTime held;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            managerId = UUID.fromString(securityContext.getUserPrincipal().getName());
            employeeId = UUID.fromString(jsonObject.get("user_id").getAsString());
            templateId = UUID.fromString(jsonObject.get("template").getAsString());
            held = LocalDateTime.of(LocalDate.parse(jsonObject.get("date").getAsString()), LocalTime.parse(jsonObject.get("time").getAsString()));
        } catch (JsonSyntaxException | NullPointerException | JsonException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                throw new SanitizationException("Invalid UUID");
            }

            throw new API_Exception();
        }

        User manager = USER_FACADE.getById(managerId);
        User employee = USER_FACADE.getById(employeeId);

        InterviewTemplate interviewTemplate = INTERVIEW_TEMPLATE_FACADE.getById(templateId);

        INTERVIEW_FACADE.create(interviewTemplate, held, manager, employee);

        Status status = Status.OK;
        String message = "Samtale oprettet.";

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
    @RolesAllowed("USER")
    public Response delete(@PathParam("id") String interviewId) throws SanitizationException, InterviewNotFoundException, DatabaseException, GoogleRecaptchaException {
        UUID id;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            id = UUID.fromString(interviewId);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        Interview interview = INTERVIEW_FACADE.getById(id);
        INTERVIEW_FACADE.delete(interview);

        Status status = Status.OK;
        String message = "Samtale slettet.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @GET
    @Path("{id}/send_invitation")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response sendInvitation(@PathParam("id") String interviewId) throws SanitizationException, InterviewNotFoundException {
        UUID id;

        try {
            id = UUID.fromString(interviewId);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        Interview interview = INTERVIEW_FACADE.getById(id);
        INTERVIEW_FACADE.sendInvitation(interview);

        Status status = Status.OK;
        String message = "Invitationer Afsendt.";

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
    public Response getById(@PathParam("id") String idString) throws SanitizationException, InterviewNotFoundException {
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        InterviewDTO interviewDTO = INTERVIEW_FACADE.getDTOById(id);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(interviewDTO))
                .build();
    }

    @PUT
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @RolesAllowed("USER")
    public Response update(@PathParam("id") String idString, String jsonString) throws SanitizationException, API_Exception, UserNotFoundException, InterviewNotFoundException, InterviewQuestionNotFoundException, DatabaseException, GoogleRecaptchaException {
        UUID userID, interviewID;
        Map<UUID, String> answersToQuestions = new HashMap<>();
        String summary, tmpId, tmpAnswer;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            userID = UUID.fromString(securityContext.getUserPrincipal().getName());
            interviewID = UUID.fromString(idString);

            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            summary = jsonObject.get("summary").getAsString();

            for (JsonElement answer : jsonObject.get("questions").getAsJsonArray()) {
                tmpId = answer.getAsJsonObject().get("id").getAsString();
                tmpAnswer = answer.getAsJsonObject().get("value").getAsString();

                answersToQuestions.put(UUID.fromString(tmpId), tmpAnswer);
            }

        } catch (JsonSyntaxException | NullPointerException | JsonException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                throw new SanitizationException("Invalid UUID");
            }

            throw new API_Exception();
        }

        User user = USER_FACADE.getById(userID);
        Interview interview = INTERVIEW_FACADE.getById(interviewID);

        INTERVIEW_FACADE.update(interview, user, summary, answersToQuestions);

        Status status = Status.OK;
        String message = "Ændring gemt.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

}
