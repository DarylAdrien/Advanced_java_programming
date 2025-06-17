// src/com/votingsystem/util/PasswordUtil.java
package com.votingsystem.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int SALT_LENGTH = 32; // 32 bytes = 256 bits

    /**
     * Generates a random salt.
     * @return A Base64 encoded string of the salt.
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password using SHA-256 with a given salt.
     * @param password The plaintext password.
     * @param salt The salt to use.
     * @return The SHA-256 hash of the salted password, Base64 encoded.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.getDecoder().decode(salt)); // Add salt
            byte[] hashedPassword = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            // This should not happen with "SHA-256"
            System.err.println("SHA-256 algorithm not found: " + e.getMessage());
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against a stored hash and salt.
     * @param plainPassword The password entered by the user.
     * @param storedHash The hashed password from the database.
     * @param storedSalt The salt from the database.
     * @return True if the password matches, false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String storedHash, String storedSalt) {
        String newHash = hashPassword(plainPassword, storedSalt);
        return newHash.equals(storedHash);
    }

    public static void main(String[] args) {
        // Example usage:
        String password = "mySecurePassword123";
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        System.out.println("Plain Password: " + password);
        System.out.println("Generated Salt: " + salt);
        System.out.println("Hashed Password: " + hashedPassword);

        // Verification test
        boolean verified = verifyPassword(password, hashedPassword, salt);
        System.out.println("Password Verified: " + verified);

        boolean wrongPasswordVerified = verifyPassword("wrongPassword", hashedPassword, salt);
        System.out.println("Wrong Password Verified: " + wrongPasswordVerified);
    }
}