package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
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

  @PostMapping
  public ResponseEntity<String> addFavorite(
          @RequestParam Long userId,
          @RequestParam Long listingId,
          @RequestHeader("Authorization") String authHeader
  ) {
    logger.info("Received request to add favorite listing for user ID: "
            + userId + " and listing ID: " + listingId);
    try {
      favoriteService.addListingAsFavorite(userId, listingId);
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

}
