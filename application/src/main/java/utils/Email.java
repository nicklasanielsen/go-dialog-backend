package utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import entities.Company;
import entities.Interview;
import entities.User;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nicklas Nielsen
 */
public class Email {

    private static final String BASE_URL = "https://www.nicklasnielsen.dk";
    private static final String API_URL = "https://api.eu.mailgun.net/v3/nicklasnielsen.dk/messages";
    private static String API_KEY = null;

    private static boolean inProduction() {
        return System.getenv("DEPLOYED") != null;
    }

    private static void setApiKey() {
        API_KEY = System.getenv("MAILGUN_KEY");
    }

    private static void send(Map<String, Object> parameters) {
        if (inProduction()) {
            if (API_KEY == null) {
                setApiKey();
            }

            try {
                HttpResponse<JsonNode> request = Unirest.post(API_URL)
                        .basicAuth("api", API_KEY)
                        .fields(parameters)
                        .asJson();

                System.out.println(request.getBody());

                System.out.println("Email send");
            } catch (UnirestException ex) {
                Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("E-mail not send, not in production");
        }
    }

    public static void userAndCompanyCreation(User user) {
        // Default values
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "GoDialog <noreply@nicklasnielsen.dk>");
        parameters.put("to", user.getEmail());
        parameters.put("subject", "Oprettelse af konto");

        // Template
        parameters.put("template", "user_and_company_creation");

        // Template parameters
        String link = BASE_URL + "/account-activation/" + user.getId().toString() + "/" + user.getActivationCode().toString();
        parameters.put("v:link", link);

        // Send email
        send(parameters);
    }

    public static void userCreation(User user) {
        // Default values
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "GoDialog <noreply@nicklasnielsen.dk>");
        parameters.put("to", user.getEmail());
        parameters.put("subject", "Oprettelse af konto");

        // Template
        parameters.put("template", "user_creation");

        // Template parameters
        String link = BASE_URL + "/account-activation/" + user.getId().toString() + "/" + user.getActivationCode().toString() + "/process";
        parameters.put("v:link", link);

        // Send email
        send(parameters);
    }

    public static void userCreationAlreadyExists(User user) {
        // Default values
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "GoDialog <noreply@nicklasnielsen.dk>");
        parameters.put("to", user.getEmail());
        parameters.put("subject", "Oprettelse af konto // Konto allerede i brug");

        // Template
        parameters.put("template", "user_creation_already_exists");

        // Send email
        send(parameters);
    }

    public static void accountActivation(User user) {
        // Default values
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "GoDialog <noreply@nicklasnielsen.dk>");
        parameters.put("to", user.getEmail());
        parameters.put("subject", "Velkommen til GoDialog!");

        // Template
        parameters.put("template", "account_activation");

        // Send email
        send(parameters);
    }

    public static void accountRecovery(User user) {
        // Default values
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "GoDialog <noreply@nicklasnielsen.dk>");
        parameters.put("to", user.getEmail());
        parameters.put("subject", "Nulstilling af adgangskode");

        // Template
        parameters.put("template", "user_password_reset");

        // Tamplate parameters
        String link = BASE_URL + "/account-recovery/" + user.getId().toString() + "/" + user.getRecoveryCode().toString();
        parameters.put("v:link", link);
        parameters.put("v:expiration", user.getRecoveryCodeExpiration());

        // Send email
        send(parameters);
    }

    public static void passwordReset(User user) {
        // Default values
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "GoDialog <noreply@nicklasnielsen.dk>");
        parameters.put("to", user.getEmail());
        parameters.put("subject", "Adgangskode nulstillet");

        // Template
        parameters.put("template", "password_reset_success");

        // Send email
        send(parameters);
    }

    public static void inviteToPlatform(String email, Company company) {
        // Default values
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "GoDialog <noreply@nicklasnielsen.dk>");
        parameters.put("to", email);
        parameters.put("subject", "Invitation til GoDialog");

        // Template
        parameters.put("template", "invite_to_platform");

        // Template parameters
        String link = BASE_URL + "/invite/" + company.getId().toString();
        parameters.put("v:link", link);
        parameters.put("v:companyName", company.getName());

        // Send email
        send(parameters);
    }

    public static void inviteToInterview(Interview interview, User user) {
        // Default values
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "GoDialog <noreply@nicklasnielsen.dk>");
        parameters.put("to", user.getEmail());
        parameters.put("subject", "Invitation til samtale");

        // Template
        parameters.put("template", "invite_to_interview");

        // Template parameters
        parameters.put("v:held", interview.getHeld());

        // Send email
        send(parameters);
    }

}
