package ntnu.idatt2105.project.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.AddListingRequest;
import ntnu.idatt2105.project.backend.dto.request.ListingFilterRequest;
import ntnu.idatt2105.project.backend.dto.response.AddListingResponse;
import ntnu.idatt2105.project.backend.dto.response.FullListingResponse;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.dto.response.ShortListingResponse;
import ntnu.idatt2105.project.backend.model.CategoryShare;
import ntnu.idatt2105.project.backend.model.Listing;
import ntnu.idatt2105.project.backend.repository.BrowsingHistoryRepo;
import ntnu.idatt2105.project.backend.repository.ListingImageRepo;
import ntnu.idatt2105.project.backend.repository.ListingRepo;
import ntnu.idatt2105.project.backend.repository.LocationRepo;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for handling listing-related operations.
 * It provides methods to add, fetch, and filter listings.
 * It uses the ListingRepo, ListingImageRepo adn LocationRepo to interact with the database.
 */
@Service
@RequiredArgsConstructor
public class ListingService {
  private final ListingImageRepo listingImageRepo;
  private final ListingRepo listingRepo;
  private final LocationRepo locationRepo;
  private final BrowsingHistoryRepo browsingHistoryRepo;

  /**
   * List of allowed sort fields for filtering listings.
   * This is used to validate the sort field in the filter request.
   */
  private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
          "price",
          "created_at"
  );

  /**
   * Fetches listings based on the provided filter criteria. If no criteria are provided,
   * it returns all listings. It also divides the listings into pages, to be sent in
   * smaller chunks on demand.
   *
   * @param filterRequest the filter criteria for fetching listings
   * @param pageable      pagination parameters
   * @return MultipleListingsResponse containing the listings and pagination info
   */
  public MultipleListingsResponse getListingByFilter(ListingFilterRequest filterRequest,
                                                     Pageable pageable) {
    List<String> conditions = filterRequest.getConditions();
    if (conditions != null) {
      for (String condition : conditions) {
        if (!EnumUtils.isValidEnumIgnoreCase(Listing.Condition.class, condition)) {
          throw new IllegalArgumentException("Invalid condition: " + condition);
        }
      }
    }

    int page = pageable.getPageNumber() - 1;
    if (page < 0) {
      page = 0;
    }

    String sortBy = filterRequest.getSortBy();
    String sortOrder = filterRequest.getSortOrder();

    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      sortBy = "created_at";
    }
    Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder)
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

    pageable = PageRequest.of(page, pageable.getPageSize(), Sort.by(direction, sortBy));


    Page<Listing> listings = listingRepo.getAllListingsByCriteria(
            filterRequest.getCategoryId(),
            filterRequest.getCity(),
            filterRequest.getMinPrice(),
            filterRequest.getMaxPrice(),
            filterRequest.getConditions(),
            pageable
    );
    return mapToMultipleListingResponse(listings);
  }

  /**
   * Adds a new listing to the database and stores related images.
   *
   * @param listing the listing to be added
   * @return AddListingResponse containing the ID of the new listing and a success message.
   */
  public AddListingResponse addListing(AddListingRequest listing) {
    Optional<Listing> newListing = listingRepo.save(
            listing.getTitle(),
            listing.getCategoryId(),
            listing.getPrice(),
            listing.getBriefDescription(),
            listing.getDescription(),
            listing.getUserId(),
            Listing.Condition.valueOf(listing.getCondition().toUpperCase()),
            listing.getPostalCode());

    if (newListing.isEmpty()) {
      throw new IllegalArgumentException("Failed to add listing");
    }
    for (String image : listing.getImages()) {
      if (image == null || image.isBlank()) {
        throw new IllegalArgumentException("Image path cannot be null or empty");
      }
      listingImageRepo.save(newListing.get().getId(), image);

    }
    return new AddListingResponse(newListing.get().getId(),
            "Listing added successfully");
  }

  /**
   * Fetches a listing by its ID. If the listing is not found, it throws an
   * IllegalArgumentException, if the id is null. If the listing is found,
   * it returns a FullListingResponse containing all the details of the listing.
   *
   * @param id the ID of the listing to fetch
   * @return FullListingResponse containing all the listing details
   * @throws IllegalArgumentException if no listing is found
   */
  public FullListingResponse getListingById(Long id) throws IllegalArgumentException {
    Optional<Listing> listingOptional = listingRepo.getListingById(id);
    if (listingOptional.isEmpty()) {
      throw new IllegalArgumentException("Listing not found");
    }
    Listing listing = listingOptional.get();

    String city = locationRepo.getLocationByPostalCode(listing.getPostalCode())
            .flatMap(location -> Optional.ofNullable(location.getCity()))
            .orElse(null);

    List<String> images = listingImageRepo.getAllImagesByListingId(id);

    return new FullListingResponse(
            listing.getId(),
            listing.getTitle(),
            listing.getCategoryId(),
            listing.getPrice(),
            listing.getBriefDescription(),
            listing.getDescription(),
            listing.getUserId(),
            listing.getStatus().name().toLowerCase(),
            listing.getCondition().name().toLowerCase(),
            listing.getCreatedAt(),
            listing.getReservedByUserId(),
            listing.getReservedAt(),
            listing.getSoldAt(),
            listing.getSoldToUserId(),
            listing.getPostalCode(),
            listing.getViewsCount(),
            images,
            city
    );
  }

  /**
   * Maps a list of Listing objects to a list of ShortListingResponse objects.
   * This is used to convert the listings fetched from the database
   * to a format suitable for the API response.
   *
   * @param listings the array of Listing objects to map
   * @return a list of ShortListingResponse objects, based on the listings
   */
  private List<ShortListingResponse> mapListingsToShortResponse(Listing[] listings) {
    return Arrays.stream(listings)
            .map(listing -> {
              String city = locationRepo.getLocationByPostalCode(listing.getPostalCode())
                      .flatMap(location -> Optional.ofNullable(location.getCity()))
                      .orElse(null);
              String imageUrl = listingImageRepo.getOneImageByListingId(listing.getId())
                      .orElse(null);

              return new ShortListingResponse(
                      listing.getId(),
                      listing.getTitle(),
                      listing.getPrice(),
                      listing.getBriefDescription(),
                      city,
                      imageUrl,
                      listing.getCondition().name().toLowerCase()
              );
            })
            .collect(Collectors.toList());
  }

  /**
   * Maps a Page of Listing objects to a MultipleListingsResponse object.
   * This is used to convert the listings fetched from the database
   * to a format suitable for the API response.
   * @param listings the Page of Listing objects to map
   * @return a MultipleListingsResponse object containing the listings and pagination info
   */
  private MultipleListingsResponse mapToMultipleListingResponse(Page<Listing> listings) {
    return new MultipleListingsResponse(
            mapListingsToShortResponse(listings.getContent().toArray(new Listing[0])),
            listings.getTotalElements(),
            listings.getTotalPages(),
            listings.getNumber() + 1,
            listings.getSize(),
            listings.isFirst(),
            listings.isLast()
    );
  }

  /**
   * Fetches multiple listings by their IDs. It uses pagination to limit the number of
   * listings returned in a single request.
   * @param ids the list of IDs of the listings to fetch
   * @param pageable the pagination parameters
   * @return MultipleListingsResponse containing the listings and pagination info
   */
  public MultipleListingsResponse getMultipleListingsById(List<Long> ids, Pageable pageable) {
    Page<Listing> listings = listingRepo.getMultipleListingsByIds(ids, pageable);
    return mapToMultipleListingResponse(listings);
  }

  /**
   * Retrieves recommended listings based on previous browsing history, if no history
   * exists for the user, recommended is just random from all listings
   * @param userId the ID of the user to be recommended
   * @param pageable the pagination information for the request
   * @return MultipleListingResponse with metadata for the pagination and the listings
   */
  public MultipleListingsResponse getRecommendedListings(Long userId, Pageable pageable) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    int pageNumber = Math.max(pageable.getPageNumber() - 1, 0);
    pageable = PageRequest.of(
            pageNumber,
            pageable.getPageSize(),
            pageable.getSort()
    );
    List<CategoryShare> categoryShares = browsingHistoryRepo.getUsersCategoryShares(userId);
    if (categoryShares.isEmpty()) {
      return getListingByFilter(new ListingFilterRequest(), pageable);
    }
    List<Listing> allListings = new ArrayList<>();

    for (CategoryShare categoryShare : categoryShares) {
      Long categoryId = categoryShare.getCategoryId();
      Double viewPercentage = categoryShare.getViewPercentage();
      int pageSize = (int) Math.ceil(pageable.getPageSize() * viewPercentage);

      Pageable categoryPageable = PageRequest.of(
              pageNumber,
              pageSize,
              pageable.getSort()
      );

      List<Listing> listings = listingRepo.getByCategoryId(categoryId, categoryPageable);
      allListings.addAll(listings);
    }
    return mapToMultipleListingResponse(new PageImpl<>(
            allListings,
            pageable,
            allListings.size()
    ));
  }

  /**
   * Checks if a listing exists by its ID. It returns true if the listing exists,
   * false otherwise.
   * @param id the ID of the listing to check
   * @return true if the listing exists, false otherwise
   */
  public boolean listingExists(Long id) {
    return listingRepo.getListingById(id).isPresent();
  }

}
