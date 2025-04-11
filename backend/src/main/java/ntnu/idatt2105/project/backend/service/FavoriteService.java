package ntnu.idatt2105.project.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.FavoriteRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.repository.UserFavoritesRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing user favorites.
 * This class provides methods to add, remove, and fetch user favorites.
 */
@Service
@RequiredArgsConstructor
public class FavoriteService {
  private final UserFavoritesRepo userFavoritesRepo;
  private final ListingService listingService;
  private final UserService userService;

  /**
   * Adds a listing to the user's favorites.
   * This method checks if the user ID and listing ID are valid,
   * validates the token, and checks if the listing is already a favorite.
   *
   * @param request the request containing user ID and listing ID
   * @param token   the token for authentication
   */
  public void addListingAsFavorite(FavoriteRequest request, String token) {
    long userId = request.getUserId();
    long listingId = request.getListingId();
    if (userId <= 0 || listingId <= 0) {
      throw new IllegalArgumentException("User ID and Listing ID must be positive");
    }
    if (!listingService.listingExists(listingId)) {
      throw new IllegalArgumentException("Listing not found");
    }
    if (!userService.userExists(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    if (!userService.validateUserIdMatchesToken(userId, token)) {
      throw new IllegalArgumentException("User ID does not match token");
    }

    if (userFavoritesRepo.existsByUserIdAndListingId(userId, listingId)) {
      throw new IllegalArgumentException("Listing is already a favorite");
    }

    userFavoritesRepo.save(userId, listingId);
  }

  /**
   * Removes a listing from the user's favorites.
   * This method checks if the user ID and listing ID are valid,
   * validates the token, and checks if the listing is already a favorite.
   *
   * @param request the request containing user ID and listing ID
   * @param token   the token for authentication
   */
  public void removeListingAsFavorite(FavoriteRequest request, String token) {
    long userId = request.getUserId();
    long listingId = request.getListingId();
    if (userId <= 0 || listingId <= 0) {
      throw new IllegalArgumentException("User ID and Listing ID must be positive");
    }
    if (!userService.validateUserIdMatchesToken(userId, token)) {
      throw new IllegalArgumentException("User ID does not match token");
    }

    if (!userFavoritesRepo.existsByUserIdAndListingId(userId, listingId)) {
      throw new IllegalArgumentException("Listing is not a favorite");
    }

    userFavoritesRepo.deleteByUserIdAndListingId(userId, listingId);
  }

  /**
   * Retrieves all favorite listings for a user.
   * This method checks if the user ID is valid,
   * validates the token, and retrieves the favorite listings.
   *
   * @param userId  the ID of the user
   * @param pageable the pagination information
   * @param token    the token for authentication
   * @return a response containing the favorite listings
   */
  public MultipleListingsResponse getAllFavorites(long userId, Pageable pageable, String token) {
    pageable = PageRequest.of(Math.max(pageable.getPageNumber() - 1, 0), pageable.getPageSize(),
            pageable.getSort());
    if (userId <= 0) {
      throw new IllegalArgumentException("User ID must be positive");
    }
    if (!userService.validateUserIdMatchesToken(userId, token)) {
      throw new IllegalArgumentException("User ID does not match token");
    }

    List<Long> listingIds = userFavoritesRepo.getFavoriteIdsByUserId(userId);

    return listingService.getMultipleListingsById(listingIds, pageable);
  }
}
