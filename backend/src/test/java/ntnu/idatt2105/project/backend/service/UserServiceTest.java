package ntnu.idatt2105.project.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.Optional;

import ntnu.idatt2105.project.backend.dto.request.LoginRequest;
import ntnu.idatt2105.project.backend.dto.request.ModifyUserRequest;
import ntnu.idatt2105.project.backend.dto.request.RegisterRequest;
import ntnu.idatt2105.project.backend.dto.response.AuthResponse;
import ntnu.idatt2105.project.backend.dto.response.UserResponse;
import ntnu.idatt2105.project.backend.enums.AuthResponseMessage;
import ntnu.idatt2105.project.backend.security.JwtUtil;
import ntnu.idatt2105.project.backend.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ntnu.idatt2105.project.backend.model.User;
import ntnu.idatt2105.project.backend.repository.UserRepo;

class UserServiceTest {

  @Mock
  private UserRepo userRepo;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private UserService userService;

  /**
   * This method is called before each test case to initialize the mocks.
   * It uses MockitoAnnotations to create mock objects for the UserRepo and JwtUtil.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Tests the registerUser method with a new email.
   * It verifies that the user is saved and a token is generated.
   * It also checks that the correct response is returned.
   */
  @Test
  void registerUser_newEmail_UserIsSavedAndTokenGenerated() {
    RegisterRequest request = new RegisterRequest();
    request.setFirstname("Ola");
    request.setLastname("Nordmann");
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("password123");
    request.setPhoneNumber("12345678");

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(jwtUtil.generateToken(request.getEmail())).thenReturn("mockedToken");
    when(userRepo.save(any(User.class))).thenReturn(Optional.of(new User(request.getFirstname(), request.getLastname(), request.getEmail(), request.getPhoneNumber(), PasswordUtil.hashPassword(request.getPassword()))));

    AuthResponse response = userService.registerUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals(AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage(), response.getMessage());
    assertEquals("mockedToken", response.getToken());
    verify(userRepo, times(1)).findByEmail(request.getEmail());
    verify(userRepo, times(1)).save(any(User.class));
    verify(jwtUtil, times(1)).generateToken(request.getEmail());
  }

  /**
   * Tests the registerUser method with an existing email.
   * It verifies that the user is not saved and the correct response is returned.
   */
  @Test
  void registeredUser_existingEmail_returnsUserAlreadyExists() {
    RegisterRequest request = new RegisterRequest();
    request.setFirstname("Ola");
    request.setLastname("Nordmann");
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("password123");
    request.setPhoneNumber("12345678");
    User existingUser = new User(request.getFirstname(), request.getLastname(), request.getEmail(), request.getPhoneNumber(), PasswordUtil.hashPassword(request.getPassword()));

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

    AuthResponse response = userService.registerUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals(AuthResponseMessage.USER_ALREADY_EXISTS.getMessage(), response.getMessage());
    assertNull(response.getToken());
    verify(userRepo, times(1)).findByEmail(request.getEmail());
    verify(userRepo, never()).save(any(User.class));
    verify(jwtUtil, never()).generateToken(request.getEmail());
  }

  /**
   * Tests the registerUser method with a database error.
   * It verifies that the user is not saved and the correct response is returned.
   */
  @Test
  void registerUser_databaseError_returnsErrorSavingUser() {
    RegisterRequest request = new RegisterRequest();
    request.setFirstname("Ola");
    request.setLastname("Nordmann");
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("password123");
    request.setPhoneNumber("12345678");

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(userRepo.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

    AuthResponse response = userService.registerUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals(AuthResponseMessage.SAVING_USER_ERROR.getMessage()
            + "Database error", response.getMessage());
    assertNull(response.getToken());
    verify(userRepo, times(1)).findByEmail(request.getEmail());
    verify(userRepo, times(1)).save(any(User.class));
    verify(jwtUtil, never()).generateToken(anyString());
  }

  /**
   * Tests the loginUser method with a correct password to an existing user.
   * It verifies that the login is successful and a token is generated.
   * It also checks that the correct response is returned.
   */
  @Test
  void loginUser_existingUserAndPasswordMatch_returnsSuccessAndToken() {
    LoginRequest request = new LoginRequest();
    request.setEmail("ola.nordmann@gmail.com");
    request.setPassword("password123");

    User existingUser = new User("Ola", "Nordmann", request.getEmail(), "12345678", PasswordUtil.hashPassword(request.getPassword()));
    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
    when(jwtUtil.generateToken(request.getEmail())).thenReturn("mockedToken");

    AuthResponse response = userService.loginUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals(AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(), response.getMessage());
    assertEquals("mockedToken", response.getToken());
    verify(userRepo, times(1)).findByEmail(request.getEmail());
    verify(jwtUtil, times(1)).generateToken(request.getEmail());
  }

  /**
   * Tests the loginUser method with a non-existing user.
   * It verifies that the login fails and the correct response is returned.
   * It also checks that no token is generated.
   */
  @Test
  void loginUser_nonExistingUser_returnsUserNotFound() {
    LoginRequest request = new LoginRequest();
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("wrongPassword");

    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());

    AuthResponse response = userService.loginUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals(AuthResponseMessage.USER_NOT_FOUND.getMessage(), response.getMessage());
    assertNull(response.getToken());
    verify(userRepo, times(1)).findByEmail(request.getEmail());
    verify(jwtUtil, never()).generateToken(anyString());
  }

