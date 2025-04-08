package ntnu.idatt2105.project.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO class for filtering listings based on various criteria.
 * It contains fields for category ID, city, price range, conditions,
 * sorting options, and pagination, which are used to filter the listings
 * all of which are optional.
 */
@Data
@NoArgsConstructor
public class ListingFilterRequest {
  private Long categoryId;
  private String city;

  private Double minPrice;

  private Double maxPrice;

  private List<String> conditions;
  private String sortBy;
  private String sortOrder;
}
