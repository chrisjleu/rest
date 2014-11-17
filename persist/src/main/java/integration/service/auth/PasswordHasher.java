package integration.service.auth;

import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * TODO Check this solution is sound
 * (http://stackoverflow.com/questions/2860943/suggestions-for-library-to-hash-passwords-in-java)
 */
public class PasswordHasher {

    private static final int ITERATIONS = 10 * 1024;
    private static final int SALT_LENGTH = 32;
    private static final int DESIRED_KEY_LENGTH = 256;

    /**
     * Checks whether given plain text password corresponds to a stored salted hash of the password.
     * 
     * @param password
     * @param stored
     * @return
     * @throws Exception
     */
    public static boolean check(String password, String stored) throws Exception {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2)
            return false;
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    /**
     * Computes a salted PBKDF2 hash of given plain text password suitable for storing in a database. Empty passwords
     * are not supported.
     * 
     * @param rawPassword
     * @return
     * @throws Exception
     */
    public static String hashPassword(String rawPassword) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(SALT_LENGTH);
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hash(rawPassword, salt);
    }

    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, DESIRED_KEY_LENGTH));
        return Base64.encodeBase64String(key.getEncoded());
    }
}
