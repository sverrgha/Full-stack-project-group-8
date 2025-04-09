package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntnu.idatt2105.project.backend.model.Category;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleCategoryResponse {
  private List<Category> categories;
  private int categoryCount;
}
