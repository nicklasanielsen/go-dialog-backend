package utils;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Nicklas Nielsen
 */
public class EMF_Creator {

    private static final String PERSISTENCE_UNIT_NAME = "pu";
    private static final String PERSISTENCE_UNIT_NAME_FOR_TEST = "puTest";

    /**
     * Used for testing.
     *
     * Call this method before all integration tests that uses the Grizzly
     * server and the test database (in @BeforeAll)
     */
    public static void startREST_TestWithDB() {
        System.setProperty("IS_INTEGRATION_TEST_WITH_DB", "testing");
    }

    /**
     * *
     * Used for testing.
     *
     * Call this method after all integration tests that uses the Grizzly server
     * and the test database (in @AfterAll)
     */
    public static void endREST_TestWithDB() {
        System.clearProperty("IS_INTEGRATION_TEST_WITH_DB");
    }

    public static EntityManagerFactory createEntityManagerFactory() {
        return createEntityManagerFactory(false);
    }

    public static EntityManagerFactory createEntityManagerFactoryForTest() {
        return createEntityManagerFactory(true);
    }

    private static EntityManagerFactory createEntityManagerFactory(boolean isTest) {
        // Used in production
        if (isDeployed()) {
            String dbUser = System.getenv("USER");
            String dbPass = System.getenv("PW");
            String dbConnectionString = System.getenv("CONNECTION_STR");

            Properties props = getProps(dbUser, dbPass, dbConnectionString);

            return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
        }

        // Used for testing and development
        String puName = getPersistenceUnitName(isTest);
        EntityManagerFactory emf = null;

        try {
            emf = Persistence.createEntityManagerFactory(puName, null);
        } catch (javax.persistence.PersistenceException ex) {
            System.out.println("##########################################################");
            System.out.println("######                                              ######");
            System.out.println("######      ERROR Creating a persistence Unit       ######");
            System.out.println("###### Have you started the dev and test databases? ######");
            System.out.println("######                                              ######");
            System.out.println("##########################################################");
            throw ex;
        }

        return emf;
    }

    private static boolean isDeployed() {
        return System.getenv("DEPLOYED") != null;
    }

    private static Properties getProps(String dbUser, String dbPass, String dbConnectionString) {
        Properties props = new Properties();

        props.setProperty("javax.persistence.jdbc.user", dbUser);
        props.setProperty("javax.persistence.jdbc.password", dbPass);
        props.setProperty("javax.persistence.jdbc.url", dbConnectionString);
        props.setProperty("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");

        // Sets the production log-level to show only potential problems
        props.setProperty("eclipselink.logging.level", "WARNING");
        props.setProperty("eclipselink.logging.level.sql", "WARNING");

        return props;
    }

    private static String getPersistenceUnitName(boolean isTest) {
        return isTest || System.getProperty("IS_INTEGRATION_TEST_WITH_DB") != null ? PERSISTENCE_UNIT_NAME_FOR_TEST : PERSISTENCE_UNIT_NAME;
    }

}