  /**
   * Tests the loginUser method with an existing user but incorrect password.
   * It verifies that the login fails and the correct response is returned.
   * It also checks that no token is generated.
   */
  @Test
  void loginUser_existingUserIncorrectPassword_returnsInvalidPassword() {
    LoginRequest request = new LoginRequest();
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("wrongPassword");
    User existingUser = new User("Ola", "Nordmann", request.getEmail(), "12345678", PasswordUtil.hashPassword("correctPassword"));
    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

    AuthResponse response = userService.loginUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), response.getMessage());
    assertNull(response.getToken());
    verify(userRepo, times(1)).findByEmail(request.getEmail());
    verify(jwtUtil, never()).generateToken(anyString());
  }

  /**
   * Tests the findByEmail method with an email to a registered user.
   * It verifies that the user is found and the correct user is returned.
   */
  @Test
  void testFindByEmail_Success() {

    String email = "test@example.com";
    User user = new User(1L, "John", "Doe", email, "12345678", "password", false, null);
    when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

    UserDetails userDetails = userService.loadUserByUsername(email);

    assertNotNull(userDetails);
    assertEquals(email, userDetails.getUsername());
  }

  /**
   * Tests the findByEmail method with an email to a non-registered user.
   * It verifies that a UsernameNotFoundException is thrown.
   */
  @Test
  void testLoadUserByUsername_UserNotFound() {

    String email = "notfound@example.com";
    when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
  }

  /**
   * Tests the findByEmail method with an email to a registered user.
   * It verifies that the user is found and true is returned.
   */
  @Test
  void userExists_existingUser_returnsTrue() {
    long userId = 123L;
    when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));

    boolean exists = userService.userExists(userId);

    assertTrue(exists);
    verify(userRepo, times(1)).findById(userId);
  }

  /**
   * Tests the findByEmail method with an email to a non-registered user.
   * It verifies that the user is not found and the false is returned.
   */
  @Test
  void userExists_nonExistingUser_returnsFalse() {
    long userId = 456L;
    when(userRepo.findById(userId)).thenReturn(Optional.empty());

    boolean exists = userService.userExists(userId);

    assertFalse(exists);
    verify(userRepo, times(1)).findById(userId);
  }

  /**
   * Tests the validateUserIdMatchesToken method with a matching user ID and token.
   * It verifies that the user ID matches the email in the token and true is returned.
   */
  @Test
  void validateUserIdMatchesToken_matchingUserAndToken_returnsTrue() {
    long userId = 789L;
    String token = "mockedToken";
    User user = new User();
    user.setEmail("test@example.com");
    when(userRepo.findById(userId)).thenReturn(Optional.of(user));
    when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");

    boolean matches = userService.validateUserIdMatchesToken(userId, token);

    assertTrue(matches);
    verify(userRepo, times(1)).findById(userId);
    verify(jwtUtil, times(1)).extractUsername(token);
  }

  /**
   * Tests the validateUserIdMatchesToken method with a non-matching user ID and token.
   * It verifies that the user ID does not match the email in the token and false is returned.
   */
  @Test
  void validateUserIdMatchesToken_nonMatchingUserAndToken_returnsFalse() {
    long userId = 101L;
    String token = "mockedToken";
    User user = new User();
    user.setEmail("test@example.com");
    when(userRepo.findById(userId)).thenReturn(Optional.of(user));
    when(jwtUtil.extractUsername(token)).thenReturn("different@example.com");

    boolean matches = userService.validateUserIdMatchesToken(userId, token);

    assertFalse(matches);
    verify(userRepo, times(1)).findById(userId);
    verify(jwtUtil, times(1)).extractUsername(token);
  }

  /**
   * Tests the validateUserIdMatchesToken method with a non-existing user.
   * It verifies that the user is not found and false is returned.
   */
  @Test
  void validateUserIdMatchesToken_userNotFound_returnsFalse() {
    long userId = 202L;
    String token = "mockedToken";
    when(userRepo.findById(userId)).thenReturn(Optional.empty());

    boolean matches = userService.validateUserIdMatchesToken(userId, token);

    assertFalse(matches);
    verify(userRepo, times(1)).findById(userId);
    verify(jwtUtil, never()).extractUsername(anyString());
  }

  /**
   * Tests the getUserById method with an existing user ID.
   * It verifies that the user is found and the correct user response is returned.
   * It also checks that the user ID, first name, last name, email, phone number,
   * and admin status are correct.
   */
  @Test
  void getUserById_existingUser_returnsUserResponse() {
    Long userId = 1L;
    User user = new User(userId, "John", "Doe", "john.doe@example.com",
            "1234567890", "password", true, Date.valueOf("2023-10-01"));
    when(userRepo.findById(userId)).thenReturn(Optional.of(user));

    UserResponse response = userService.getUserById(userId);

    assertNotNull(response);
    assertEquals(userId, response.getId());
    assertEquals("John", response.getFirstname());
    assertEquals("Doe", response.getLastname());
    assertEquals("john.doe@example.com", response.getEmail());
    assertEquals("1234567890", response.getPhoneNumber());
    assertTrue(response.isAdmin());
  }

  /**
   * Tests the getUserById method with a non-existing user ID.
   * It verifies that an IllegalArgumentException is thrown.
   */
  @Test
  void getUserById_nonExistingUser_throwsIllegalArgumentException() {
    Long userId = 2L;
    when(userRepo.findById(userId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> userService.getUserById(userId));
  }

  /**
   * Tests the updateUser method with a valid request and matching token.
   * It verifies that the user is updated successfully,
   * since no exceptions are thrown.
   */
  @Test
  void updateUser_validRequestAndMatchingToken_userUpdated() {
    Long userId = 3L;
    String token = "mockedToken";
    ModifyUserRequest request = new ModifyUserRequest();
    request.setFirstname("Jane");
    request.setLastname("Smith");
    request.setEmail("jane.smith@example.com");
    request.setPhoneNumber("0987654321");

    User oldUser = new User(userId, "Old", "User", "old@example.com", "111222333",
            "oldPass", false, Date.valueOf("2023-10-01"));
    when(userRepo.findById(userId)).thenReturn(Optional.of(oldUser));
    when(jwtUtil.extractUsername(token)).thenReturn(oldUser.getEmail());

    userService.updateUser(userId, request, token);

    verify(userRepo, times(1)).update(any(User.class));
  }

  /**
   * Tests the updateUser method with a non-matching token.
   * It verifies that an IllegalArgumentException is thrown,
   * indicating that the user ID does not match the token.
   */
  @Test
  void updateUser_nonMatchingToken_throwsIllegalArgumentException() {
    Long userId = 4L;
    String token = "mockedToken";
    ModifyUserRequest request = new ModifyUserRequest();
    request.setFirstname("Jane");
    request.setLastname("Smith");
    request.setEmail("jane.smith@SecondGo.com");

    User oldUser = new User(userId, "Old", "User", "old@example.com", "111222333",
            "oldPass", false, Date.valueOf("2023-10-01"));
    when(userRepo.findById(userId)).thenReturn(Optional.of(oldUser));
    when(jwtUtil.extractUsername(token)).thenReturn("wrong@email.com");

    assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, request, token));
    verify(userRepo, never()).update(any(User.class));
  }

  /**
   * Tests the updateUser method with empty fields in the request.
   * It verifies that the empty/null fields are not updated
   * and the old values are retained.
   */
  @Test
  void updateUser_emptyFieldsInRequest_fieldsNotUpdated() {
    Long userId = 5L;
    String token = "mockedToken";
    ModifyUserRequest request = new ModifyUserRequest();
    request.setFirstname(null);
    request.setLastname("");
    request.setEmail("invalid-email");
    request.setPhoneNumber("");

    User oldUser = new User(userId, "Old", "User", "old@example.com",
            "111222333", "oldPass", false, Date.valueOf("2023-10-01"));
    when(userRepo.findById(userId)).thenReturn(Optional.of(oldUser));
    when(jwtUtil.extractUsername(token)).thenReturn(oldUser.getEmail());

    userService.updateUser(userId, request, token);

    verify(userRepo, times(1)).update(argThat(updatedUser ->
            updatedUser.getFirstname().equals("Old") &&
                    updatedUser.getLastname().equals("User") &&
                    updatedUser.getEmail().equals("old@example.com") &&
                    updatedUser.getPhoneNumber().equals("111222333")
    ));
  }
}
