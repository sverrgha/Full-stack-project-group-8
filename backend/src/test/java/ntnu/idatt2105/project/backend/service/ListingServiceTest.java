package ntnu.idatt2105.project.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ntnu.idatt2105.project.backend.dto.request.AddListingRequest;
import ntnu.idatt2105.project.backend.dto.request.ListingFilterRequest;
import ntnu.idatt2105.project.backend.dto.response.AddListingResponse;
import ntnu.idatt2105.project.backend.dto.response.FullListingResponse;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.dto.response.ShortListingResponse;
import ntnu.idatt2105.project.backend.model.Listing;
import ntnu.idatt2105.project.backend.model.Location;
import ntnu.idatt2105.project.backend.repository.ListingImageRepo;
import ntnu.idatt2105.project.backend.repository.ListingRepo;
import ntnu.idatt2105.project.backend.repository.LocationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

  @Mock
  private ListingRepo listingRepo;

  @Mock
  private ListingImageRepo listingImageRepo;

  @Mock
  private LocationRepo locationRepo;

  @InjectMocks
  private ListingService listingService;

  private Listing listing1;
  private Listing listing2;
  private Location location1;

  @BeforeEach
  void setUp() {
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

  @Test
  void getListingByFilter_validFilter_returnsMultipleListingsResponse() {
    ListingFilterRequest filterRequest = new ListingFilterRequest();
    filterRequest.setCategoryId(1L);
    filterRequest.setCity("Test City");
    filterRequest.setMinPrice(50.0);
    filterRequest.setMaxPrice(150.0);
    filterRequest.setConditions(Collections.singletonList("NEW"));
    Pageable pageable = PageRequest.of(1, 10);
    when(listingRepo.getAllListingsByCriteria(any(), any(), any(), any(), any(), any())).thenReturn(new Listing[]{listing1});
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
    System.out.println("LISTING: " + response.getElements().get(0));
    ShortListingResponse shortListing = response.getElements().get(0);
    assertEquals(listing1.getId(), shortListing.getId());
    assertEquals(listing1.getTitle(), shortListing.getTitle());
    assertEquals(listing1.getPrice(), shortListing.getPrice());
    assertEquals(location1.getCity(), shortListing.getCity());
    assertEquals("image1.jpg", shortListing.getPathToImage());
    assertEquals(listing1.getCondition().name().toLowerCase(), shortListing.getCondition());

    verify(listingRepo).getAllListingsByCriteria(eq(1L), eq("Test City"), eq(50.0), eq(150.0), eq(Collections.singletonList("NEW")), any(Pageable.class));
  }

  @Test
  void getListingByFilter_invalidCondition_throwsIllegalArgumentException() {
    ListingFilterRequest filterRequest = new ListingFilterRequest();
    filterRequest.setConditions(Collections.singletonList("INVALID"));
    Pageable pageable = PageRequest.of(1, 10);
    assertThrows(IllegalArgumentException.class, () -> listingService.getListingByFilter(filterRequest, pageable));
    verifyNoInteractions(listingRepo);
  }

  @Test
  void getListingByFilter_nullConditions_returnsMultipleListingsResponse() {
    ListingFilterRequest filterRequest = new ListingFilterRequest();
    Pageable pageable = PageRequest.of(1, 10);
    when(listingRepo.getAllListingsByCriteria(isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
            .thenReturn(new Listing[]{listing1, listing2});
    when(locationRepo.getLocationByPostalCode(1234)).thenReturn(Optional.of(location1));
    when(locationRepo.getLocationByPostalCode(5678)).thenReturn(Optional.empty());
    when(listingImageRepo.getOneImageByListingId(1L)).thenReturn(Optional.of("image1.jpg"));
    when(listingImageRepo.getOneImageByListingId(2L)).thenReturn(Optional.empty());

    MultipleListingsResponse response = listingService.getListingByFilter(filterRequest, pageable);

    assertNotNull(response);
    assertEquals(2, response.getElements().size());
    assertEquals(2, response.getTotalElements());
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
    addListingRequest.setPostalCode(9012);
    addListingRequest.setImages(Arrays.asList("imageA.jpg", "imageB.jpg"));

    Listing savedListing = new Listing();
    savedListing.setId(3L);
    savedListing.setTitle("New Listing");
    savedListing.setCategoryId(3L);
    savedListing.setPrice(250.0);
    savedListing.setBriefDescription("New brief description");
    savedListing.setDescription("New long description");
    savedListing.setUserId(30L);
    savedListing.setStatus(Listing.Status.ACTIVE); // Default status?
    savedListing.setCondition(Listing.Condition.NEW);
    savedListing.setCreatedAt(Date.valueOf(LocalDate.now()));
    savedListing.setReservedByUserId(null);
    savedListing.setReservedAt(null);
    savedListing.setSoldToUserId(null);
    savedListing.setSoldAt(null);
    savedListing.setPostalCode(9012);
    savedListing.setViewsCount(0);

    when(listingRepo.save(eq("New Listing"), eq(3L), eq(250.0), eq("New brief description"), eq("New long description"), eq(30L), eq(Listing.Condition.NEW), eq(9012)))
            .thenReturn(Optional.of(savedListing));
    doNothing().when(listingImageRepo).save(3L, "imageA.jpg");
    doNothing().when(listingImageRepo).save(3L, "imageB.jpg");

    AddListingResponse response = listingService.addListing(addListingRequest);

    assertNotNull(response);
    assertEquals(3L, response.getId());
    assertEquals("Listing added successfully", response.getMessage());

    verify(listingImageRepo, times(2)).save(eq(3L), anyString());
  }
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
    addListingRequest.setPostalCode(9012);
    addListingRequest.setImages(Arrays.asList("imageA.jpg", "imageB.jpg"));

    when(listingRepo.save(any(), any(), any(), any(), any(), any(), any(), anyInt()))
            .thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> listingService.addListing(addListingRequest));
    verifyNoInteractions(listingImageRepo);
  }

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

  @Test
  void getListingById_invalidId_throwsIllegalArgumentException() {
    when(listingRepo.getListingById(99L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> listingService.getListingById(99L));
    verify(listingRepo).getListingById(99L);
    verifyNoInteractions(locationRepo, listingImageRepo);
  }

  @Test
  void getListingByFilter_validFilter2_returnsMultipleListingsResponse() {
    ListingFilterRequest filterRequest = new ListingFilterRequest();
    filterRequest.setCategoryId(1L);
    filterRequest.setCity("Test City");
    filterRequest.setMinPrice(50.0);
    filterRequest.setMaxPrice(150.0);
    filterRequest.setConditions(Collections.singletonList("NEW"));
    Pageable pageable = PageRequest.of(1, 10);

    // Mock the repository to return our test listings
    when(listingRepo.getAllListingsByCriteria(eq(1L), eq("Test City"), eq(50.0), eq(150.0), eq(Collections.singletonList("NEW")), any(Pageable.class)))
            .thenReturn(new Listing[]{listing1, listing2});

    // Mock the location and image retrieval
    when(locationRepo.getLocationByPostalCode(1234)).thenReturn(Optional.of(location1));
    when(locationRepo.getLocationByPostalCode(5678)).thenReturn(Optional.empty());
    when(listingImageRepo.getOneImageByListingId(1L)).thenReturn(Optional.of("image1.jpg"));
    when(listingImageRepo.getOneImageByListingId(2L)).thenReturn(Optional.empty());

    MultipleListingsResponse response = listingService.getListingByFilter(filterRequest, pageable);

    assertNotNull(response);
    assertEquals(2, response.getElements().size()); // Now we expect two listings

    // Assertions for the first ShortListingResponse (based on listing1)
    ShortListingResponse shortListing1 = response.getElements().get(0);
    assertEquals(listing1.getId(), shortListing1.getId());
    assertEquals(listing1.getTitle(), shortListing1.getTitle());
    assertEquals(listing1.getPrice(), shortListing1.getPrice());
    assertEquals(location1.getCity(), shortListing1.getCity());
    assertEquals("image1.jpg", shortListing1.getPathToImage());
    assertEquals(listing1.getCondition().name().toLowerCase(), shortListing1.getCondition());

    // Assertions for the second ShortListingResponse (based on listing2)
    ShortListingResponse shortListing2 = response.getElements().get(1);
    assertEquals(listing2.getId(), shortListing2.getId());
    assertEquals(listing2.getTitle(), shortListing2.getTitle());
    assertEquals(listing2.getPrice(), shortListing2.getPrice());
    assertNull(shortListing2.getCity());
    assertNull(shortListing2.getPathToImage());
    assertEquals(listing2.getCondition().name().toLowerCase(), shortListing2.getCondition());

    // Verify repository interactions
    verify(listingRepo).getAllListingsByCriteria(eq(1L), eq("Test City"), eq(50.0), eq(150.0), eq(Collections.singletonList("NEW")), any(Pageable.class));
    verify(locationRepo, times(2)).getLocationByPostalCode(anyInt());
    verify(listingImageRepo, times(2)).getOneImageByListingId(anyLong());
  }
}
