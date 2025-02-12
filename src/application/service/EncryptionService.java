package application.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionService {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536; // Adjust this (higher is better, but slower)
    private static final int KEY_LENGTH = 256;   // Bits for the derived key

    public String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] encoded = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(encoded);
    }


    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Optional:  Add a way to hash a password *without* a salt, *only to be used during initial user creation/registration*:
    //  Do NOT use this method for login validation!
    public String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String salt = generateSalt(); // Generate a *new* salt each time.
        return hashPassword(password, salt);
    }



    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncryptionService encryptionService = new EncryptionService();

        String password = "mySecretPassword";

        // Generate salt ONCE when the user *registers*
        String salt = encryptionService.generateSalt();
        System.out.println("Generated Salt: " + salt);

        // Store the salt and hashed password in your database

        String hashedPassword = encryptionService.hashPassword(password, salt);
        System.out.println("Hashed Password: " + hashedPassword);


        // Simulate login attempt
        String loginPassword = "mySecretPassword";
        String retrievedSalt = salt; // Pretend you retrieved this from the database

        String loginHashedPassword = encryptionService.hashPassword(loginPassword, retrievedSalt);

        if (hashedPassword.equals(loginHashedPassword)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Login failed!");
        }
    }
}
