package ntnu.idatt2105.project.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.repository.UserFavoritesRepo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {
  private final UserFavoritesRepo userFavoritesRepo;
    private final ListingService listingService;
    private final UserService userService;

  public void addListingAsFavorite(long userId, long listingId, String token) {
    if (userId <= 0 || listingId <= 0) {
      throw new IllegalArgumentException("User ID and Listing ID must be positive");
    }
    if(!listingService.listingExists(listingId)) {
      throw new IllegalArgumentException("Listing not found");
    }
    if (!userService.userExists(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    if (!userService.validateUserIdMatchesToken(userId, token)) {
      throw new IllegalArgumentException("User ID does not match token");
    }

    userFavoritesRepo.save(userId, listingId);
  }

  public void removeListingAsFavorite(long userId, long listingId, String token) {
    if (userId <= 0 || listingId <= 0) {
      throw new IllegalArgumentException("User ID and Listing ID must be positive");
    }
    if (!userService.validateUserIdMatchesToken(userId, token)) {
      throw new IllegalArgumentException("User ID does not match token");
    }

    userFavoritesRepo.deleteByUserIdAndListingId(userId, listingId);
  }
}
