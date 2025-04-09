package ntnu.idatt2105.project.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.AddCategoryRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleCategoryResponse;
import ntnu.idatt2105.project.backend.model.Category;
import ntnu.idatt2105.project.backend.repository.CategoryRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepo categoryRepo;
  private final UserService userService;

  public MultipleCategoryResponse getAllCategories() {
    List<Category> categories = categoryRepo.getAllCategories();
    return new MultipleCategoryResponse(
            categories,
            categories.size()
    );
  }

  public void addCategory(AddCategoryRequest request, String token)
          throws IllegalAccessException {
    if (!userService.validateAdmin(token)) {
      throw new IllegalAccessException("User is not an admin");
    }
    categoryRepo.addCategory(request.getNameEn(), request.getNameNo());
  }

  public void deleteCategoryById(Long id, String token) throws IllegalAccessException {
    if (!userService.validateAdmin(token)) {
      throw new IllegalAccessException("User is not an admin");
    }
    categoryRepo.deleteCategoryById(id);
  }
}
