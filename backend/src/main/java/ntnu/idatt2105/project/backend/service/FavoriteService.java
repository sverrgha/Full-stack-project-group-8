package ntnu.idatt2105.project.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.model.Listing;
import ntnu.idatt2105.project.backend.repository.ListingRepo;
import ntnu.idatt2105.project.backend.repository.UserFavoritesRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {
  private final UserFavoritesRepo userFavoritesRepo;
    private final ListingService listingService;
    private final UserService userService;

  public void addListingAsFavorite(long userId, long listingId) {
    if (userId <= 0 || listingId <= 0) {
      throw new IllegalArgumentException("User ID and Listing ID must be positive");
    }
    if(!listingService.listingExists(listingId)) {
      throw new IllegalArgumentException("Listing not found");
    }
    if (!userService.userExists(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    userFavoritesRepo.save(userId, listingId);
  }
}
