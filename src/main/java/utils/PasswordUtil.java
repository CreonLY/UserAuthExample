package utils;

import entities.exceptions.AuthorizationFailException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class PasswordUtil {

    public static final int SALT_SIZE_BYTE = 16;
    public static final int HASH_SIZE_BIT = 64 << 2;
    public static final int ITERATIONS_DEFAULT = 10_000;
    public static final String ALGORITHM_NAME = "PBKDF2&SHA256";

    /**
     * generate the encoded password
     *
     * @param pwd raw password
     * @return the encoded string
     */
    public static String encode(String pwd) {
        return encode(pwd, getSalt(), ITERATIONS_DEFAULT);
    }

    /**
     * generate the encoded password
     *
     * @param pwd  raw password
     * @param salt salt value fot algorithm
     * @return the encoded string
     */
    public static String encode(String pwd, String salt) {
        return encode(pwd, salt, ITERATIONS_DEFAULT);
    }

    /**
     * generate the encoded password
     *
     * @param pwd        raw password
     * @param iterations encryption times
     * @return the encoded string
     */
    public static String encode(String pwd, int iterations) {
        return encode(pwd, getSalt(), iterations);
    }

    /**
     * generate the encoded password
     *
     * @param pwd        raw password
     * @param salt       salt value fot algorithm
     * @param iterations encryption times
     * @return the encoded string
     */
    public static String encode(String pwd, String salt, int iterations) {
        String hash = getEncodedHash(pwd, salt, iterations);
        return String.format("%s$%d$%s$%s",
                ALGORITHM_NAME,
                iterations,
                salt,
                hash);
    }

    /**
     * verify given password corresponds encoded one
     *
     * @param pwd raw password
     * @param enc encoded password
     * @return <i>true</i> if corresponding, <i>false</i> otherwise
     */
    public static boolean verification(String pwd, String enc) throws AuthorizationFailException {
        String[] parts = enc.split("\\$");
        String encode = encode(pwd, parts[2], Integer.parseInt(parts[1]));
        if(!enc.equals(encode))
            throw new AuthorizationFailException("");
        return true;
    }

    ;

    /**
     * generate the encoded hash
     *
     * @param pwd        raw password
     * @param salt       salt value fot algorithm
     * @param iterations encryption times
     * @return the encoded string
     */
    public static String getEncodedHash(String pwd, String salt, int iterations) {
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        PBEKeySpec keySpec =
                new PBEKeySpec(
                        pwd.toCharArray(),
                        salt.getBytes(StandardCharsets.UTF_8),
                        iterations,
                        HASH_SIZE_BIT);
        SecretKey secret = null;
        try {
            secret = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
        return toHex(secret.getEncoded());
    }

    /**
     * generate salt value, consists of digits and alphabet
     * (both upper & downer case)
     *
     * @return salt string
     */
    public static String getSalt() {
        Random random = new Random();
        char[] res = new char[SALT_SIZE_BYTE];
        for (int i = 0; i < res.length; i++) {
            switch (random.nextInt(3)) {
                case 0 -> res[i] = (char) (random.nextInt(10) + '0');
                case 1 -> res[i] = (char) (random.nextInt(26) + 'a');
                case 2 -> res[i] = (char) (random.nextInt(26) + 'A');
            }
        }
        return new String(res);
    }

    /**
     * convert bytes to hex string
     * @param arr byte array
     * @return hex string
     */
    private static String toHex(byte[] arr) {
        String hex = new BigInteger(1, arr).toString(16);
        int padding = (arr.length << 1) - hex.length();
        return "0".repeat(padding) + hex;
    }
}
