package ntnu.idatt2105.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ntnu.idatt2105.project.backend.dto.request.AddCategoryRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleCategoryResponse;
import ntnu.idatt2105.project.backend.service.CategoryService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * This class is a REST controller for managing categories.
 * It provides endpoints to get all categories, add a new category,
 * and delete an existing category.
 */
@Tag(name = "Categories", description = "Endpoints for managing listing categories")
@RestController
@RequestMapping("/api/category")
public class CategoryController {
  private static final Logger logger = Logger.getLogger(CategoryController.class.getName());
  private final CategoryService categoryService;

  /**
   * Constructor for CategoryController.
   * Initializes the categoryService.
   *
   * @param categoryService The service used to manage categories.
   */
  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  /**
   * Endpoint to get all categories.
   * Handles GET requests to "/api/category"
   *
   * @return ResponseEntity containing a MultipleCategoryResponse object
   * with the list of categories and their count.
   */
  @Operation(
          summary = "Get all categories",
          description = "Retrieves a list of all available listing categories.",
          responses = {
                  @ApiResponse(responseCode = "200", description = "Successfully retrieved categories",
                          content = @Content(mediaType = "application/json", schema = @Schema(implementation = MultipleCategoryResponse.class))),
                  @ApiResponse(responseCode = "500", description = "Internal server error while fetching categories")
          }
  )
  @GetMapping
  public ResponseEntity<MultipleCategoryResponse> getAllCategories() {
    logger.info("Received request for all categories");
    try {
      MultipleCategoryResponse response = categoryService.getAllCategories();
      logger.info("Categories fetched successfully: " + response.getCategoryCount() +
              " categories found");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.severe("Error fetching categories: " + e.getMessage());
      return ResponseEntity.status(500).body(new MultipleCategoryResponse(
              null, 0
      ));
    }
  }

  /**
   * Endpoint to add a new category.
   * Handles POST requests to "/api/category"
   *
   * @param request    The request body containing the category name.
   * @param authHeader The authorization header containing the JWT token.
   * @return ResponseEntity with a message indicating success or failure.
   */
  @Operation(
          summary = "Add a new category",
          description = "Adds a new category to the list of available listing categories.",
          security = @SecurityRequirement(name = "BearerAuth"),
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Category name to add",
                  required = true,
                  content = @Content(schema = @Schema(implementation = AddCategoryRequest.class))
          ),
          parameters = {
                  @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "Bearer token for authentication")
          },
          responses = {
                  @ApiResponse(responseCode = "200", description = "Category added successfully"),
                  @ApiResponse(responseCode = "400", description = "Invalid category name"),
                  @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to add category"),
                  @ApiResponse(responseCode = "500", description = "Internal server error while adding category")
          }
  )
  @PostMapping
  public ResponseEntity<String> addCategory(@Valid @RequestBody AddCategoryRequest request,
                                            @RequestHeader("Authorization") String authHeader) {
    logger.info("Received request to add new category: " + request.getNameEn());
    try {
      categoryService.addCategory(request, TokenExtractor.extractToken(authHeader));
      logger.info("Category added successfully: " + request.getNameEn());
      return ResponseEntity.ok().body("Category added successfully");
    } catch (IllegalAccessException e) {
      logger.warning("User is not authorized to add category: " + e.getMessage());
      return ResponseEntity.status(403).body("User is not authorized to add category");
    } catch (IllegalArgumentException e) {
      logger.warning("Error adding category: " + e.getMessage());
      return ResponseEntity.badRequest().body("Error adding category: " + e.getMessage());
    } catch (Exception e) {
      logger.severe("Error adding category: " + e.getMessage());
      return ResponseEntity.status(500).body("Error adding category: " + e.getMessage());
    }
  }

  /**
   * Endpoint to delete a category.
   * Handles DELETE requests to "/api/category/{id}"
   *
   * @param id         The ID of the category to be deleted.
   * @param authHeader The authorization header containing the JWT token.
   * @return ResponseEntity with a message indicating success or failure.
   */
  @Operation(
          summary = "Delete a category by ID",
          description = "Deletes a specific category based on its ID.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "ID of the category to delete"),
                  @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "Bearer token for authentication")
          },
          responses = {
                  @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
                  @ApiResponse(responseCode = "400", description = "Invalid category ID"),
                  @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to delete category"),
                  @ApiResponse(responseCode = "500", description = "Internal server error while deleting category")
          }
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCategory(@PathVariable Long id,
                                               @RequestHeader("Authorization") String authHeader) {
    logger.info("Received request to delete category: " + id);
    try {
      categoryService.deleteCategoryById(id, TokenExtractor.extractToken(authHeader));
      logger.info("Successfully deleted category with id: " + id);
      return ResponseEntity.ok().body("Category deleted successfully");
    } catch (IllegalAccessException e) {
      logger.warning("User is not authorized to delete category: " + e.getMessage());
      return ResponseEntity.status(403).body("User is not authorized to delete category");
    } catch (IllegalArgumentException e) {
      logger.warning("Error deleting category: " + e.getMessage());
      return ResponseEntity.badRequest().body("Error deleting category: " + e.getMessage());
    } catch (Exception e) {
      logger.severe("Error deleting category: " + e.getMessage());
      return ResponseEntity.status(500).body("Error deleting category: " + e.getMessage());
    }
  }
}
