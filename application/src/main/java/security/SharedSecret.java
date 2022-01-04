package security;

import io.jsonwebtoken.impl.crypto.MacProvider;
import javax.crypto.SecretKey;

/**
 *
 * @author Nicklas Nielsen
 */
public class SharedSecret {

    private static SecretKey secretKey;

    public static SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = MacProvider.generateKey(); // Algorithm: HMAC-SHA512
        }

        return secretKey;
    }

}
