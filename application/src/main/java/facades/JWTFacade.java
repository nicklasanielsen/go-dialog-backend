package facades;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entities.RevokedJWT;
import entities.User;
import errorhandling.exceptions.DatabaseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import security.SharedSecret;

/**
 *
 * @author Nicklas Nielsen
 */
public class JWTFacade {

    private static EntityManagerFactory emf = null;
    private static JWTFacade instance = null;
    private static UserFacade userFacade = null;

    private static final long JWT_LIFE_TIME = TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES); // 30 min
    private static final String JWT_ISSUER = "godialog.cphbusiness";

    private JWTFacade() {
        // private to ensure singleton
    }

    public static JWTFacade getJWTFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new JWTFacade();
            userFacade = UserFacade.getUserFacade(_emf);
        }

        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public String create(User user) throws JOSEException {
        String subject, audince, tokenId;
        List<String> roles = new ArrayList<>();
        Date issueTime, expirationTime;

        subject = user.getId().toString();
        audince = user.getId().toString();

        user.getRoles().forEach(role -> {
            roles.add(role.getType());
        });

        issueTime = new Date();
        expirationTime = new Date(issueTime.getTime() + JWT_LIFE_TIME);

        tokenId = UUID.randomUUID().toString();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(JWT_ISSUER)
                .audience(audince)
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .claim("user_id", user.getId().toString())
                .claim("roles", String.join(",", roles))
                .claim("token_id", tokenId)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);
        signedJWT.sign(new MACSigner(SharedSecret.getSecretKey()));

        return signedJWT.serialize();
    }

    public void revoke(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            String jwtID = signedJWT.getJWTClaimsSet().getClaim("token_id").toString();
            long JWTExpirationTime = signedJWT.getJWTClaimsSet().getExpirationTime().getTime();

            revoke(token, Instant.ofEpochMilli(JWTExpirationTime).atZone(ZoneId.systemDefault()).toLocalDateTime());
        } catch (Exception e) {

        }
    }

    public void revoke(String token, LocalDateTime expire) throws DatabaseException {
        RevokedJWT revokedJWT = new RevokedJWT(token, expire);

        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(revokedJWT);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new DatabaseException();
        } finally {
            em.close();
        }
    }

    public boolean isRevoked(String token) {
        EntityManager em = getEntityManager();

        try {
            RevokedJWT jwt = em.find(RevokedJWT.class, token);

            return jwt != null;
        } finally {
            em.close();
        }
    }

    public String revokeAndCreateNewToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            String jwtID = signedJWT.getJWTClaimsSet().getClaim("token_id").toString();
            String userId = signedJWT.getJWTClaimsSet().getClaim("user_id").toString();

            long JWTExpirationTime = signedJWT.getJWTClaimsSet().getExpirationTime().getTime();

            revoke(jwtID, Instant.ofEpochMilli(JWTExpirationTime).atZone(ZoneId.systemDefault()).toLocalDateTime());

            User user = userFacade.getById(UUID.fromString(userId));

            return create(user);
        } catch (Exception e) {
            return null;
        }
    }

}
