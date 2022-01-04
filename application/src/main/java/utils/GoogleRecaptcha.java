package utils;

import errorhandling.exceptions.GoogleRecaptchaException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.container.ContainerRequestContext;

/**
 *
 * @author Nicklas Nielsen
 */
public class GoogleRecaptcha {

    private static final String API_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static String key;

    public static void verify(ContainerRequestContext request) throws GoogleRecaptchaException {
        boolean inProduction = System.getenv("DEPLOYED") != null;
        if (inProduction) {
            if (key == null) {
                key = getKey();
            }

            String token = request.getHeaderString("recaptcha");
            if (token == null) {
                throw new GoogleRecaptchaException("Token not provided");
            }

            HttpURLConnection connection = openConnection(token);
            checkResponse(connection);
        }
    }

    private static String getKey() {
        return System.getenv("GOOGLE_RECAPTCHA_KEY");
    }

    private static HttpURLConnection openConnection(String token) throws GoogleRecaptchaException {
        URL url;
        HttpURLConnection connection;

        String postParams = "secret=" + key + "&response=" + token;

        try {
            // Creating URL
            url = new URL(API_URL);

            // Creating connection
            connection = (HttpURLConnection) url.openConnection();

            // Setting headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("secret", key);
            connection.setRequestProperty("response", token);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
        } catch (IOException e) {
            throw new GoogleRecaptchaException("Failed to open connection");
        }

        return connection;
    }

    private static void checkResponse(HttpURLConnection connection) throws GoogleRecaptchaException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();

            boolean isHuman = jsonObject.getBoolean("success");
            if (!isHuman) {
                throw new GoogleRecaptchaException("Verification failed");
            }

        } catch (IOException e) {
            throw new GoogleRecaptchaException("Failed to get response from Google");
        }
    }

}
