package ntnu.idatt2105.project.backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

  /**
   * Tests the hashPassword method with a valid password.
   * It verifies that the hashed password is not null, not equal to the original password
   * and is a bcrypt-compatible hash of the original password.
   */
  @Test
  void testHashPassword_validPassword_returnsHashedPassword() {
    String password = "password";
    String hashedPassword = PasswordUtil.hashPassword(password);
    assertNotNull(hashedPassword);
    assertNotEquals(password, hashedPassword);
    assertTrue(new BCryptPasswordEncoder().matches(password, hashedPassword));
  }

  /**
   * Tests the hashPassword method with an empty password.
   * It verifies that the hashed password is not null, not equal to the original password
   * and is a bcrypt-compatible hash of the original password.
   */
  @Test
  void testHashPassword_emptyPassword_returnsHashedPassword() {
    String password = "";
    String hashedPassword = PasswordUtil.hashPassword(password);
    assertNotNull(hashedPassword);
    assertNotEquals(password, hashedPassword);
    assertTrue(new BCryptPasswordEncoder().matches(password, hashedPassword));
  }

  /**
   * Tests the hashPassword method with a null password.
   * It verifies that an IllegalArgumentException is thrown.
   */
  @Test
  void testHashPassword_nullPassword_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      PasswordUtil.hashPassword(null);
    });
  }

  /**
   * Tests the verifyPassword method with a valid password and hashed password.
   * It verifies that the method returns true.
   */
  @Test
  void testVerifyPassword_validPasswordAndHashedPassword_returnsTrue() {
    String password = "password";
    String hashedPassword = PasswordUtil.hashPassword(password);
    assertTrue(PasswordUtil.verifyPassword(password, hashedPassword));
  }

  /**
   * Tests the verifyPassword method with an invalid password.
   * It verifies that the method returns false.
   */
  @Test
  void testVerifyPassword_invalidPassword_returnsFalse() {
    String password = "password";
    String hashedPassword = PasswordUtil.hashPassword(password);
    assertFalse(PasswordUtil.verifyPassword("wrongpassword", hashedPassword));
  }

  /**
   * Tests the verifyPassword method with an empty password.
   * It verifies that the method returns false.
   */
  @Test
  void testVerifyPassword_nullPassword_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      PasswordUtil.verifyPassword(null, "");
    });
  }

  /**
   * Tests the verifyPassword method with null as the hashed password.
   * It verifies that the method returns false.
   */
  @Test
  void testVerifyPassword_nullHashedPassword_returnsFalse() {
    String password = "password";
    assertFalse(PasswordUtil.verifyPassword(password, null));
  }
}
