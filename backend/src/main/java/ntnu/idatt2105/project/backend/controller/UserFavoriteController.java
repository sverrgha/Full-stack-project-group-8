package ntnu.idatt2105.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ntnu.idatt2105.project.backend.dto.request.FavoriteRequest;
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
@Tag(name = "User Favorites", description = "Endpoints for managing user's favorite listings")
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
   * @param userId     the ID of the user whose favorites are to be fetched
   * @param authHeader the authorization header containing the token
   * @param pageable   pagination parameters
   * @return ResponseEntity with MultipleListingsResponse containing the favorite listings
   */
  @Operation(
          summary = "Get user's favorite listings",
          description = "Retrieves a paginated list of listings that a user has marked as favorites.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "userId", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, required = true, description = "ID of the user"),
                  @Parameter(name = "Authorization", in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER, required = true, description = "Bearer token for authentication"),
                  @Parameter(name = "size", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, description = "Number of items per page (default: 20)"),
                  @Parameter(name = "page", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, description = "Page number (default: 1)"),
                  @Parameter(name = "sort", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, description = "Sorting criteria (e.g., 'created_at')"),
                  @Parameter(name = "direction", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, description = "Sorting direction (e.g., 'asc' or 'desc')")
          },
          responses = {
                  @ApiResponse(responseCode = "200", description = "Successfully retrieved favorite listings",
                          content = @Content(mediaType = "application/json", schema = @Schema(implementation = MultipleListingsResponse.class))),
                  @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                  @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                  @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/{userId}")
  public ResponseEntity<MultipleListingsResponse> getFavorites(
          @PathVariable Long userId,
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
   *
   * @param request    the request body containing user ID and listing ID
   * @param authHeader the authorization header containing the token
   * @return ResponseEntity with a message indicating success or failure
   */
  @Operation(
          summary = "Add a listing to user's favorites",
          description = "Adds a specific listing to a user's list of favorite listings.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "userId", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, required = true, description = "ID of the user"),
                  @Parameter(name = "listingId", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, required = true, description = "ID of the listing to add"),
                  @Parameter(name = "Authorization", in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER, required = true, description = "Bearer token for authentication")
          },
          responses = {
                  @ApiResponse(responseCode = "200", description = "Successfully added listing to favorites"),
                  @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                  @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                  @ApiResponse(responseCode = "500", description = "Internal server error")
          })
  @PostMapping
  public ResponseEntity<String> addFavorite(
          @RequestBody FavoriteRequest request,
          @RequestHeader("Authorization") String authHeader
  ) {
    logger.info("Received favorite request: " + request);
    try {
      favoriteService.addListingAsFavorite(request,
              TokenExtractor.extractToken(authHeader));
      logger.info("Favorite listing added successfully for user ID: " + request.getUserId());
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
   *
   * @param request    the request body containing user ID and listing ID
   * @param authHeader the authorization header containing the token
   * @return ResponseEntity with a message indicating success or failure
   */
  @Operation(
          summary = "Remove a listing from user's favorites",
          description = "Removes a specific listing from a user's list of favorite listings.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "userId", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, required = true, description = "ID of the user"),
                  @Parameter(name = "listingId", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, required = true, description = "ID of the listing to remove"),
                  @Parameter(name = "Authorization", in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER, required = true, description = "Bearer token for authentication")
          },
          responses = {
                  @ApiResponse(responseCode = "200", description = "Successfully removed listing from favorites"),
                  @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                  @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                  @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @DeleteMapping
  public ResponseEntity<String> removeFavorite(
          @RequestBody FavoriteRequest request,
          @RequestHeader("Authorization") String authHeader
  ) {
    logger.info("Received request to remove favorite listing: " + request);
    try {
      favoriteService.removeListingAsFavorite(request,
              TokenExtractor.extractToken(authHeader));
      logger.info("Favorite listing removed successfully for user ID: " + request.getUserId());
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
