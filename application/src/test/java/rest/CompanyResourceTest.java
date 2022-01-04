package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dtos.CompanyDTO;
import entities.Company;
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
public class CompanyResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/";

    private Company company;
    private CompanyDTO companyDTO;

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
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        company = new Company("TEST", "12345678");
        admin = new User("admin@admin.admin", "password123");
        adminRole = new Role("ADMIN");
        admin.addRole(adminRole);

        user = new User("user@user.user", "password123");
        userRole = new Role("USER", true);
        user.addRole(userRole);

        admin.activate();
        user.activate();

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(company);
            em.persist(admin);
            em.persist(user);
            em.persist(adminRole);
            em.persist(userRole);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        companyDTO = new CompanyDTO(company);
    }

    @AfterEach
    public void tearDown() {
        company = null;
        companyDTO = null;
        admin = null;
        adminRole = null;
        user = null;
        userRole = null;

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private String userLogin() {
        String email = user.getEmail();
        String password = "password123";

        return login(email, password);
    }

    private String adminLogin() {
        String email = admin.getEmail();
        String password = "password123";

        return login(email, password);
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
        String expected = GSON.toJson(Arrays.asList(companyDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/all")
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
                .when().get("company/all")
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
                .when().get("company/all")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id() {
        // Arrange
        String expected = GSON.toJson(companyDTO);
        String id = companyDTO.getId().toString();
        String jwt = adminLogin();

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .header("Authentication", jwt)
                .when().get("company/" + id)
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
                .when().get("company/" + id)
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
                .when().get("company/" + UUID.randomUUID().toString())
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
                .when().get("company/" + UUID.randomUUID().toString())
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_sanitization_exception() {
        // Arrange
        String id = "#####";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/" + id)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_name() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(companyDTO));
        String name = companyDTO.getName();
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/find/name/" + name)
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_name_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("company/find/name/test")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_name_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/find/name/test")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_name_sanitization_exception() {
        // Arrange
        String name = "#####";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/find/name/" + name)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_cvr() {
        // Arrange
        String expected = GSON.toJson(Arrays.asList(companyDTO));
        String cvr = companyDTO.getCvr();
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/find/cvr/" + cvr)
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_cvr_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("company/find/cvr/12345678")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_cvr_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/find/cvr/12345678")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_cvr_sanitization_exception() {
        // Arrange
        String cvr = "#####";
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/find/cvr/" + cvr)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

}
