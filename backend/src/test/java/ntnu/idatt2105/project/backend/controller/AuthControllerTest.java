package ntnu.idatt2105.project.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ntnu.idatt2105.project.backend.dto.request.LoginRequest;
import ntnu.idatt2105.project.backend.dto.request.RegisterRequest;
import ntnu.idatt2105.project.backend.dto.response.AuthResponse;
import ntnu.idatt2105.project.backend.enums.AuthResponseMessage;
import ntnu.idatt2105.project.backend.service.UserService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * This class contains unit tests for the AuthController class.
 * It uses MockMvc to perform HTTP requests and verify the responses.
 * The tests cover various scenarios for user registration and login,
 * including valid and invalid requests, as well as error handling.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Container
	private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
																	.withDatabaseName("testdb")
																	.withUsername("test")
																	.withPassword("test");

	@BeforeAll
	static void beforeAll() {
					mysql.start(); // Ensure the container starts
					System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
					System.setProperty("spring.datasource.username", mysql.getUsername());
					System.setProperty("spring.datasource.password", mysql.getPassword());
	}

  /**
   * Tests the registerUser method with a valid request.
   * It verifies that the user is registered successfully and a token is generated.
   * It also checks that the correct response is returned with status 201 Created.
   */
  @Test
  @WithAnonymousUser
  void registerUser_validRequest_returnsCreatedAndLoginResponse() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setFirstname("Ola");
    request.setLastname("Nordmann");
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("password123");
    request.setPhoneNumber("12345678");

    AuthResponse response = new AuthResponse("ola.nordman@gmail.com",
            AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage(), "mockedToken");

    when(userService.registerUser(any(RegisterRequest.class))).thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                    .value("ola.nordman@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                    .value(AuthResponseMessage.USER_REGISTERED_SUCCESSFULLY.getMessage()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.token")
                    .value("mockedToken"));
  }

  /**
   * Tests the registerUser method with an existing user.
   * It verifies that the user is not registered again and the correct response is
   * returned. It also checks that the status is 400 Bad Request.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  @WithAnonymousUser
  void registerUser_existingUser_returnsBadRequestAndLoginResponse() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setFirstname("Ola");
    request.setLastname("Nordmann");
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("password123");
    request.setPhoneNumber("12345678");

    AuthResponse response = new AuthResponse("ola.nordman@gmail.com",
            AuthResponseMessage.USER_ALREADY_EXISTS.getMessage(), null);

    when(userService.registerUser(any(RegisterRequest.class))).thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                    .value("ola.nordman@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                    .value(AuthResponseMessage.USER_ALREADY_EXISTS.getMessage()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").isEmpty());
  }

  /**
   * Tests the registerUser method with an invalid request.
   * It verifies that the request is not valid and the correct response is returned.
   * It also checks that the status is 400 Bad Request.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  @WithAnonymousUser
  void registerUser_invalidRequest_returnsBadRequest() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setFirstname("");
    request.setLastname("");
    request.setEmail("invalid-email");
    request.setPassword("short");
    request.setPhoneNumber("");

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  /**
   * Tests the registerUser method when the userService throws an exception.
   * It verifies that the exception is handled and the correct response is returned.
   * It also checks that the status is 500 Internal Server Error.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  @WithAnonymousUser
  void registerUser_userServiceThrowsException_returnsInternalServerError() throws Exception {
    RegisterRequest request = new RegisterRequest();
    request.setFirstname("Ola");
    request.setLastname("Nordmann");
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("password123");
    request.setPhoneNumber("12345678");
    when(userService.registerUser(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("Service error"));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                    .value("ola.nordman@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                    .value(AuthResponseMessage
                            .SAVING_USER_ERROR.getMessage() + "Service error"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.token")
                    .isEmpty());
  }

  /**
   * Tests the loginUser method with valid credentials.
   * It verifies that the user is logged in successfully and a token is generated.
   * It also checks that the correct response is returned with status 200 OK.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  @WithAnonymousUser
  void loginUser_validCredentials_returnsOkAndLoginResponse() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("password");

    AuthResponse response = new AuthResponse("ola.nordman@gmail.com",
            AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(), "token");

    when(userService.loginUser(any(LoginRequest.class))).thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                    .value("ola.nordman@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                    .value(AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.token")
                    .value("token"));
  }

  /**
   * Tests the loginUser method with invalid credentials.
   * It verifies that the user is not found and the correct response is returned.
   * It also checks that the status is 401 Unauthorized.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  @WithAnonymousUser
  void loginUser_invalidCredentials_returnsUnauthorizedAndLoginResponse() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("wrongPassword");

    AuthResponse response = new AuthResponse("ola.nordman@gmail.com",
            AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), null);

    when(userService.loginUser(any(LoginRequest.class))).thenReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                    .value("ola.nordman@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                    .value(AuthResponseMessage.INVALID_CREDENTIALS.getMessage()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").isEmpty());
  }

  /**
   * Tests the loginUser method when the loginUser in UserService throws.
   * It verifies that the request is not valid and the correct response is returned.
   * It also checks that the status is 500 internal server error.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  @WithAnonymousUser
  void loginUser_userServiceThrowsException_returnsInternalServerError() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setEmail("ola.nordman@gmail.com");
    request.setPassword("password");

    when(userService.loginUser(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Service error"));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                    .value("ola.nordman@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                    .value(AuthResponseMessage.USER_LOGIN_ERROR.getMessage()
                            + "Service error"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").isEmpty());
  }
}