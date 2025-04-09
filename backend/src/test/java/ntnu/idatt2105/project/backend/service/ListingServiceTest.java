package ntnu.idatt2105.project.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ntnu.idatt2105.project.backend.dto.request.AddListingRequest;
import ntnu.idatt2105.project.backend.dto.request.ListingFilterRequest;
import ntnu.idatt2105.project.backend.dto.request.LocationDTO;
import ntnu.idatt2105.project.backend.dto.request.ModifyListingRequest;
import ntnu.idatt2105.project.backend.dto.response.AddListingResponse;
import ntnu.idatt2105.project.backend.dto.response.FullListingResponse;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.dto.response.ShortListingResponse;
import ntnu.idatt2105.project.backend.model.CategoryShare;
import ntnu.idatt2105.project.backend.model.Listing;
import ntnu.idatt2105.project.backend.model.ListingImage;
import ntnu.idatt2105.project.backend.model.Location;
import ntnu.idatt2105.project.backend.repository.BrowsingHistoryRepo;
import ntnu.idatt2105.project.backend.repository.ListingImageRepo;
import ntnu.idatt2105.project.backend.repository.ListingRepo;
import ntnu.idatt2105.project.backend.repository.LocationRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for the ListingService class.
 * This class tests the methods in the ListingService class to ensure they work as expected.
 */
