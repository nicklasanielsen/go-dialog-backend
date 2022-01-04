package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dtos.PersonDTO;
import entities.Person;
import entities.Role;
import entities.User;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Nicklas Nielsen
 */
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/";

    private Person person;
    private PersonDTO personDTO;

    private User user;
    private User admin;

    private Role userRole;
    private Role adminRole;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void tearDownClass() {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        person = new Person("Test", "Tester", "Testensen");

        user = new User("uset@user.user", "password123");
        admin = new User("admin@admin.admin", "password123");

        userRole = new Role("USER", true);
        adminRole = new Role("ADMIN");

        user.addRole(userRole);
        admin.addRole(adminRole);

        user.activate();
        admin.activate();

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.persist(user);
            em.persist(admin);
            em.persist(userRole);
            em.persist(adminRole);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        personDTO = new PersonDTO(person);
    }

    @AfterEach
    public void tearDown() {
        person = null;
        personDTO = null;
        user = null;
        admin = null;
        userRole = null;
        adminRole = null;

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private String userLogin() {
        return login(user.getEmail(), "password123");
    }

    private String adminLogin() {
        return login(admin.getEmail(), "password123");
    }

    private String login(String email, String password) {
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("email", email);
        jsonRequest.addProperty("password", password);

        return given()
                .contentType(APPLICATION_JSON)
                .body(GSON.toJson(jsonRequest))
                .when().post("/auth/login")
                .then()
                .extract()
                .header("Authentication");
    }

    @Test
    public void get_all() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(personDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/all")
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_all_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/all")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_all_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/all")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_found() {
        // Arrange
        String expected = GSON.toJson(personDTO);
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/" + person.getId().toString())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_not_found() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String expected = "Bad Request";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/" + id)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/" + UUID.randomUUID().toString())
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/" + UUID.randomUUID().toString())
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_sanitization_exception() {
        // Arrange
        String id = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/" + id)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(personDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + person.getFirstname())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/nicklas")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/nicklas")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_sanitization_exception() {
        // Arrange
        String firstname = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + firstname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_middlename() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(personDTO));
        String jwt = adminLogin();

        // Assert
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/" + person.getMiddlename())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_middlename_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/nicklas/middlename/alexander")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_middlename_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/nicklas/middlename/alexander")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_middlename_sanitization_exception_firstname() {
        // Arrange
        String firstname = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .header("Authentication", jwt)
                .when().get("person/find/firstname/" + firstname + "/middlename/" + person.getMiddlename())
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_middlename_sanitization_exception_middlename() {
        // Arrange
        String middlename = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + person.getFirstname() + "/middlename/" + middlename)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_middlename_sanitization_exception_both() {
        // Arrange
        String firstname = "#";
        String middlename = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + firstname + "/middlename/" + middlename)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(personDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + person.getFirstname() + "/middlename/" + person.getMiddlename() + "/lastname/" + person.getLastname())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/nicklas/middlename/alexander/lastname/nielsen")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/nicklas/middlename/alexander/lastname/nielsen")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname_sanitization_exception_firstname() {
        // Arrange
        String firstname = "123";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + firstname + "/middlename/" + person.getMiddlename() + "/lastname/" + person.getLastname())
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname_sanitization_exception_middlename() {
        // Arrange
        String middlename = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + person.getFirstname() + "/middlename/" + middlename + "/lastname/" + person.getLastname())
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname_sanitization_exception_lastname() {
        // Arrange
        String lastname = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + person.getFirstname() + "/middlename/" + person.getMiddlename() + "/lastname/" + lastname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname_sanitization_exception_firstname_middlename() {
        // Arrange
        String firstname = "#";
        String middlename = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + firstname + "/middlename/" + middlename + "/lastname/" + person.getLastname())
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname_sanitization_exception_firstname_lastname() {
        // Arrange
        String firstname = "#";
        String lastname = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + firstname + "/middlename/" + person.getMiddlename() + "/lastname/" + lastname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_fullname_sanitization_exception_middlename_lastname() {
        // Arrange
        String middlename = "#";
        String lastname = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + person.getFirstname() + "/middlename/" + middlename + "/lastname/" + lastname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_lastname() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(personDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + person.getFirstname() + "/lastname/" + person.getLastname())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_lastname_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/nicklas/lastname/nielsen")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_lastname_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/nicklas/lastname/nielsen")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_lastname_sanitization_exception_firstname() {
        // Arrange
        String firstname = "123";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + firstname + "/lastname/" + person.getLastname())
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_lastname_sanitization_exception_lastname() {
        // Arrange
        String lastname = "123";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + person.getFirstname() + "/lastname/" + lastname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_firstname_lastname_sanitization_exception_both() {
        // Arrange
        String firstname = "123";
        String lastname = "123";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/firstname/" + firstname + "/lastname/" + lastname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(personDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/" + person.getMiddlename())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/alexander")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/alexander")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_sanitization_exception() {
        // Arrange
        String middlename = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/" + middlename)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_lastname() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(personDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/" + person.getMiddlename() + "/lastname/" + person.getLastname())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_lastname_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/alexander/lastname/nielsen")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_lastname_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/alexander/lastname/nielsen")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_lastname_sanitization_exception_middlename() {
        // Arrange
        String middlename = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/" + middlename + "/lastname/" + person.getLastname())
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_lastname_sanitization_exception_lastname() {
        // Arrange
        String lastname = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/" + person.getMiddlename() + "/lastname/" + lastname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_middlename_lastname_sanitization_exception_both() {
        // Arrange
        String middlename = "#";
        String lastname = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/middlename/" + middlename + "/lastname/" + lastname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_lastname() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(personDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/lastname/" + person.getLastname())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_lastname_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/find/lastname/nielsen")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_lastname_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/lastname/nielsen")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_lastname_sanitization_exception() {
        // Arrange
        String lastname = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/find/lastname/" + lastname)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_user_found() {
        // Arrange
        User user = new User("test@test.test", "testing123");
        user.setPerson(person);
        person.setUser(user);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        personDTO = new PersonDTO(person);
        String expected = GSON.toJson(personDTO);

        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/user/" + user.getId().toString())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_user_not_found() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String expected = "Bad Request";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/user/" + id)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_user_found_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/user/" + UUID.randomUUID().toString())
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_user_found_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/user/" + UUID.randomUUID().toString())
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_user_not_found_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("person/user/" + UUID.randomUUID().toString())
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_user_not_found_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/user/" + UUID.randomUUID().toString())
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_user_sanitization_exception() {
        // Arrange
        String id = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("person/user/" + id)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

}
