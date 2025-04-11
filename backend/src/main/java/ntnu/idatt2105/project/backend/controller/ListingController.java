package ntnu.idatt2105.project.backend.controller;

import jakarta.validation.Valid;
import ntnu.idatt2105.project.backend.dto.request.AddListingRequest;
import ntnu.idatt2105.project.backend.dto.request.ListingFilterRequest;
import ntnu.idatt2105.project.backend.dto.request.ListingStatusRequest;
import ntnu.idatt2105.project.backend.dto.request.ModifyListingRequest;
import ntnu.idatt2105.project.backend.dto.response.AddListingResponse;
import ntnu.idatt2105.project.backend.dto.response.FullListingResponse;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ntnu.idatt2105.project.backend.service.ListingService;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * ListingController handles requests related to listings.
 * It provides endpoints for adding, fetching, and filtering listings.
 */

@RestController
@RequestMapping("/api/listing")
public class ListingController {
  private final ListingService listingService;
  private static final Logger logger = Logger.getLogger(ListingController.class.getName());

  /**
   * Constructor for ListingController. Sets the listingService.
   *
   * @param listingService
   */
  @Autowired
  public ListingController(ListingService listingService) {
    this.listingService = listingService;
  }

  /**
   * Endpoint for fetching listings based on filter criteria. If none are provided,
   * it returns all listings.
   * It also takes pagination parameters to limit the number of listings returned, and
   * dividing them into pages.
   *
   * @param filterRequest the filter criteria for fetching listings
   * @param pageable      pagination parameters
   * @return ResponseEntity with MultipleListingsResponse containing simple
   */
  @GetMapping
  public ResponseEntity<MultipleListingsResponse> getListings(
          @Valid ListingFilterRequest filterRequest,
          @PageableDefault(size = 20, page = 1, sort = "created_at",
                  direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable
  ) {
    logger.info("Received request for listings with filter: " + filterRequest);
    try {
      MultipleListingsResponse response = listingService
              .getListingByFilter(filterRequest, pageable);
      logger.info("Listings fetched successfully " + response.getElements().size() + " listings found");
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid filter request: " + e.getMessage());
      return ResponseEntity.badRequest().body(new MultipleListingsResponse(
              Collections.emptyList(), 0, 0,
              pageable.getPageNumber(), pageable.getPageSize(), true, true
      ));
    } catch (Exception e) {
      logger.severe("Error while fetching listings: " + e.getMessage());
      return ResponseEntity.internalServerError().body(new MultipleListingsResponse(
              Collections.emptyList(), 0, 0,
              pageable.getPageNumber(), pageable.getPageSize(), true, true
      ));
    }
  }

  /**
   * Endpoint for adding a new listing.
   *
   * @param listing the listing to be added, with necessary information about the listing
   * @return ResponseEntity with AddListingResponse containing the ID of the added and
   * a message
   */
  @PostMapping
  public ResponseEntity<AddListingResponse> addListing(
          @Valid @RequestBody AddListingRequest listing) {
    logger.info("Received request to add listing: " + listing);
    try {
      AddListingResponse response = listingService.addListing(listing);
      logger.info("Listing added successfully with ID: " + response.getId());
      return ResponseEntity.ok(response);

    } catch (IllegalArgumentException e) {
      logger.warning("Invalid listing data: " + e.getMessage());
      return ResponseEntity.badRequest().body(new AddListingResponse(
              null, "Invalid listing data: " + e.getMessage()));

    } catch (Exception e) {
      logger.severe("Error while adding listing: " + e.getCause());
      return ResponseEntity.internalServerError().body(new AddListingResponse(
              null, "An unexpected error occurred while adding listing: "
              + e.getMessage()));
    }
  }

  /**
   * Endpoint for fetching a listing by its ID, and retrieving all the information
   * about it.
   *
   * @param id the ID of the listing to be fetched
   * @return ResponseEntity with FullListingResponse containing all the information
   */
  @GetMapping("/{id}")
  public ResponseEntity<FullListingResponse> getListingById(@PathVariable Long id) {
    logger.info("Received request for listing with ID: " + id);
    try {
      FullListingResponse response = listingService.getListingById(id);
      logger.info("Listing found with ID: " + id);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.warning("No listing found with ID: " + id);
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint for updating a listing by its ID.
   * It takes the ID of the listing to be updated,
   * and the new data for the listing.
   *
   * @param id         the ID of the listing to be updated, is part of the URL
   * @param request    the new data for the listing
   * @param authHeader the authorization header containing the token
   * @return ResponseEntity with a message indicating the result of the update
   */
  @PutMapping("/{id}")
  public ResponseEntity<String> updateListing(
          @PathVariable Long id,
          @Valid @RequestBody ModifyListingRequest request,
          @RequestHeader("Authorization") String authHeader
  ) {
    logger.info("Received request to update listing with ID: " + id);
    try {
      listingService.updateListing(id, request, TokenExtractor.extractToken(authHeader));
      logger.info("Listing updated successfully with ID: " + id);
      return ResponseEntity.ok("Listing updated successfully");
    } catch (IllegalAccessException e) {
      logger.warning("Unauthorized attempt to update listing: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Unauthorized: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid argument: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body("Invalid argument: " + e.getMessage());
    } catch (Exception e) {
      logger.severe("Error while updating listing: " + e.getMessage());
      return ResponseEntity.internalServerError().body("An unexpected error occurred while updating listing: "
              + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving all listings a user has posted.
   * It takes the user ID as a parameter, and returns a list of listings
   * posted by that user.
   *
   * @param userId         the ID of the user whose posted listings to fetch
   * @param authHeader the authorization header containing the token
   * @return ResponseEntity with MultipleListingsResponse containing the posted listings
   */
  @GetMapping("/{userId}/posted")
  public ResponseEntity<MultipleListingsResponse> getPostedListings(
          @PathVariable Long userId,
          @PageableDefault(size = 20, page = 1) Pageable pageable,
          @RequestHeader("Authorization") String authHeader
  ) {
    logger.info("Received request for posted listings for user ID: " + userId);
    try {
      MultipleListingsResponse response = listingService.getPostedListings(userId, pageable,
              TokenExtractor.extractToken(authHeader));
      logger.info("Posted listings fetched successfully " + response.getElements().size() + " listings found");
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid user ID: " + e.getMessage());
      return ResponseEntity.badRequest().body(new MultipleListingsResponse(
              Collections.emptyList(), 0, 0,
              pageable.getPageNumber(), pageable.getPageSize(), true, true
      ));
    } catch (Exception e) {
      logger.severe("Error while fetching posted listings: " + e.getMessage());
      return ResponseEntity.internalServerError().body(new MultipleListingsResponse(
              Collections.emptyList(), 0, 0,
              pageable.getPageNumber(), pageable.getPageSize(), true, true
      ));
    }
  }

  /**
   * Endpoint for retrieving recommended listings for a user.
   * It takes the user ID as a parameter, and returns a list of recommended listings
   * for that user.
   *
   * @param userId   the ID of the user for whom to fetch recommended listings
   * @param pageable pagination parameters
   * @return ResponseEntity with MultipleListingsResponse containing the recommended listings
   */
  @GetMapping("/user/recommended")
  public ResponseEntity<MultipleListingsResponse> getRecommendedListings(
          @RequestParam Long userId,
          @PageableDefault(size = 20, page = 1) Pageable pageable
  ) {
    logger.info("Received request for recommended listings for user ID: " + userId);
    try {
      MultipleListingsResponse response = listingService.getRecommendedListings(userId, pageable);
      logger.info("Recommended listings fetched successfully " + response.getElements().size() + " listings found");
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid user ID: " + e.getMessage());
      return ResponseEntity.badRequest().body(new MultipleListingsResponse(
              Collections.emptyList(), 0, 0,
              pageable.getPageNumber(), pageable.getPageSize(), true, true
      ));
    } catch (Exception e) {
      logger.severe("Error while fetching recommended listings: " + e.getCause() + Arrays.toString(e.getStackTrace()));
      return ResponseEntity.internalServerError().body(new MultipleListingsResponse(
              Collections.emptyList(), 0, 0,
              pageable.getPageNumber(), pageable.getPageSize(), true, true
      ));
    }
  }

  /**
   * Endpoint for updating the status of a listing.
   * It takes the ID of the listing to be updated, the new status,
   * and the authorization header containing the token.
   *
   * @param listingId  the ID of the listing to be updated
   * @param status     the new status for the listing
   * @param authHeader the authorization header containing the token
   * @return ResponseEntity with a status and message indicating the result of the update
   */
  @PutMapping("/{listingId}/status")
  public ResponseEntity<String> updateListingStatus(
          @PathVariable Long listingId,
          @RequestBody ListingStatusRequest status,
          @RequestHeader("Authorization") String authHeader
  ) {
    logger.info("Received request to update listing status with ID: " + listingId);
    try {
      listingService.updateListingStatus(listingId, status, TokenExtractor.extractToken(authHeader));
      logger.info("Listing status updated successfully with ID: " + listingId);
      return ResponseEntity.ok("Listing status updated successfully");
    } catch (IllegalAccessException e) {
      logger.warning("Unauthorized attempt to update listing status: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Unauthorized: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid argument: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body("Invalid argument: " + e.getMessage());
    } catch (Exception e) {
      logger.severe("Error while updating listing status: " + e.getMessage());
      return ResponseEntity.internalServerError().body("An unexpected error occurred while updating listing status: "
              + e.getMessage());
    }
  }

  /**
   * Endpoint for searching listings based on a query string.
   * It takes the query string and pagination parameters,
   * and returns a list of listings that match the query.
   *
   * @param query the query string to search for listings
   * @param pageable pagination parameters
   * @return ResponseEntity with MultipleListingsResponse containing the search results
   */
  @GetMapping("/search")
  public ResponseEntity<MultipleListingsResponse> searchListings(
          @RequestParam String query,
          @PageableDefault(size = 20, page = 1) Pageable pageable
  ) {
    logger.info("Received request to search listings with query: " + query);
    try {
      MultipleListingsResponse response = listingService.searchForListings(query, pageable);
      logger.info("Search results fetched successfully " + response.getElements().size() + " listings found");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.severe("Error while searching listings: " + e.getMessage());
      return ResponseEntity.internalServerError().body(new MultipleListingsResponse(
              Collections.emptyList(), 0, 0,
              pageable.getPageNumber(), pageable.getPageSize(), true, true
      ));
    }
  }
}
