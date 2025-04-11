package ntnu.idatt2105.project.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO class for favorite request.
 * This class is used to transfer data for adding or removing a favorite listing.
 */
@Data
@AllArgsConstructor
public class FavoriteRequest {
  private Long userId;
  private Long listingId;
}
