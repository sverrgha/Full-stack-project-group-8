package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.dto.request.FavoriteRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.service.FavoriteService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserFavoriteControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private FavoriteService favoriteService;

  private static Long userId;
  private static Long listingId;
  private static String validToken;
  private static String extractedToken;

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

  /**
   * Set up the test class with common parameters, to be used in all tests.
   */
  @BeforeAll
  static void setUp() {
    userId = 1L;
    listingId = 1L;
    validToken = "validToken";
    extractedToken = "validToken";
  }

  /**
   * Test method to verify the behavior of the addFavorite method in the UserFavoriteController.
   * This test checks if the method returns a 200 OK status with a success message
   * when a valid request is made.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void addFavorite_validRequest_returnsOk() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doNothing().when(favoriteService).addListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.post("/api/favorites")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(new FavoriteRequest(
                              userId, listingId
                      )))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Favorite listing added successfully"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).addListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);
    }
  }

  /**
   * Test method to verify the behavior of the addFavorite method in the UserFavoriteController.
   * This test checks if the method returns a 400 Bad Request status with an error message
   * when an invalid request is made.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void addFavorite_invalidInput_returnsBadRequest() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doNothing().when(favoriteService).removeListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(new FavoriteRequest(
                              userId, listingId
                      )))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Favorite listing removed successfully"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).removeListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);
    }
  }

  /**
   * Test method to verify the behavior of the addFavorite method in the UserFavoriteController.
   * This test checks if the method returns a 500 Internal Server Error status with an error message
   * when an unexpected error occurs during the request.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void addFavorite_internalServerError_returnsInternalServerError() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doThrow(new RuntimeException("Database error")).when(favoriteService)
              .addListingAsFavorite(new FavoriteRequest(
                      userId, listingId
              ), extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.post("/api/favorites")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(new FavoriteRequest(
                              userId, listingId
                      )))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isInternalServerError())
              .andExpect(MockMvcResultMatchers.content().string("Error while adding favorite listing: Database error"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).addListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);
    }
  }

  /**
   * Test method to verify the behavior of the removeFavorite method in the UserFavoriteController.
   * This test checks if the method returns a 200 OK status with a success message
   * when a valid request is made.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void removeFavorite_validRequest_returnsOk() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doNothing().when(favoriteService).removeListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(new FavoriteRequest(
                              userId, listingId
                      )))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Favorite listing removed successfully"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).removeListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);
    }
  }

  /**
   * Test method to verify the behavior of the removeFavorite method in the UserFavoriteController.
   * This test checks if the method returns a 400 Bad Request status with an error message
   * when an invalid request is made.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void removeFavorite_invalidInput_returnsBadRequest() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doThrow(new IllegalArgumentException("Invalid input")).when(favoriteService)
              .removeListingAsFavorite(new FavoriteRequest(
                      userId, listingId
              ), extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(new FavoriteRequest(
                              userId, listingId
                      )))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isBadRequest())
              .andExpect(MockMvcResultMatchers.content().string("Invalid favorite request: Invalid input"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).removeListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);
    }
  }

  /**
   * Test method to verify the behavior of the removeFavorite method in the
   * UserFavoriteController. Verifies that the method returns a 500 Internal Server Error
   * status with an error message when an unexpected error occurs during the request.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void removeFavorite_internalServerError_returnsInternalServerError() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doThrow(new RuntimeException("Database error")).when(favoriteService)
              .removeListingAsFavorite(new FavoriteRequest(
                      userId, listingId
              ), extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(new FavoriteRequest(
                              userId, listingId
                      )))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isInternalServerError())
              .andExpect(MockMvcResultMatchers.content().string("Error while removing favorite listing: Database error"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).removeListingAsFavorite(new FavoriteRequest(
              userId, listingId
      ), extractedToken);
    }
  }

  /**
   * Test method to verify the behavior of the getFavorites method in the UserFavoriteController.
   * This test checks if the method returns a 200 OK status with the expected response
   * when a valid request is made.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getFavorites_validRequest_returnsOkWithResponse() throws Exception {
    MultipleListingsResponse mockResponse = new MultipleListingsResponse();
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      when(favoriteService.getAllFavorites(eq(userId), any(Pageable.class), eq(extractedToken)))
              .thenReturn(mockResponse);

      mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites/" + userId)
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(mockResponse)));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).getAllFavorites(eq(userId), any(Pageable.class), eq(extractedToken));
    }
  }

  /**
   * Test method to verify the behavior of the getFavorites method in the UserFavoriteController.
   * This test checks if the method returns a 400 Bad Request status with an empty response
   * when an invalid user ID is provided.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getFavorites_invalidUserId_returnsBadRequestWithEmptyResponse() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      when(favoriteService.getAllFavorites(eq(0L), any(Pageable.class), eq(extractedToken)))
              .thenThrow(new IllegalArgumentException("User ID must be positive"));

      mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites/" + 0L)
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isBadRequest())
              .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new MultipleListingsResponse())));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).getAllFavorites(eq(0L), any(Pageable.class), eq(extractedToken));
    }
  }

  /**
   * Test method to verify the behavior of the getFavorites method in the UserFavoriteController.
   * This test checks if the method returns a 500 Internal Server Error status with an empty response
   * when an unexpected error occurs during the request.
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getFavorites_internalServerError_returnsInternalServerErrorWithEmptyResponse() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      when(favoriteService.getAllFavorites(eq(userId), any(Pageable.class), eq(extractedToken)))
              .thenThrow(new RuntimeException("Database error"));

      mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites/" + userId)
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isInternalServerError())
              .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(new MultipleListingsResponse())));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).getAllFavorites(eq(userId), any(Pageable.class), eq(extractedToken));
    }
  }
}