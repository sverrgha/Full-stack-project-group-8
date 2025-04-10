package ntnu.idatt2105.project.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddCategoryRequest {
  @NotBlank(message = "Category name in English cannot be blank")
  private String nameEn;
  @NotBlank(message = "Category name in Norwegian cannot be blank")
  private String nameNo;
  @NotBlank(message = "Category URL cannot be blank")
  private String url;
}
