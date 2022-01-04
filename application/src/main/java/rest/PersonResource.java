package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PersonDTO;
import entities.User;
import errorhandling.exceptions.PersonNotFoundException;
import errorhandling.exceptions.SanitizationException;
import errorhandling.exceptions.UserNotFoundException;
import facades.PersonFacade;
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
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final PersonFacade PERSON_FACADE = PersonFacade.getPersonFacade(EMF);
    private static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("all")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getAll() {
        List<PersonDTO> dtos = PERSON_FACADE.getAllDTOs();

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(dtos))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getById(@PathParam("id") String id) throws SanitizationException, PersonNotFoundException {
        UUID uid;

        try {
            uid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        PersonDTO personDTO = PERSON_FACADE.getDTOById(uid);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTO))
                .build();
    }

    @GET
    @Path("find/firstname/{firstname}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByFirstname(@PathParam("firstname") String firstname) throws SanitizationException {
        List<PersonDTO> personDTOs = PERSON_FACADE.getAllDTOsByName(firstname, "", "");

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTOs))
                .build();
    }

    @GET
    @Path("find/firstname/{firstname}/middlename/{middlename}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByFirstnameMiddlename(@PathParam("firstname") String firstname, @PathParam("middlename") String middlename) throws SanitizationException {
        List<PersonDTO> personDTOs = PERSON_FACADE.getAllDTOsByName(firstname, middlename, "");

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTOs))
                .build();
    }

    @GET
    @Path("find/firstname/{firstname}/middlename/{middlename}/lastname/{lastname}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByFullname(@PathParam("firstname") String firstname, @PathParam("middlename") String middlename, @PathParam("lastname") String lastname) throws SanitizationException {
        List<PersonDTO> personDTOs = PERSON_FACADE.getAllDTOsByName(firstname, middlename, lastname);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTOs))
                .build();
    }

    @GET
    @Path("find/firstname/{firstname}/lastname/{lastname}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByFirstnameLastname(@PathParam("firstname") String firstname, @PathParam("lastname") String lastname) throws SanitizationException {
        System.out.println("firstname=" + firstname + ", lastname=" + lastname);
        List<PersonDTO> personDTOs = PERSON_FACADE.getAllDTOsByName(firstname, "", lastname);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTOs))
                .build();
    }

    @GET
    @Path("find/middlename/{middlename}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByMiddlename(@PathParam("middlename") String middlename) throws SanitizationException {
        List<PersonDTO> personDTOs = PERSON_FACADE.getAllDTOsByName("", middlename, "");

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTOs))
                .build();
    }

    @GET
    @Path("find/middlename/{middlename}/lastname/{lastname}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByMiddlenameLastname(@PathParam("middlename") String middlename, @PathParam("lastname") String lastname) throws SanitizationException {
        List<PersonDTO> personDTOs = PERSON_FACADE.getAllDTOsByName("", middlename, lastname);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTOs))
                .build();
    }

    @GET
    @Path("find/lastname/{lastname}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByLastname(@PathParam("lastname") String lastname) throws SanitizationException {
        List<PersonDTO> personDTOs = PERSON_FACADE.getAllDTOsByName("", "", lastname);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTOs))
                .build();
    }

    @GET
    @Path("user/{userId}")
    @Produces(APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response getByUser(@PathParam("userId") String userId) throws SanitizationException, PersonNotFoundException, UserNotFoundException {
        UUID uid;

        try {
            uid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new SanitizationException("Invalid UUID");
        }

        User user = USER_FACADE.getById(uid);

        PersonDTO personDTO = PERSON_FACADE.getDTOByUser(user);

        return Response
                .status(Status.OK)
                .entity(GSON.toJson(personDTO))
                .build();
    }

}
