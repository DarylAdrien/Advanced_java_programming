// src/main/java/com/votingsystem/util/PasswordUtil.java
package com.votingsystem.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for password hashing.
 * IMPORTANT: For production environments, use a stronger, salt-based hashing
 * algorithm like BCrypt, Argon2, or PBKDF2, which are designed to be slow
 * and resistant to brute-force attacks. This SHA-256 implementation is
 * for demonstration purposes only and is NOT secure enough for real-world use.
 */
public class PasswordUtil {

    /**
     * Hashes a plain text password using SHA-256.
     *
     * @param password The plain text password to hash.
     * @return The Base64 encoded SHA-256 hash of the password, or null if an error occurs.
     */
    public static String hashPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add password bytes to digest
            md.update(password.getBytes());

            // Get the hashed bytes
            byte[] hashedPassword = md.digest();

            // Encode the hashed bytes to Base64 to make it a String
            return Base64.getEncoder().encodeToString(hashedPassword);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifies a plain text password against a hashed password.
     *
     * @param plainPassword The plain text password provided by the user.
     * @param hashedPassword The hashed password stored in the database.
     * @return true if the plain password, when hashed, matches the stored hash; false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedAttempt = hashPassword(plainPassword);
        return hashedAttempt != null && hashedAttempt.equals(hashedPassword);
    }

    // Main method for testing (optional)
//    public static void main(String[] args) {
//    	
//        String plain = "mysecretpassword";
//        String hashed = hashPassword(plain);
//        System.out.println("Plain: " + plain);
//        System.out.println("Hashed: " + hashed);
//        System.out.println("Verification (correct): " + verifyPassword(plain, hashed));
//        System.out.println("Verification (incorrect): " + verifyPassword("wrongpassword", hashed));
//    }
    	public static void main(String[] args) {
    	     String plain = "admin123";
    	     String hashed = PasswordUtil.hashPassword(plain);
    	     System.out.println("Hashed 'admin123': " + hashed);
    	}
}
