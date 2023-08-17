package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    /**
     * default expire duration is 2 hours
     */
    private static final long EXPIRE_DEFAULT_MILLIS = 7_200_000L;
    private static final String TOKEN_SECRET = "c290887021c7ecae6b2b5d57d210d74a";

    /**
     * generate token with given uuid
     *
     * @param uuid unique key
     * @return token object
     */
    public static String getToken(String uuid) {
        return getToken(uuid, EXPIRE_DEFAULT_MILLIS);
    }

    /**
     * generate token with given uuid and custom expire time
     *
     * @param uuid       unique id
     * @param expiration expiration time in millis
     * @return token object
     */
    public static String getToken(String uuid, long expiration) {
        long curTime = System.currentTimeMillis();
        return getToken(uuid, curTime, expiration);
    }

    private static String getToken(String uuid, long createTime, long expiration) {
        String token;
        Date expireTime = new Date(createTime + expiration);

        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);

        Map<String, Object> header = new HashMap<>() {{
            put("typ", "JWT");
            put("alg", "HS256");
        }};

        try {
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("uuid", uuid)
                    .withExpiresAt(expireTime)
                    .sign(algorithm);
        } catch (IllegalArgumentException | JWTCreationException e) {
            logger.info(e.getMessage(), e);
            return null;
        }

        return token;
    }

    /**
     * verify given token
     *
     * @param token the given token
     * @return <i>true</i> if verified, <i>false</i> otherwise
     */
    public static boolean isValidated(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            verifier.verify(token);
        } catch (JWTVerificationException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return true;
    }

    /**
     * verify given token
     *
     * @param token the given token
     * @return <i>true</i> if verified, <i>false</i> otherwise
     */
    public static DecodedJWT validate(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            logger.info(e.getMessage());
            throw e;
        }
    }
}