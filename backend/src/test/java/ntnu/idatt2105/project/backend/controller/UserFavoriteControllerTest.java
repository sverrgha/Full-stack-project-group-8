package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.service.FavoriteService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

  @Test
  @WithMockUser("test")
  void addFavorite_validRequest_returnsOk() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doNothing().when(favoriteService).addListingAsFavorite(userId, listingId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.post("/api/favorites")
                      .param("userId", String.valueOf(userId))
                      .param("listingId", String.valueOf(listingId))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Favorite listing added successfully"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).addListingAsFavorite(userId, listingId, extractedToken);
    }
  }


  @Test
  @WithMockUser("test")
  void addFavorite_invalidInput_returnsBadRequest() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doNothing().when(favoriteService).removeListingAsFavorite(userId, listingId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites")
                      .param("userId", String.valueOf(userId))
                      .param("listingId", String.valueOf(listingId))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Favorite listing removed successfully"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).removeListingAsFavorite(userId, listingId, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void addFavorite_internalServerError_returnsInternalServerError() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doThrow(new RuntimeException("Database error")).when(favoriteService)
              .addListingAsFavorite(userId, listingId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.post("/api/favorites")
                      .param("userId", String.valueOf(userId))
                      .param("listingId", String.valueOf(listingId))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isInternalServerError())
              .andExpect(MockMvcResultMatchers.content().string("Error while adding favorite listing: Database error"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).addListingAsFavorite(userId, listingId, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void removeFavorite_validRequest_returnsOk() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doNothing().when(favoriteService).removeListingAsFavorite(userId, listingId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites")
                      .param("userId", String.valueOf(userId))
                      .param("listingId", String.valueOf(listingId))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Favorite listing removed successfully"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).removeListingAsFavorite(userId, listingId, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void removeFavorite_invalidInput_returnsBadRequest() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doThrow(new IllegalArgumentException("Invalid input")).when(favoriteService)
              .removeListingAsFavorite(userId, listingId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites")
                      .param("userId", String.valueOf(userId))
                      .param("listingId", String.valueOf(listingId))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isBadRequest())
              .andExpect(MockMvcResultMatchers.content().string("Invalid favorite request: Invalid input"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).removeListingAsFavorite(userId, listingId, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void removeFavorite_internalServerError_returnsInternalServerError() throws Exception {
    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(anyString())).thenReturn(extractedToken);
      doThrow(new RuntimeException("Database error")).when(favoriteService)
              .removeListingAsFavorite(userId, listingId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites")
                      .param("userId", String.valueOf(userId))
                      .param("listingId", String.valueOf(listingId))
                      .header("Authorization", validToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isInternalServerError())
              .andExpect(MockMvcResultMatchers.content().string("Error while removing favorite listing: Database error"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(validToken));
      verify(favoriteService, times(1)).removeListingAsFavorite(userId, listingId, extractedToken);
    }
  }
}