package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nimbusds.jose.JOSEException;
import dtos.UserDTO;
import entities.Company;
import entities.Person;
import entities.User;
import errorhandling.exceptions.API_Exception;
import errorhandling.exceptions.AccountActivationException;
import errorhandling.exceptions.AccountRecoveryException;
import errorhandling.exceptions.AuthenticationException;
import errorhandling.exceptions.CompanyNotFoundException;
import errorhandling.exceptions.DatabaseException;
import errorhandling.exceptions.GoogleRecaptchaException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserCreationException;
import errorhandling.exceptions.UserNotFoundException;
import facades.AuthFacade;
import facades.CompanyFacade;
import facades.JWTFacade;
import facades.PersonFacade;
import facades.UserFacade;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonException;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import utils.EMF_Creator;
import utils.Email;
import utils.GoogleRecaptcha;

/**
 *
 * @author Nicklas Nielsen
 */
@Path("auth")
public class AuthResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final AuthFacade AUTH_FACADE = AuthFacade.getAuthFacade(EMF);
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final PersonFacade PERSON_FACADE = PersonFacade.getPersonFacade(EMF);
    private static final CompanyFacade COMPANY_FACADE = CompanyFacade.getCompanyFacade(EMF);
    private static final JWTFacade JWT_FACADE = JWTFacade.getJWTFacade(EMF);

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    ContainerRequestContext requestContext;

    @POST
    @Path("register")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response register(String jsonRequestString) throws API_Exception, SanitizationException, DatabaseException, UnirestException, UserNotFoundException, GoogleRecaptchaException {
        String firstname, middlename, lastname, email, password;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonRequestString).getAsJsonObject();
            firstname = jsonObject.get("firstname").getAsString();
            middlename = jsonObject.get("middlename").getAsString();
            lastname = jsonObject.get("lastname").getAsString();
            email = jsonObject.get("email").getAsString();
            password = jsonObject.get("password").getAsString();
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        }

        User user;
        Person person;

        try {
            user = USER_FACADE.create(email, password);
            person = PERSON_FACADE.create(firstname, middlename, lastname);
            USER_FACADE.setPerson(user, person);

            Email.userAndCompanyCreation(user);
        } catch (UserCreationException e) {
            user = USER_FACADE.getByEmail(email);
            Email.userCreationAlreadyExists(user);
        }

        Status status = Status.OK;
        String message = "Et link til at aktivere din konto er blevet sendt til den angivne adresse.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @POST
    @Path("register/new")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response registerNew(String jsonString) throws API_Exception, SanitizationException, CompanyNotFoundException, AccountActivationException, DatabaseException, UserNotFoundException, GoogleRecaptchaException {
        String firstname, middlename, lastname, email, password;
        UUID companyId;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            firstname = jsonObject.get("firstname").getAsString();
            middlename = jsonObject.get("middlename").getAsString();
            lastname = jsonObject.get("lastname").getAsString();
            email = jsonObject.get("email").getAsString();
            password = jsonObject.get("password").getAsString();
            companyId = UUID.fromString(jsonObject.get("company_id").getAsString());
        } catch (JsonSyntaxException | NullPointerException | JsonException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                throw new SanitizationException("Invalid UUID");
            }

            throw new API_Exception();
        }

        Company company = COMPANY_FACADE.getById(companyId);
        User user;
        Person person;

        try {
            user = USER_FACADE.create(email, password);
            person = PERSON_FACADE.create(firstname, middlename, lastname);
            USER_FACADE.setPerson(user, person);
            USER_FACADE.activateUser(user, company, user.getActivationCode());
        } catch (UserCreationException e) {
            user = USER_FACADE.getByEmail(email);
            Email.userCreationAlreadyExists(user);
        }

        Status status = Status.OK;
        String message = "Din konto er oprettet og aktiveret.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @POST
    @Path("login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response login(String jsonRequestString) throws API_Exception, SanitizationException, AuthenticationException, JOSEException, GoogleRecaptchaException {
        String email, password;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonRequestString).getAsJsonObject();
            email = jsonObject.get("email").getAsString();
            password = jsonObject.get("password").getAsString();
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        }

        User user = AUTH_FACADE.login(email, password);
        String JWT = JWT_FACADE.create(user);

        return Response.status(Status.OK)
                .entity(GSON.toJson(new UserDTO(user)))
                .header("Authentication", JWT)
                .build();
    }

    @POST
    @Path("account-recovery/request")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response requestAccountRecovery(String jsonRequestString) throws API_Exception, SanitizationException, DatabaseException, GoogleRecaptchaException {
        String email;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonRequestString).getAsJsonObject();
            email = jsonObject.get("email").getAsString();
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        }

        AUTH_FACADE.requestAccountRecovery(email);

        Status status = Status.OK;
        String message = "Hvis denne e-mailadresse er i vores database, sender vi dig en e-mail for at nulstille din adgangskode.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @POST
    @Path("account-recovery/process")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response processAccountRecovery(String jsonRequestString) throws API_Exception, SanitizationException, DatabaseException, AccountRecoveryException, GoogleRecaptchaException {
        UUID userId, recoveryCode;
        String newPassword;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonRequestString).getAsJsonObject();
            userId = UUID.fromString(jsonObject.get("user_id").getAsString());
            recoveryCode = UUID.fromString(jsonObject.get("recovery_code").getAsString());
            newPassword = jsonObject.get("new_password").getAsString();
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        } catch (IllegalArgumentException ex) {
            throw new SanitizationException("Invalid UUID");
        }

        AUTH_FACADE.processAccountRecovery(userId, recoveryCode, newPassword);

        Status status = Status.OK;
        String message = "Kontogendannelse vellykket.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @POST
    @Path("account-activation")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response accountActivation(String jsonRequestString) throws API_Exception, SanitizationException, AccountActivationException, DatabaseException {
        UUID userId, activationCode;

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonRequestString).getAsJsonObject();
            userId = UUID.fromString(jsonObject.get("user_id").getAsString());
            activationCode = UUID.fromString(jsonObject.get("activation_code").getAsString());
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        } catch (IllegalArgumentException ex) {
            throw new SanitizationException("Invalid UUID");
        }

        AUTH_FACADE.accountActivation(userId, activationCode);

        Status status = Status.OK;
        String message = "Konto aktiveret.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @POST
    @Path("account-activation/company")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response accountAndCompanyActivation(String jsonRequestString) throws SanitizationException, API_Exception, AccountActivationException, DatabaseException, UserNotFoundException, GoogleRecaptchaException {
        UUID userId, activationCode;
        String cvr, name;

        // Google Recaptcha
        GoogleRecaptcha.verify(requestContext);

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonRequestString).getAsJsonObject();
            userId = UUID.fromString(jsonObject.get("user_id").getAsString());
            activationCode = UUID.fromString(jsonObject.get("activation_code").getAsString());
            cvr = jsonObject.get("cvr").getAsString();
            name = jsonObject.get("name").getAsString();
        } catch (JsonSyntaxException | NullPointerException | JsonException e) {
            throw new API_Exception();
        } catch (IllegalArgumentException ex) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = null;
        Company company = null;

        try {
            user = USER_FACADE.getById(userId);
            company = COMPANY_FACADE.create(cvr, name);

            USER_FACADE.activateUser(user, company, activationCode);
        } catch (UserNotFoundException | DatabaseException | AccountActivationException e) {
            if (e instanceof UserNotFoundException) {
                throw new AccountActivationException();
            } else if (e instanceof AccountActivationException) {
                if (company != null) {
                    COMPANY_FACADE.delete(company.getId());
                }
                throw e;
            }

            if (user != null) {
                try {
                    USER_FACADE.deactivateUser(user);
                } catch (DatabaseException ex) {

                }
            }

            if (company != null) {
                try {
                    COMPANY_FACADE.delete(company.getId());
                } catch (DatabaseException exx) {

                }
            }

            throw new AccountActivationException();
        }

        Status status = Status.OK;
        String message = "Konto og virksomhed aktiveret.";

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", status.getStatusCode());
        jsonResponse.addProperty("message", message);

        return Response.status(status)
                .entity(GSON.toJson(jsonResponse))
                .build();
    }

    @POST
    @Path("signout")
    @RolesAllowed("USER")
    @Produces(APPLICATION_JSON)
    public Response signout() {
        JWT_FACADE.revoke(requestContext.getHeaderString("Authentication"));

        return Response.status(Status.OK).build();
    }

    @POST
    @Path("activity")
    @RolesAllowed("USER")
    @Produces(APPLICATION_JSON)
    public Response tokenRenew() {
        String JWT = JWT_FACADE.revokeAndCreateNewToken(requestContext.getHeaderString("Authentication"));

        return Response.status(Status.OK)
                .header("Authentication", JWT)
                .build();
    }

}
