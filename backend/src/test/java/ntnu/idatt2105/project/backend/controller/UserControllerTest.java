package ntnu.idatt2105.project.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ntnu.idatt2105.project.backend.dto.request.ModifyUserRequest;
import ntnu.idatt2105.project.backend.dto.response.UserResponse;
import ntnu.idatt2105.project.backend.service.UserService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  private static Long userId;
  private static String validToken;
  private static String extractedToken;
  private static ModifyUserRequest modifyRequest;
  private static UserResponse mockUserResponse;

  @Container
  private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @BeforeAll
  static void beforeAll() {
    mysql.start();
    System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
    System.setProperty("spring.datasource.username", mysql.getUsername());
    System.setProperty("spring.datasource.password", mysql.getPassword());
  }

  @BeforeAll
  static void setUp() {
    userId = 1L;
    validToken = "Bearer validToken";
    extractedToken = "validToken";
    modifyRequest = new ModifyUserRequest();
    mockUserResponse = new UserResponse();
  }

  /**
   * Verify successful retrieval of a user by their ID.
   * Expects HTTP 200 OK and the `UserResponse` in JSON format.
   * @throws Exception if the MockMvc performs an action that results in an error.
   */
  @Test
  @WithMockUser("test")
  void getUserById_validId_returnsOkAndUserResponse() throws Exception {
    when(userService.getUserById(userId)).thenReturn(mockUserResponse);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(mockUserResponse)));

    verify(userService, times(1)).getUserById(userId);
  }

  /**
   * Verify bad request when retrieving a user with an invalid ID.
   * Expects HTTP 400 Bad Request and an empty `UserResponse` in JSON format.
   * @throws Exception if the MockMvc performs an action that results in an error.
   */
  @Test
  @WithMockUser("test")
  void getUserById_invalidId_returnsBadRequestAndEmptyUserResponse() throws Exception {
    when(userService.getUserById(userId)).thenThrow(new IllegalArgumentException("Invalid ID"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new UserResponse())));

    verify(userService, times(1)).getUserById(userId);
  }

  /**
   * Verify internal server error when the user service throws an exception during retrieval.
   * Expects HTTP 500 Internal Server Error and an empty `UserResponse` in JSON format.
   * @throws Exception if the MockMvc performs an action that results in an error.
   */
  @Test
  @WithMockUser("test")
  void getUserById_serviceThrowsException_returnsInternalServerErrorAndEmptyUserResponse() throws Exception {
    when(userService.getUserById(userId)).thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new UserResponse())));

    verify(userService, times(1)).getUserById(userId);
  }

  /**
   * Verify successful update of a user with a valid request and token.
   * Expects HTTP 200 OK and the "Update successful" message.
   * @throws Exception if the MockMvc performs an action that results in an error.
   */
  @Test
  @WithMockUser("test")
  void updateUser_validRequestAndToken_returnsOkAndSuccessMessage() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(validToken)).thenReturn(extractedToken);
      doNothing().when(userService).updateUser(userId, modifyRequest, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.put("/api/user/{id}", userId)
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(modifyRequest)))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Update successful"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(userService, times(1)).updateUser(userId, modifyRequest, extractedToken);
    }
  }

  /**
   * Verify bad request during user update with an invalid user ID.
   * Expects HTTP 400 Bad Request and the "Invalid user ID" message.
   * @throws Exception if the MockMvc performs an action that results in an error.
   */
  @Test
  @WithMockUser("test")
  void updateUser_invalidId_returnsBadRequestAndErrorMessage() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(validToken)).thenReturn(extractedToken);
      doThrow(new IllegalArgumentException("Invalid user ID")).when(userService)
              .updateUser(userId, modifyRequest, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.put("/api/user/{id}", userId)
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(modifyRequest)))
              .andExpect(MockMvcResultMatchers.status().isBadRequest())
              .andExpect(MockMvcResultMatchers.content().string("Invalid user ID"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(userService, times(1)).updateUser(userId, modifyRequest, extractedToken);
    }
  }

  /**
   * Verify internal server error when the user service throws an exception during update.
   * Expects HTTP 500 Internal Server Error and the "An error occurred" message.
   * @throws Exception if the MockMvc performs an action that results in an error.
   */
  @Test
  @WithMockUser("test")
  void updateUser_serviceThrowsException_returnsInternalServerErrorAndErrorMessage() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(validToken)).thenReturn(extractedToken);
      doThrow(new RuntimeException("Update failed")).when(userService)
              .updateUser(userId, modifyRequest, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.put("/api/user/{id}", userId)
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(modifyRequest)))
              .andExpect(MockMvcResultMatchers.status().isInternalServerError())
              .andExpect(MockMvcResultMatchers.content().string("An error occurred"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(userService, times(1)).updateUser(userId, modifyRequest, extractedToken);
    }
  }
}
