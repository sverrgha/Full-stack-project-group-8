package ntnu.idatt2105.project.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for password hashing and verification during login and registering.
 * This class provides methods to hash passwords using BCrypt and verify
 * plain passwords against hashed passwords.
 */
public class PasswordUtil {

  /**
   * Private constructor to prevent instantiation.
   */
  private PasswordUtil() {
  }

  /**
   * Hashes a password using BCrypt.
   *
   * @param password the password to hash
   * @return the hashed password
   */
  public static String hashPassword(String password) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.encode(password);
  }

    /**
     * Verifies a plain password against a hashed password.
     *
     * @param password       the password to verify
     * @param hashedPassword the hashed password to verify against
     * @return true if the password matches the hashed password, false otherwise
     */
  public static boolean verifyPassword(String password, String hashedPassword) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.matches(password, hashedPassword);
  }
}
