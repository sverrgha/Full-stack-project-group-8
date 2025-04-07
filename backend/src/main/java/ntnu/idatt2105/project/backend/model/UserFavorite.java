package ntnu.idatt2105.project.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a user's favorite listing.
 * It contains the user ID and the listing ID.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFavorite {
  /**
   * The ID of the user who favorited the listing.
   */
  private Long userId;

  /**
   * The ID of the listing that was favorited.
   */
  private Long listingId;
}
