package ntnu.idatt2105.project.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import ntnu.idatt2105.project.backend.dto.request.LoginRequest;
import ntnu.idatt2105.project.backend.dto.request.RegisterRequest;
import ntnu.idatt2105.project.backend.dto.response.LoginResponse;
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

    LoginResponse response = userService.registerUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals("User registered successfully", response.getMessage());
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

    LoginResponse response = userService.registerUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals("User already exists", response.getMessage());
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

    LoginResponse response = userService.registerUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals("Error saving user: Database error", response.getMessage());
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

    LoginResponse response = userService.loginUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals("User logged in successfully", response.getMessage());
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

    LoginResponse response = userService.loginUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals("User not found", response.getMessage());
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

    LoginResponse response = userService.loginUser(request);

    assertNotNull(response);
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals("Invalid password", response.getMessage());
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
}
