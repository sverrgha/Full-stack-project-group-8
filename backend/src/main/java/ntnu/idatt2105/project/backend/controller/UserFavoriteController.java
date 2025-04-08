package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.service.FavoriteService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user favorites.
 * This controller provides endpoints to add, remove, and fetch user favorites.
 */
@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {
  private final FavoriteService favoriteService;

  private static final java.util.logging.Logger logger =
          java.util.logging.Logger.getLogger(UserFavoriteController.class.getName());

  @Autowired
  public UserFavoriteController(FavoriteService favoriteService) {
    this.favoriteService = favoriteService;
  }

  /**
   * Endpoint to fetch all favorite listings for a user.
   * This method takes a user ID and pagination parameters to limit the number of
   * listings returned, and dividing them into pages.
   *
   * @param userId the ID of the user whose favorites are to be fetched
   * @param authHeader the authorization header containing the token
   * @param pageable pagination parameters
   * @return ResponseEntity with MultipleListingsResponse containing the favorite listings
   */
  @GetMapping
  public ResponseEntity<MultipleListingsResponse> getFavorites(
          @RequestParam Long userId,
          @RequestHeader("Authorization") String authHeader,
          @PageableDefault(size = 20, page = 1, sort = "created_at",
                  direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable
  ) {
    logger.info("Received request to get favorite listings for user ID: " + userId);

    try {
      MultipleListingsResponse response = favoriteService.getAllFavorites(userId, pageable,
              TokenExtractor.extractToken(authHeader));
      logger.info("Favorite listings fetched successfully for user ID: " + userId);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid favorite request: " + e.getMessage());
      return ResponseEntity.badRequest().body(new MultipleListingsResponse());
    } catch (Exception e) {
      logger.severe("Error while fetching favorite listings: " + e.getMessage());
      return ResponseEntity.internalServerError()
              .body(new MultipleListingsResponse());
    }
  }

  /**
   * Endpoint to add a listing to the user's favorites.
   * This method takes a user ID and listing ID to add the listing as a favorite.
   * It also requires an authorization header containing the token, to verify the
   * user's identity.
   * @param userId the ID of the user adding the favorite
   * @param listingId the ID of the listing to be added as a favorite
   * @param authHeader the authorization header containing the token
   * @return ResponseEntity with a message indicating success or failure
   */
  @PostMapping
  public ResponseEntity<String> addFavorite(
          @RequestParam Long userId,
          @RequestParam Long listingId,
          @RequestHeader("Authorization") String authHeader
  ) {
    logger.info("Received request to add favorite listing for user ID: "
            + userId + " and listing ID: " + listingId);
    try {
      favoriteService.addListingAsFavorite(userId, listingId,
              TokenExtractor.extractToken(authHeader));
      logger.info("Favorite listing added successfully for user ID: " + userId);
      return ResponseEntity.ok().body("Favorite listing added successfully");
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid favorite request: " + e.getMessage());
      return ResponseEntity.badRequest().body("Invalid favorite request: " + e.getMessage());
    } catch (Exception e) {
      logger.severe("Error while adding favorite listing: " + e.getMessage());
      return ResponseEntity.internalServerError()
              .body("Error while adding favorite listing: " + e.getMessage());
    }
  }

  /**
   * Endpoint to remove a listing from the user's favorites.
   * This method takes a user ID and listing ID to remove the listing from the favorites.
   * It also requires an authorization header containing the token, to verify the
   * user's identity.
   * @param userId the ID of the user removing the favorite
   * @param listingId the ID of the listing to be removed from favorites
   * @param authHeader the authorization header containing the token
   * @return ResponseEntity with a message indicating success or failure
   */
  @DeleteMapping
  public ResponseEntity<String> removeFavorite(
          @RequestParam Long userId,
          @RequestParam Long listingId,
          @RequestHeader("Authorization") String authHeader
  ) {
    logger.info("Received request to remove favorite listing for user ID: "
            + userId + " and listing ID: " + listingId);
    try {
      favoriteService.removeListingAsFavorite(userId, listingId,
              TokenExtractor.extractToken(authHeader));
      logger.info("Favorite listing removed successfully for user ID: " + userId);
      return ResponseEntity.ok().body("Favorite listing removed successfully");
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid favorite request: " + e.getMessage());
      return ResponseEntity.badRequest().body("Invalid favorite request: " + e.getMessage());
    } catch (Exception e) {
      logger.severe("Error while removing favorite listing: " + e.getMessage());
      return ResponseEntity.internalServerError()
              .body("Error while removing favorite listing: " + e.getMessage());
    }
  }
}
