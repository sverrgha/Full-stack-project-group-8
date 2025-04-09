package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.dto.request.AddCategoryRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleCategoryResponse;
import ntnu.idatt2105.project.backend.model.Category;
import ntnu.idatt2105.project.backend.service.CategoryService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CategoryService categoryService;

  private static String adminToken;
  private static String userToken;

  @BeforeAll
  static void setUp() {
    adminToken = "Bearer adminToken";
    userToken = "Bearer userToken";
  }

  @Test
  @WithMockUser("test")
  void getAllCategories_returnsOkWithCategories() throws Exception {
    MultipleCategoryResponse mockResponse = new MultipleCategoryResponse(
            List.of(
                    new Category(1L, "English 1", "Norwegian 1")
            ),
            1
    );
    when(categoryService.getAllCategories()).thenReturn(mockResponse);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/category"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(mockResponse)));

    verify(categoryService, times(1)).getAllCategories();
  }

  @Test
  @WithMockUser("test")
  void getAllCategories_returnsOkWithEmptyCategories() throws Exception {
    MultipleCategoryResponse mockResponse = new MultipleCategoryResponse(Collections.emptyList(), 0);
    when(categoryService.getAllCategories()).thenReturn(mockResponse);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/category"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(mockResponse)));

    verify(categoryService, times(1)).getAllCategories();
  }

  @Test
  @WithMockUser("test")
  void getAllCategories_returnsInternalServerErrorOnError() throws Exception {
    when(categoryService.getAllCategories()).thenThrow(new RuntimeException("Database error"));
    MultipleCategoryResponse errorResponse = new MultipleCategoryResponse(null, 0);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/category"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(errorResponse)));

    verify(categoryService, times(1)).getAllCategories();
  }

  @Test
  @WithMockUser("test")
  void addCategory_adminUser_returnsOk() throws Exception {

    AddCategoryRequest request = new AddCategoryRequest("New Category En", "Ny Kategori No");
    String extractedToken = "adminToken";

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(adminToken)).thenReturn(extractedToken);
      doNothing().when(categoryService).addCategory(request, extractedToken);


      mockMvc.perform(MockMvcRequestBuilders.post("/api/category")
                      .header("Authorization", adminToken)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Category added successfully"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(adminToken), times(1));
      verify(categoryService, times(1)).addCategory(request, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void addCategory_nonAdminUser_returnsForbidden() throws Exception {
    AddCategoryRequest request = new AddCategoryRequest("New Category En", "Ny Kategori No");
    String extractedToken = "userToken";

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(userToken)).thenReturn(extractedToken);
      doThrow(new IllegalAccessException("User is not an admin")).when(categoryService).addCategory(request, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.post("/api/category")
                      .header("Authorization", userToken)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(MockMvcResultMatchers.status().isForbidden())
              .andExpect(MockMvcResultMatchers.content().string("User is not authorized to add category"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(userToken), times(1));
      verify(categoryService, times(1)).addCategory(request, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void addCategory_invalidInput_returnsBadRequest() throws Exception {
    AddCategoryRequest invalidRequest = new AddCategoryRequest(null, "Ny Kategori No");
    String extractedToken = "adminToken";

    doThrow(new IllegalArgumentException("Category nameEn cannot be null")).when(categoryService).addCategory(invalidRequest, extractedToken);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/category")
                    .header("Authorization", adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

    verify(categoryService, never()).addCategory(invalidRequest, extractedToken);
  }

  @Test
  @WithMockUser("test")
  void addCategory_internalServerError_returnsInternalServerError() throws Exception {
    AddCategoryRequest request = new AddCategoryRequest("New Category En", "Ny Kategori No");
    String extractedToken = "adminToken";

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(adminToken)).thenReturn(extractedToken);
      doThrow(new RuntimeException("Database error")).when(categoryService).addCategory(request, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.post("/api/category")
                      .header("Authorization", adminToken)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(MockMvcResultMatchers.status().isInternalServerError())
              .andExpect(MockMvcResultMatchers.content().string("Error adding category: Database error"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(adminToken), times(1));
      verify(categoryService, times(1)).addCategory(request, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void deleteCategory_adminUser_returnsOk() throws Exception {
    Long categoryId = 5L;
    String extractedToken = "adminToken";

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(adminToken)).thenReturn(extractedToken);
      doNothing().when(categoryService).deleteCategoryById(categoryId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/category/" + categoryId)
                      .header("Authorization", adminToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Category deleted successfully"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(adminToken), times(1));
      verify(categoryService, times(1)).deleteCategoryById(categoryId, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void deleteCategory_nonAdminUser_returnsForbidden() throws Exception {
    Long categoryId = 5L;
    String extractedToken = "userToken";

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(userToken)).thenReturn(extractedToken);
      doThrow(new IllegalAccessException("User is not an admin")).when(categoryService).deleteCategoryById(categoryId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/category/" + categoryId)
                      .header("Authorization", userToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isForbidden())
              .andExpect(MockMvcResultMatchers.content().string("User is not authorized to delete category"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(userToken), times(1));
      verify(categoryService, times(1)).deleteCategoryById(categoryId, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void deleteCategory_invalidCategoryId_returnsBadRequest() throws Exception {
    Long invalidCategoryId = -1L;
    String extractedToken = "adminToken";

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(adminToken)).thenReturn(extractedToken);
      doThrow(new IllegalArgumentException("Invalid category ID")).when(categoryService).deleteCategoryById(invalidCategoryId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/category/" + invalidCategoryId)
                      .header("Authorization", adminToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isBadRequest())
              .andExpect(MockMvcResultMatchers.content().string("Error deleting category: Invalid category ID"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(adminToken), times(1));
      verify(categoryService, times(1)).deleteCategoryById(invalidCategoryId, extractedToken);
    }
  }

  @Test
  @WithMockUser("test")
  void deleteCategory_internalServerError_returnsInternalServerError() throws Exception {
    Long categoryId = 5L;
    String extractedToken = "adminToken";

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(adminToken)).thenReturn(extractedToken);
      doThrow(new RuntimeException("Database error")).when(categoryService).deleteCategoryById(categoryId, extractedToken);

      mockMvc.perform(MockMvcRequestBuilders.delete("/api/category/" + categoryId)
                      .header("Authorization", adminToken)
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.status().isInternalServerError())
              .andExpect(MockMvcResultMatchers.content().string("Error deleting category: Database error"));

      mockedTokenExtractor.verify(() -> TokenExtractor.extractToken(adminToken), times(1));
      verify(categoryService, times(1)).deleteCategoryById(categoryId, extractedToken);
    }
  }
}
