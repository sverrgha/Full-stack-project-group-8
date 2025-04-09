package ntnu.idatt2105.project.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.AddCategoryRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleCategoryResponse;
import ntnu.idatt2105.project.backend.model.Category;
import ntnu.idatt2105.project.backend.repository.CategoryRepo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class is a service for managing categories in the database.
 * It provides methods to get, add, and delete categories.
 * It uses CategoryRepo to interact with the database.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepo categoryRepo;
  private final UserService userService;

  /**
   * This method retrieves all categories from the database using the
   * CategoryRepo.
   *
   * @return A MultipleCategoryResponse object containing a list of categories
   */
  public MultipleCategoryResponse getAllCategories() {
    List<Category> categories = categoryRepo.getAllCategories();
    return new MultipleCategoryResponse(
            categories,
            categories.size()
    );
  }

  /**
   * This method adds a new category to the database.
   * It verifies that the user is an admin before adding the category.
   *
   * @param request The AddCategoryRequest object containing the category information.
   * @param token   The JWT token of the user making the request.
   * @throws IllegalAccessException if the user is not an admin.
   */
  public void addCategory(AddCategoryRequest request, String token)
          throws IllegalAccessException {
    if (!userService.validateAdmin(token)) {
      throw new IllegalAccessException("User is not an admin");
    }
    categoryRepo.addCategory(request.getNameEn(), request.getNameNo());
  }

  /**
   * This method retrieves a category by its ID by using the CategoryRepo.
   * It verifies that the user is an admin before retrieving the category.
   *
   * @param id The ID of the category to retrieve.
   */
  public void deleteCategoryById(Long id, String token) throws IllegalAccessException {
    if (!userService.validateAdmin(token)) {
      throw new IllegalAccessException("User is not an admin");
    }
    categoryRepo.deleteCategoryById(id);
  }
}
