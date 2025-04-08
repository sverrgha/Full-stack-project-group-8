package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.service.FavoriteService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