@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

  @Mock
  private ListingRepo listingRepo;

  @Mock
  private ListingImageRepo listingImageRepo;

  @Mock
  private LocationRepo locationRepo;

  @Mock
  private BrowsingHistoryRepo browsingHistoryRepo;

  @Mock
  private UserService userService;

  @InjectMocks
  private ListingService listingService;

  private static Listing listing1;
  private static Listing listing2;
  private static Location location1;

  /**
   * Sets up the test data before each test case.
   */
  @BeforeAll
  static void setUp() {
    listing1 = new Listing();
    listing1.setId(1L);
    listing1.setTitle("Test Listing 1");
    listing1.setCategoryId(1L);
    listing1.setPrice(100.0);
    listing1.setBriefDescription("Brief description 1");
    listing1.setDescription("Long description 1");
    listing1.setUserId(10L);
    listing1.setStatus(Listing.Status.ACTIVE);
    listing1.setCondition(Listing.Condition.NEW);
    listing1.setCreatedAt(Date.valueOf(LocalDate.now()));
    listing1.setReservedByUserId(null);
    listing1.setReservedAt(null);
    listing1.setSoldToUserId(null);
    listing1.setSoldAt(null);
    listing1.setPostalCode(1234);
    listing1.setViewsCount(5);

    listing2 = new Listing();
    listing2.setId(2L);
    listing2.setTitle("Test Listing 2");
    listing2.setCategoryId(2L);
    listing2.setPrice(200.0);
    listing2.setBriefDescription("Brief description 2");
    listing2.setDescription("Long description 2");
    listing2.setUserId(20L);
    listing2.setStatus(Listing.Status.SOLD);
    listing2.setCondition(Listing.Condition.FAIR);
    listing2.setCreatedAt(Date.valueOf(LocalDate.now().minusDays(1)));
    listing2.setReservedByUserId(null);
    listing2.setReservedAt(null);
    listing2.setSoldToUserId(22L);
    listing2.setSoldAt(Date.valueOf(LocalDate.now().minusDays(2)));
    listing2.setPostalCode(5678);
    listing2.setViewsCount(10);

    location1 = new Location();
    location1.setPostalCode(1234);
    location1.setCity("Test City");
  }

  /**
   * Tests the getListingByFilter method with a valid filter.
   * This test checks if the method returns the expected response
   * with the correct number of listings and pagination information.
   */
  @Test
  void getListingByFilter_validFilter_returnsMultipleListingsResponse() {
    ListingFilterRequest filterRequest = new ListingFilterRequest();
    filterRequest.setCategoryId(1L);
    filterRequest.setCity("Test City");
    filterRequest.setMinPrice(50.0);
    filterRequest.setMaxPrice(150.0);
    filterRequest.setConditions(Collections.singletonList("NEW"));
    Pageable pageable = PageRequest.of(0, 10);
    when(listingRepo.getAllListingsByCriteria(any(), any(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(listing1), pageable, 1));
    when(locationRepo.getLocationByPostalCode(anyInt())).thenReturn(Optional.of(location1));
    when(listingImageRepo.getOneImageByListingId(anyLong())).thenReturn(Optional.of("image1.jpg"));

    MultipleListingsResponse response = listingService.getListingByFilter(filterRequest, pageable);

    assertNotNull(response);
    assertEquals(1, response.getElements().size());
    assertEquals(1, response.getTotalElements());
    assertEquals(1, response.getTotalPages());
    assertEquals(1, response.getCurrentPage());
    assertEquals(10, response.getPageSize());
    assertTrue(response.isFirstPage());
    assertTrue(response.isLastPage());
    ShortListingResponse shortListing = response.getElements().get(0);
    assertEquals(listing1.getId(), shortListing.getId());
    assertEquals(listing1.getTitle(), shortListing.getTitle());
    assertEquals(listing1.getPrice(), shortListing.getPrice());
    assertEquals(location1.getCity(), shortListing.getCity());
    assertEquals("image1.jpg", shortListing.getPathToImage());
    assertEquals(listing1.getCondition().name().toLowerCase(), shortListing.getCondition());

    verify(listingRepo).getAllListingsByCriteria(eq(1L), eq("Test City"), eq(50.0), eq(150.0), eq(Collections.singletonList("NEW")), any(Pageable.class));
  }

  /**
   * Tests the getListingByFilter method with an invalid condition.
   * This test checks if the method throws an IllegalArgumentException
   * when an invalid condition is provided in the filter request.
   */
  @Test
  void getListingByFilter_invalidCondition_throwsIllegalArgumentException() {
    ListingFilterRequest filterRequest = new ListingFilterRequest();
    filterRequest.setConditions(Collections.singletonList("INVALID"));
    Pageable pageable = PageRequest.of(1, 10);
    assertThrows(IllegalArgumentException.class, () -> listingService.getListingByFilter(filterRequest, pageable));
    verifyNoInteractions(listingRepo);
  }

  /**
   * Tests the getListingByFilter method with null conditions.
   * This test checks if the method returns multiple listings
   * when the filter request has null conditions.
   */
  @Test
  void getListingByFilter_nullConditions_returnsMultipleListingsResponse() {
    ListingFilterRequest filterRequest = new ListingFilterRequest();
    Pageable pageable = PageRequest.of(0, 10);
    when(listingRepo.getAllListingsByCriteria(any(), any(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<>(Arrays.asList(listing1, listing2), pageable, 2));

    when(locationRepo.getLocationByPostalCode(1234)).thenReturn(Optional.of(location1));
    when(locationRepo.getLocationByPostalCode(5678)).thenReturn(Optional.empty());
    when(listingImageRepo.getOneImageByListingId(1L)).thenReturn(Optional.of("image1.jpg"));
    when(listingImageRepo.getOneImageByListingId(2L)).thenReturn(Optional.empty());

    MultipleListingsResponse response = listingService.getListingByFilter(filterRequest, pageable);
    assertNotNull(response);
    assertEquals(2, response.getElements().size());
    assertEquals(1, response.getTotalPages());
    assertEquals(1, response.getCurrentPage());
    assertEquals(10, response.getPageSize());
    assertTrue(response.isFirstPage());
    assertTrue(response.isLastPage());

    ShortListingResponse shortListing1 = response.getElements().get(0);
    assertEquals(listing1.getId(), shortListing1.getId());
    assertEquals(listing1.getTitle(), shortListing1.getTitle());
    assertEquals(listing1.getPrice(), shortListing1.getPrice());
    assertEquals(location1.getCity(), shortListing1.getCity());
    assertEquals("image1.jpg", shortListing1.getPathToImage());
    assertEquals(listing1.getCondition().name().toLowerCase(), shortListing1.getCondition());

    ShortListingResponse shortListing2 = response.getElements().get(1);
    assertEquals(listing2.getId(), shortListing2.getId());
    assertEquals(listing2.getTitle(), shortListing2.getTitle());
    assertEquals(listing2.getPrice(), shortListing2.getPrice());
    assertNull(shortListing2.getCity());
    assertNull(shortListing2.getPathToImage());
    assertEquals(listing2.getCondition().name().toLowerCase(), shortListing2.getCondition());

    verify(listingRepo).getAllListingsByCriteria(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
  }

  /**
   * Tests the addListing method with a valid request.
   * This test checks if the method returns the expected response
   * with the correct ID and success message.
   */
  @Test
  void addListing_validRequest_returnsAddListingResponse() {
    AddListingRequest addListingRequest = new AddListingRequest();
    addListingRequest.setTitle("New Listing");
    addListingRequest.setCategoryId(3L);
    addListingRequest.setPrice(250.0);
    addListingRequest.setBriefDescription("New brief description");
    addListingRequest.setDescription("New long description");
    addListingRequest.setUserId(30L);
    addListingRequest.setCondition("new");
    addListingRequest.setLocation(new LocationDTO(
            9012, "New City", "New Country", 0.0, 0.0
    ));
    addListingRequest.setImages(Arrays.asList("imageA.jpg", "imageB.jpg"));

    Listing savedListing = new Listing();
    savedListing.setId(3L);
    savedListing.setTitle("New Listing");
    savedListing.setCategoryId(3L);
    savedListing.setPrice(250.0);
    savedListing.setBriefDescription("New brief description");
    savedListing.setDescription("New long description");
    savedListing.setUserId(30L);
    savedListing.setStatus(Listing.Status.ACTIVE);
    savedListing.setCondition(Listing.Condition.NEW);
    savedListing.setCreatedAt(Date.valueOf(LocalDate.now()));
    savedListing.setReservedByUserId(null);
    savedListing.setReservedAt(null);
    savedListing.setSoldToUserId(null);
    savedListing.setSoldAt(null);
    savedListing.setPostalCode(9012);
    savedListing.setViewsCount(0);

    when(listingRepo.save("New Listing", 3L, 250.0,
            "New brief description", "New long description",
            30L, Listing.Condition.NEW, 9012))
            .thenReturn(Optional.of(savedListing));
    doNothing().when(listingImageRepo).save(3L, "imageA.jpg");
    doNothing().when(listingImageRepo).save(3L, "imageB.jpg");

    AddListingResponse response = listingService.addListing(addListingRequest);

    assertNotNull(response);
    assertEquals(3L, response.getId());
    assertEquals("Listing added successfully", response.getMessage());

    verify(listingImageRepo, times(2)).save(eq(3L), anyString());
  }

  /**
   * Tests the addListing method with a request that fails to save.
   * This test checks if the method throws an IllegalArgumentException
   * when the listing cannot be saved.
   */
  @Test
  void addListing_saveFails_throwsIllegalArgumentException() {
    AddListingRequest addListingRequest = new AddListingRequest();
    addListingRequest.setTitle("New Listing");
    addListingRequest.setCategoryId(3L);
    addListingRequest.setPrice(250.0);
    addListingRequest.setBriefDescription("New brief description");
    addListingRequest.setDescription("New long description");
    addListingRequest.setUserId(30L);
    addListingRequest.setCondition("new");
    addListingRequest.setLocation(new LocationDTO(
            9012, "New City", "New Country", 0.0, 0.0
    ));
    addListingRequest.setImages(Arrays.asList("imageA.jpg", "imageB.jpg"));

    when(listingRepo.save(any(), any(), any(), any(), any(), any(), any(), anyInt()))
            .thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> listingService.addListing(addListingRequest));
    verifyNoInteractions(listingImageRepo);
  }

  /**
   * Tests the getListingById method with a valid ID.
   * This test checks if the method returns the expected response
   * with the correct listing details and images.
   */
  @Test
  void getListingById_validId_returnsFullListingResponse() {
    when(listingRepo.getListingById(1L)).thenReturn(Optional.of(listing1));
    when(listingImageRepo.getAllImagesByListingId(1L)).thenReturn(Arrays.asList("image1.jpg", "image2.jpg"));
    when(locationRepo.getLocationByPostalCode(1234)).thenReturn(Optional.of(location1));
    FullListingResponse response = listingService.getListingById(1L);

    assertNotNull(response);
    assertEquals(listing1.getId(), response.getId());
    assertEquals(listing1.getTitle(), response.getTitle());
    assertEquals(listing1.getCategoryId(), response.getCategoryId());
    assertEquals(listing1.getPrice(), response.getPrice());
    assertEquals(listing1.getBriefDescription(), response.getBriefDescription());
    assertEquals(listing1.getDescription(), response.getDescription());
    assertEquals(listing1.getUserId(), response.getUserId());
    assertEquals(listing1.getStatus().name().toLowerCase(), response.getStatus());
    assertEquals(listing1.getCondition().name().toLowerCase(), response.getCondition());
    assertEquals(listing1.getCreatedAt(), response.getCreatedAt());
    assertEquals(listing1.getReservedByUserId(), response.getReservedByUserId());
    assertEquals(listing1.getReservedAt(), response.getReservedAt());
    assertEquals(listing1.getSoldAt(), response.getSoldAt());
    assertEquals(listing1.getSoldToUserId(), response.getSoldToUserId());
    assertEquals(listing1.getPostalCode(), response.getPostalCode());
    assertEquals(listing1.getViewsCount(), response.getViewsCount());
    assertEquals(Arrays.asList("image1.jpg", "image2.jpg"), response.getImages());
    assertEquals(location1.getCity(), response.getCity());

    verify(listingRepo).getListingById(1L);
    verify(locationRepo).getLocationByPostalCode(1234);
    verify(listingImageRepo).getAllImagesByListingId(1L);
  }

  /**
   * Tests the getListingById method with an invalid ID.
   * This test checks if the method throws an IllegalArgumentException
   * when the listing is not found.
   */
  @Test
  void getListingById_invalidId_throwsIllegalArgumentException() {
    when(listingRepo.getListingById(99L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> listingService.getListingById(99L));
    verify(listingRepo).getListingById(99L);
    verifyNoInteractions(locationRepo, listingImageRepo);
  }

  /**
   * Tests the getListingByFilter method with a valid filter.
   * This test checks if the method returns multiple listings
   * with the correct details and images.
   */
  @Test
  void getListingByFilter_validFilter2_returnsMultipleListingsResponse() {
    ListingFilterRequest filterRequest = new ListingFilterRequest();
    filterRequest.setCategoryId(1L);
    filterRequest.setCity("Test City");
    filterRequest.setMinPrice(50.0);
    filterRequest.setMaxPrice(150.0);
    filterRequest.setConditions(Collections.singletonList("NEW"));
    Pageable pageable = PageRequest.of(1, 10);

    when(listingRepo.getAllListingsByCriteria(eq(1L), eq("Test City"), eq(50.0), eq(150.0), eq(Collections.singletonList("NEW")), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Arrays.asList(listing1, listing2), pageable, 2));

    when(locationRepo.getLocationByPostalCode(1234)).thenReturn(Optional.of(location1));
    when(locationRepo.getLocationByPostalCode(5678)).thenReturn(Optional.empty());
    when(listingImageRepo.getOneImageByListingId(1L)).thenReturn(Optional.of("image1.jpg"));
    when(listingImageRepo.getOneImageByListingId(2L)).thenReturn(Optional.empty());

    MultipleListingsResponse response = listingService.getListingByFilter(filterRequest, pageable);

    assertNotNull(response);
    assertEquals(2, response.getElements().size());

    ShortListingResponse shortListing1 = response.getElements().get(0);
    assertEquals(listing1.getId(), shortListing1.getId());
    assertEquals(listing1.getTitle(), shortListing1.getTitle());
    assertEquals(listing1.getPrice(), shortListing1.getPrice());
    assertEquals(location1.getCity(), shortListing1.getCity());
    assertEquals("image1.jpg", shortListing1.getPathToImage());
    assertEquals(listing1.getCondition().name().toLowerCase(), shortListing1.getCondition());

    ShortListingResponse shortListing2 = response.getElements().get(1);
    assertEquals(listing2.getId(), shortListing2.getId());
    assertEquals(listing2.getTitle(), shortListing2.getTitle());
    assertEquals(listing2.getPrice(), shortListing2.getPrice());
    assertNull(shortListing2.getCity());
    assertNull(shortListing2.getPathToImage());
    assertEquals(listing2.getCondition().name().toLowerCase(), shortListing2.getCondition());

    verify(listingRepo).getAllListingsByCriteria(eq(1L), eq("Test City"), eq(50.0), eq(150.0), eq(Collections.singletonList("NEW")), any(Pageable.class));
    verify(locationRepo, times(2)).getLocationByPostalCode(anyInt());
    verify(listingImageRepo, times(2)).getOneImageByListingId(anyLong());
  }

  /**
   * Tests the getMultipleListingsById method with a single ID.
   * This test checks if the method returns the expected response
   * with the correct listing details and images.
   */
  @Test
  void getMultipleListingsById_singleId_returnsResponse() {
    List<Long> ids = Collections.singletonList(listing1.getId());
    Pageable pageable = PageRequest.of(0, 10);
    List<Listing> listings = Collections.singletonList(listing1);
    Page<Listing> mockPage = new PageImpl<>(listings, pageable, 1);
    when(listingRepo.getMultipleListingsByIds(ids, pageable)).thenReturn(mockPage);

    MultipleListingsResponse actualResponse = listingService.getMultipleListingsById(ids, pageable);

    assertNotNull(actualResponse);
    verify(listingRepo, times(1)).getMultipleListingsByIds(ids, pageable);
  }

  /**
   * Tests the getMultipleListingsById method with multiple IDs.
   * This test checks if the method returns the expected response
   * with the correct listing details and images.
   */
  @Test
  void getMultipleListingsById_multipleIds_returnsResponse() {
    List<Long> ids = Arrays.asList(listing1.getId(), listing2.getId());
    Pageable pageable = PageRequest.of(0, 10);
    List<Listing> listings = Arrays.asList(listing1, listing2);
    Page<Listing> mockPage = new PageImpl<>(listings, pageable, 2);
    when(listingRepo.getMultipleListingsByIds(ids, pageable)).thenReturn(mockPage);


    MultipleListingsResponse actualResponse = listingService.getMultipleListingsById(ids, pageable);

    assertNotNull(actualResponse);
    verify(listingRepo, times(1)).getMultipleListingsByIds(ids, pageable);
  }

  /**
   * Tests the listingExists method with an ID that exists.
   * This test checks if the method returns true when the listing
   * is found in the repository.
   */
  @Test
  void listingExists_idFound_returnsTrue() {
    Long id = 1L;
    when(listingRepo.getListingById(id)).thenReturn(Optional.of(new Listing()));

    boolean exists = listingService.listingExists(id);

    assertTrue(exists);
    verify(listingRepo, times(1)).getListingById(id);
  }

  /**
   * Tests the listingExists method with an ID that does not exist.
   * This test checks if the method returns false when the listing
   * is not found in the repository.
   */
  @Test
  void listingExists_idNotFound_returnsFalse() {
    Long id = 99L;
    when(listingRepo.getListingById(id)).thenReturn(Optional.empty());

    boolean exists = listingService.listingExists(id);

    assertFalse(exists);
    verify(listingRepo, times(1)).getListingById(id);
  }

  /**
   * Tests that a default listings is returned when the user has no previous browsing history.
   * This test checks if the method returns the expected response
   * with the correct listing details and pagination information.
   */
  @Test
  void getRecommendedListings_noBrowsingHistory_returnsDefaultListings() {
    Long userId = 100L;
    Pageable pageable = PageRequest.of(1, 20);
    Pageable localPageable = PageRequest.of(0, 20);
    List<CategoryShare> emptyShares = Collections.emptyList();

    doReturn(emptyShares).when(browsingHistoryRepo).getUsersCategoryShares(userId);
    when(listingRepo.getAllListingsByCriteria(any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(listing1), localPageable, 0));

    MultipleListingsResponse response = listingService.getRecommendedListings(userId, pageable);

    assertEquals(1, response.getElements().size());
    assertEquals(1, response.getCurrentPage());
    assertEquals(20, response.getPageSize());
    assertTrue(response.isFirstPage());
    assertTrue(response.isLastPage());
  }

  /**
   * Tests that the recommended listings are based on the user's browsing history.
   * This test checks if the method returns the expected response
   * with the correct listing details and pagination information.
   */
  @Test
  void getRecommendedListings_withBrowsingHistory_returnsListingsBasedOnShares() {
    Long userId = 100L;
    Pageable pageable = PageRequest.of(1, 10);

    List<CategoryShare> categoryShares = Arrays.asList(
            new CategoryShare(1L, 1, 0.7),
            new CategoryShare(2L, 2, 0.3)
    );
    when(browsingHistoryRepo.getUsersCategoryShares(userId)).thenReturn(categoryShares);

    Pageable category1Pageable = PageRequest.of(0, 7, pageable.getSort());
    Pageable category2Pageable = PageRequest.of(0, 3, pageable.getSort());

    List<Listing> category1Listings = Collections.singletonList(listing1);
    List<Listing> category2Listings = Collections.singletonList(listing2);

    when(listingRepo.getByCategoryId(1L, category1Pageable)).thenReturn(category1Listings);
    when(listingRepo.getByCategoryId(2L, category2Pageable)).thenReturn(category2Listings);

    MultipleListingsResponse response = listingService.getRecommendedListings(userId, pageable);

    assertEquals(2, response.getElements().size());
    assertEquals(1, response.getCurrentPage());
    assertEquals(10, response.getPageSize());
    assertEquals(listing1.getId(), response.getElements().get(0).getId());
    assertEquals(listing2.getId(), response.getElements().get(1).getId());
  }

  /**
   * Tests that the recommended listings are empty when there are no listings the categories.
   * This test checks if the method returns an empty response
   * with the correct pagination information.
   */
  @Test
  void getRecommendedListings_withBrowsingHistory_emptyCategoryResults() {
    Long userId = 100L;
    Pageable pageable = PageRequest.of(1, 10);

    List<CategoryShare> categoryShares = Arrays.asList(
            new CategoryShare(1L, 1, 0.5),
            new CategoryShare(2L, 2, 0.5)
    );
    when(browsingHistoryRepo.getUsersCategoryShares(userId)).thenReturn(categoryShares);

    Pageable category1Pageable = PageRequest.of(0, 5, pageable.getSort());
    Pageable category2Pageable = PageRequest.of(0, 5, pageable.getSort());

    when(listingRepo.getByCategoryId(1L, category1Pageable)).thenReturn(Collections.emptyList());
    when(listingRepo.getByCategoryId(2L, category2Pageable)).thenReturn(Collections.emptyList());

    MultipleListingsResponse response = listingService.getRecommendedListings(userId, pageable);

    assertEquals(0, response.getElements().size());
    assertEquals(1, response.getCurrentPage());
    assertEquals(10, response.getPageSize());
  }

  @Test
  void updateListing_validRequest_updatesListing() throws IllegalAccessException {
    Long listingId = 1L;
    Long userId = listing1.getUserId();
    String token = "testToken";
    LocationDTO newLocationDTO = new LocationDTO(5678,
            "New City", "New Country", 1.0, 1.0);
    ModifyListingRequest request = new ModifyListingRequest("New Title",
            2L, 75.0, "New brief", "New desc",
            "fair", Collections.emptyList(), newLocationDTO);

    when(listingRepo.getListingById(listingId)).thenReturn(Optional.of(listing1));
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    when(locationRepo.getLocationByPostalCode(5678)).thenReturn(Optional.empty());
    doNothing().when(locationRepo).save(any());
    doNothing().when(listingRepo).update(any());

    ArgumentCaptor<Listing> listingCaptor = ArgumentCaptor.forClass(Listing.class);

    listingService.updateListing(listingId, request, token);

    verify(listingRepo).update(listingCaptor.capture());
    assertEquals("New Title", listingCaptor.getValue().getTitle());
    assertEquals(2L, listingCaptor.getValue().getCategoryId());
    assertEquals(5678, listingCaptor.getValue().getPostalCode());
  }

  @Test
  void updateListing_listingNotFound_throwsException() {
    Long listingId = 99L;
    ModifyListingRequest request = new ModifyListingRequest();
    String token = "testToken";
    when(listingRepo.getListingById(listingId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> listingService.updateListing(listingId, request, token));
    verifyNoInteractions(userService);
    verifyNoInteractions(locationRepo);
    verifyNoInteractions(listingImageRepo);
    verify(listingRepo, times(0)).update(any(Listing.class));
  }

  @Test
  void updateListing_addAndDeleteImages() throws IllegalAccessException {
    Long listingId = listing1.getId();
    Long userId = listing1.getUserId();
    String token = "testToken";
    LocationDTO newLocationDTO = new LocationDTO(location1.getPostalCode(), location1.getCity(), null, null, null);
    ModifyListingRequest request = new ModifyListingRequest("New Title",
            2L, 75.0, "New brief", "New desc",
            "fair", List.of("image3.jpg"), newLocationDTO);

    when(listingRepo.getListingById(listingId)).thenReturn(Optional.of(listing1));
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    when(locationRepo.getLocationByPostalCode(location1.getPostalCode())).thenReturn(Optional.of(location1));
    when(listingImageRepo.getAllImagesByListingId(listingId)).thenReturn(
            Arrays.asList("image1.jpg", "image2.jpg"));
    doNothing().when(listingImageRepo).save(eq(listingId), eq("image3.jpg"));
    doNothing().when(listingImageRepo).deleteImage(eq(listingId), eq("image1.jpg"));
    doNothing().when(listingImageRepo).deleteImage(eq(listingId), eq("image2.jpg"));
    doNothing().when(listingRepo).update(any());

    listingService.updateListing(listingId, request, token);

    verify(listingImageRepo).save(listingId, "image3.jpg");
    verify(listingImageRepo, times(2)).deleteImage(eq(listingId), anyString());
  }
}
