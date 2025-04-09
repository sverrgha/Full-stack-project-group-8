package ntnu.idatt2105.project.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCategoryRequest {
  @NotBlank(message = "Category name in English cannot be blank")
  private String nameEn;
  @NotBlank(message = "Category name in Norwegian cannot be blank")
  private String nameNo;
}
