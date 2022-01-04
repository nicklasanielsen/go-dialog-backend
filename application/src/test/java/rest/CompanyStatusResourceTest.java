package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dtos.CompanyStatusDTO;
import entities.Company;
import entities.CompanyStatus;
import entities.CompanyStatusType;
import entities.Role;
import entities.User;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.time.LocalDateTime;
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
public class CompanyStatusResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/";

    private CompanyStatus companyStatus;
    private CompanyStatusDTO companyStatusDTO;

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
            em.createNamedQuery("CompanyStatus.deleteAllRows").executeUpdate();
            em.createNamedQuery("CompanyStatusType.deleteAllRows").executeUpdate();
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
            em.createNamedQuery("CompanyStatus.deleteAllRows").executeUpdate();
            em.createNamedQuery("CompanyStatusType.deleteAllRows").executeUpdate();
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
        companyStatus = new CompanyStatus(LocalDateTime.now());

        user = new User("user@user.user", "password123");
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
            em.persist(companyStatus);
            em.persist(user);
            em.persist(admin);
            em.persist(userRole);
            em.persist(adminRole);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        companyStatusDTO = new CompanyStatusDTO(companyStatus);
    }

    @AfterEach
    public void tearDown() {
        companyStatus = null;
        companyStatusDTO = null;

        user = null;
        admin = null;
        userRole = null;
        adminRole = null;

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.createNamedQuery("CompanyStatus.deleteAllRows").executeUpdate();
            em.createNamedQuery("CompanyStatusType.deleteAllRows").executeUpdate();
            em.createNamedQuery("Company.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
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
        String expected = GSON.toJson(Arrays.asList(companyStatusDTO));
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/all")
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
                .when().get("company/status/all")
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
                .when().get("company/status/all")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id() {
        // Arrange
        String expected = GSON.toJson(companyStatusDTO);
        String id = companyStatus.getId().toString();
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/" + id)
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_id_not_found() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String expected = "Bad Request";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/" + id)
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
                .when().get("company/status/" + UUID.randomUUID().toString())
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
                .when().get("company/status/" + UUID.randomUUID().toString())
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_id_sanitization_exception() {
        // Arrange
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String id = "#";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/" + id)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type() {
        // Arrange
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);
        companyStatus.setCompanyStatusType(companyStatusType);
        String jwt = adminLogin();

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(companyStatusType);
            em.merge(companyStatus);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        companyStatusDTO = new CompanyStatusDTO(companyStatus);
        String expected = GSON.toJson(Arrays.asList(companyStatusDTO));

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/type/" + companyStatusType.getType())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_type_not_found() {
        // Arrange
        String expected = "Bad Request";
        String type = "UNKNOWN";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/type/" + type)
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
                .when().get("company/status/find/type/test")
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
                .when().get("company/status/find/type/test")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_type_sanitization_exception() {
        // Arrange
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String type = "#####";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/type/" + type)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company() {
        // Arrange
        Company company = new Company("TEST", "12345678");
        companyStatus.setCompany(company);
        String jwt = adminLogin();

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(company);
            em.merge(companyStatus);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        String expected = GSON.toJson(Arrays.asList(companyStatusDTO));

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + company.getId().toString())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_company_not_found() {
        // Arrange
        String companyId = UUID.randomUUID().toString();
        String expected = "Bad Request";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + companyId)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + UUID.randomUUID().toString())
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + UUID.randomUUID().toString())
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_sanitization_exception() {
        // Arrange
        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";
        String companyId = "#";
        String jwt = adminLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + companyId)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_and_type() {
        // Arrange
        Company company = new Company("TEST", "12345678");
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);
        String jwt = adminLogin();

        companyStatus.setCompany(company);
        companyStatus.setCompanyStatusType(companyStatusType);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(company);
            em.persist(companyStatusType);
            em.merge(companyStatus);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        companyStatusDTO = new CompanyStatusDTO(companyStatus);
        String expected = GSON.toJson(Arrays.asList(companyStatusDTO));

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + company.getId().toString() + "/type/" + companyStatusType.getType())
                .then().statusCode(Status.OK.getStatusCode())
                .extract().asString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_and_type_company_not_found() {
        // Arrange
        String companyId = UUID.randomUUID().toString();
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);
        String jwt = adminLogin();

        companyStatus.setCompanyStatusType(companyStatusType);

        String expected = "Bad Request";

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + companyId + "/type/" + companyStatusType.getType())
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_and_type_type_not_found() {
        // Arrange
        Company company = new Company("TEST", "12345678");
        String type = "NON";
        String jwt = adminLogin();

        companyStatus.setCompany(company);

        String expected = "Bad Request";

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + company.getId().toString() + "/type/" + type)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_and_type_company_and_type_not_found() {
        // Arrange
        String companyId = UUID.randomUUID().toString();
        String type = "NON";
        String jwt = adminLogin();

        String expected = "Bad Request";

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + companyId + "/type/" + type)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_and_type_not_signed_in() {
        // Arrange
        String expected = "Unauthorized";

        // Act
        String actual = given()
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + UUID.randomUUID().toString() + "/type/test")
                .then().statusCode(Status.UNAUTHORIZED.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_and_type_missing_permission() {
        // Arrange
        String expected = "Forbidden";
        String jwt = userLogin();

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + UUID.randomUUID().toString() + "/type/test")
                .then().statusCode(Status.FORBIDDEN.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_and_type_company_sanitization_exception() {
        // Arrange
        String companyId = "#";
        CompanyStatusType companyStatusType = new CompanyStatusType("TEST", true);
        String jwt = adminLogin();

        companyStatus.setCompanyStatusType(companyStatusType);

        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + companyId + "/type/" + companyStatusType.getType())
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void get_by_company_and_type_type_sanitization_exception() {
        // Arrange
        Company company = new Company("TEST", "12345678");
        String type = "#";
        String jwt = adminLogin();

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(company);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        companyStatus.setCompany(company);

        String expected = "Din forespørgsel indeholdt tegn, vi ikke kan lide.";

        // Act
        String actual = given()
                .header("Authentication", jwt)
                .contentType(APPLICATION_JSON)
                .when().get("company/status/find/company/" + company.getId().toString() + "/type/" + type)
                .then().statusCode(Status.BAD_REQUEST.getStatusCode())
                .extract().path("message");

        // Assert
        assertEquals(expected, actual);
    }

}
