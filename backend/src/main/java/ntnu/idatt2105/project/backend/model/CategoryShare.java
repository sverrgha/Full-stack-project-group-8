package ntnu.idatt2105.project.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CategoryShare class represents a share of a category.
 * It contains category ID and view count and percentage.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryShare {
  private Long categoryId;
  private int viewCount;
  private Double viewPercentage;
}
