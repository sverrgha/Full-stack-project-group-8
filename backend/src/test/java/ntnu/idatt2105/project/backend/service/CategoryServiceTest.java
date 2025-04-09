package ntnu.idatt2105.project.backend.service;

import ntnu.idatt2105.project.backend.dto.request.AddCategoryRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleCategoryResponse;
import ntnu.idatt2105.project.backend.model.Category;
import ntnu.idatt2105.project.backend.repository.CategoryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
  @Mock
  private CategoryRepo categoryRepo;
  @Mock
  private UserService userService;

  @InjectMocks
  private CategoryService categoryService;

  @Test
  void getAllCategories_returnsCorrectResponse() {
    List<Category> categories = Arrays.asList(
            new Category(1L, "Category 1", "Kategori 1"),
            new Category(2L, "Category 2", "Kategori 2")
    );
    when(categoryRepo.getAllCategories()).thenReturn(categories);

    MultipleCategoryResponse response = categoryService.getAllCategories();

    assertEquals(categories, response.getCategories());
    assertEquals(categories.size(), response.getCategoryCount());
    verify(categoryRepo, times(1)).getAllCategories();
  }

  @Test
  void addCategory_adminUser_categoryAddedSuccessfully() throws IllegalAccessException {
    AddCategoryRequest request = new AddCategoryRequest("New Category",
            "Ny Kategori");
    String token = "adminToken";
    when(userService.validateAdmin(token)).thenReturn(true);

    categoryService.addCategory(request, token);

    verify(userService, times(1)).validateAdmin(token);
    verify(categoryRepo, times(1)).addCategory(request.getNameEn(), request.getNameNo());
  }

  @Test
  void addCategory_nonAdminUser_throwsAccessException() {
    AddCategoryRequest request = new AddCategoryRequest("New Category",
            "Ny Kategori");
    String token = "userToken";
    when(userService.validateAdmin(token)).thenReturn(false);

    assertThrows(IllegalAccessException.class, () -> categoryService.addCategory(request, token));
    verify(userService, times(1)).validateAdmin(token);
    verify(categoryRepo, never()).addCategory(anyString(), anyString());
  }

  @Test
  void deleteCategoryById_adminUser_categoryDeletedSuccessfully() throws IllegalAccessException {
    Long categoryId = 10L;
    String token = "adminToken";
    when(userService.validateAdmin(token)).thenReturn(true);

    categoryService.deleteCategoryById(categoryId, token);

    verify(userService, times(1)).validateAdmin(token);
    verify(categoryRepo, times(1)).deleteCategoryById(categoryId);
  }

  @Test
  void deleteCategoryById_nonAdminUser_throwsAccessException() {
    Long categoryId = 10L;
    String token = "userToken";
    when(userService.validateAdmin(token)).thenReturn(false);

    assertThrows(IllegalAccessException.class, () -> categoryService.deleteCategoryById(categoryId, token));
    verify(userService, times(1)).validateAdmin(token);
    verify(categoryRepo, never()).deleteCategoryById(anyLong());
  }
}
