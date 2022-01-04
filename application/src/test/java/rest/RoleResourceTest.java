package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dtos.RoleDTO;
import entities.Role;
import entities.User;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.CoreMatchers.is;
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
public class RoleResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/";

    private Role role;
    private RoleDTO roleDTO;

    private User admin;
    private User user;

    private Role adminRole;
    private Role userRole;

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
        role = new Role("TEST", true);

        user = new User("user@user.user", "password123");
        admin = new User("admin@admin.admin", "password123");

        userRole = new Role("USER");
        adminRole = new Role("ADMIN");

        user.addRole(userRole);
        admin.addRole(adminRole);

        user.activate();
        admin.activate();

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(role);
            em.persist(user);
            em.persist(admin);
            em.persist(userRole);
            em.persist(adminRole);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        roleDTO = new RoleDTO(role);
    }

    @AfterEach
    public void tearDown() {
        role = null;
        roleDTO = null;

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
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
        String jwt = adminLogin();

        // Act
        given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("role/all")
                .then().statusCode(Status.OK.getStatusCode())
                .assertThat().body("size()", is(3));
    }

    @Test
    public void get_all_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("role/all")
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
                .when().get("role/all")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_found() {
        // Arrange
        String expected = GSON.toJson(roleDTO);
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("role/" + role.getType())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_not_found() {
        // Arrange
        String type = "TESTING";
        String expected = "Bad Request";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("role/" + type)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("role/USER")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("role/USER")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_sanitization_exception() {
        // Arrange
        String type = "#";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("role/" + type)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_defaults() {
        // Arrnage
        String expected = GSON.toJson(Arrays.asList(roleDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("role/defaults")
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_defaults_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("role/defaults")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_defaults_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("role/defaults")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

}
