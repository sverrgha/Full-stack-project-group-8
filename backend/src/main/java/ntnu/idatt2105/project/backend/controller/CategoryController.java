package ntnu.idatt2105.project.backend.controller;

import jakarta.validation.Valid;
import ntnu.idatt2105.project.backend.dto.request.AddCategoryRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleCategoryResponse;
import ntnu.idatt2105.project.backend.service.CategoryService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
  private static final Logger logger = Logger.getLogger(CategoryController.class.getName());
  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

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
