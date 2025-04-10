package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.dto.request.*;
import ntnu.idatt2105.project.backend.dto.response.AddListingResponse;
import ntnu.idatt2105.project.backend.dto.response.FullListingResponse;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.dto.response.ShortListingResponse;
import ntnu.idatt2105.project.backend.model.Listing;
import ntnu.idatt2105.project.backend.model.Location;
import ntnu.idatt2105.project.backend.service.ListingService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ListingController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ListingControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ListingService listingService;

  private Listing listing1;
  private Location location1;
  private FullListingResponse fullListingResponse1;
  private MultipleListingsResponse multipleListingsResponse;
  private AddListingResponse addListingResponse;
  private AddListingRequest addListingRequest;
  private ListingFilterRequest validFilterRequest;
  private ListingFilterRequest invalidFilterRequest;
  private String tokenHeader;
  private String extractedToken;

  /**
   * Sets up the test data before each test.
   */
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

    location1 = new Location();
    location1.setPostalCode(1234);
    location1.setCity("Test City");

    fullListingResponse1 = new FullListingResponse(
            listing1.getId(),
            listing1.getTitle(),
            listing1.getCategoryId(),
            listing1.getPrice(),
            listing1.getBriefDescription(),
            listing1.getDescription(),
            listing1.getUserId(),
            listing1.getStatus().toString(),
            listing1.getCondition().toString(),
            listing1.getCreatedAt(),
            listing1.getReservedByUserId(),
            listing1.getReservedAt(),
            listing1.getSoldAt(),
            listing1.getSoldToUserId(),
            listing1.getPostalCode(),
            listing1.getViewsCount(),
            Collections.emptyList(),
            location1.getCity()
    );

    ShortListingResponse shortListingResponse1 = new ShortListingResponse(
            listing1.getId(),
            listing1.getTitle(),
            listing1.getPrice(),
            listing1.getBriefDescription(),
            location1.getCity(),
            "",
            listing1.getCondition().toString().toLowerCase()
    );

    multipleListingsResponse = new MultipleListingsResponse();
    multipleListingsResponse.setElements(Collections.singletonList(shortListingResponse1));
    multipleListingsResponse.setTotalElements(1);
    multipleListingsResponse.setTotalPages(1);
    multipleListingsResponse.setCurrentPage(1);
    multipleListingsResponse.setPageSize(20);
    multipleListingsResponse.setFirstPage(true);
    multipleListingsResponse.setLastPage(true);

    addListingResponse = new AddListingResponse(1L, "Listing added successfully");

    addListingRequest = new AddListingRequest();
    addListingRequest.setTitle("New Listing");
    addListingRequest.setCategoryId(2L);
    addListingRequest.setPrice(200.0);
    addListingRequest.setBriefDescription("New brief description");
    addListingRequest.setDescription("New long description");
    addListingRequest.setUserId(11L);
    addListingRequest.setCondition("fair");
    addListingRequest.setLocation(new LocationDTO(
            5678, "Test City", "Test Country", 0.0, 0.0
    ));
    addListingRequest.setImages(Collections.emptyList());

    validFilterRequest = new ListingFilterRequest();
    validFilterRequest.setCategoryId(1L);
    validFilterRequest.setCity("Test City");

    invalidFilterRequest = new ListingFilterRequest();
    invalidFilterRequest.setConditions(Collections.singletonList("INVALID"));

    tokenHeader = "Bearer token";
    extractedToken = "token";
  }

  /**
   * Tests the getListings method with a valid filter.
   * Expects a 200 OK response with the correct listing data.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getListings_validFilter_returnsOk() throws Exception {
    when(listingService.getListingByFilter(any(ListingFilterRequest.class), any())).thenReturn(multipleListingsResponse);

    mockMvc.perform(get("/api/listing")
                    .param("categoryId", "1")
                    .param("city", "Test City"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.elements").isArray())
            .andExpect(jsonPath("$.elements.length()").value(1))
            .andExpect(jsonPath("$.elements[0].id").value(1));

    verify(listingService, times(1)).getListingByFilter(any(ListingFilterRequest.class), any());
  }

  /**
   * Tests the getListings method with an invalid filter.
   * Expects a 400 Bad Request response with an error message.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getListings_invalidFilter_returnsBadRequest() throws Exception {
    when(listingService.getListingByFilter(any(ListingFilterRequest.class), any()))
            .thenThrow(new IllegalArgumentException("Invalid filter criteria"));

    mockMvc.perform(get("/api/listing")
                    .param("conditions", "INVALID"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.elements").isArray())
            .andExpect(jsonPath("$.elements.length()").value(0));

    verify(listingService, times(1)).getListingByFilter(any(ListingFilterRequest.class), any());
  }

  /**
   * Tests the getListings method when the service throws an exception.
   * Expects a 500 Internal Server Error response.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getListings_serviceError_returnsInternalServerError() throws Exception {
    when(listingService.getListingByFilter(any(ListingFilterRequest.class), any()))
            .thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(get("/api/listing")
                    .param("categoryId", "1"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.elements").isArray())
            .andExpect(jsonPath("$.elements.length()").value(0));

    verify(listingService, times(1)).getListingByFilter(any(ListingFilterRequest.class), any());
  }

  /**
   * Tests the addListing method with valid input.
   * Expects a 200 OK response with the correct listing data.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void addListing_validInput_returnsOk() throws Exception {
    when(listingService.addListing(any(AddListingRequest.class))).thenReturn(addListingResponse);

    mockMvc.perform(post("/api/listing")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addListingRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.message").value("Listing added successfully"));

    verify(listingService, times(1)).addListing(any(AddListingRequest.class));
  }

  /**
   * Tests the addListing method with invalid input.
   * Expects a 400 Bad Request response with an error message.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void addListing_invalidInput_returnsBadRequest() throws Exception {
    when(listingService.addListing(any(AddListingRequest.class)))
            .thenThrow(new IllegalArgumentException("Invalid listing details"));

    mockMvc.perform(post("/api/listing")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addListingRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid listing data: Invalid listing details"))
            .andExpect(jsonPath("$.id").isEmpty());

    verify(listingService, times(1)).addListing(any(AddListingRequest.class));
  }

  /**
   * Tests the addListing method when the service throws an exception.
   * Expects a 500 Internal Server Error response.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void addListing_serviceError_returnsInternalServerError() throws Exception {
    when(listingService.addListing(any(AddListingRequest.class)))
            .thenThrow(new RuntimeException("Failed to save listing"));

    mockMvc.perform(post("/api/listing")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addListingRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred while adding listing: Failed to save listing"))
            .andExpect(jsonPath("$.id").isEmpty());

    verify(listingService, times(1)).addListing(any(AddListingRequest.class));
  }

  /**
   * Tests the getListingById method with a valid ID.
   * Expects a 200 OK response with the correct listing data.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getListingById_validId_returnsOk() throws Exception {
    when(listingService.getListingById(1L)).thenReturn(fullListingResponse1);

    mockMvc.perform(get("/api/listing/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Listing 1"))
            .andExpect(jsonPath("$.city").value("Test City"));

    verify(listingService, times(1)).getListingById(1L);
  }

  /**
   * Tests the getListingById method with an invalid ID.
   * Expects a 404 Not Found response.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getListingById_invalidId_returnsNotFound() throws Exception {
    when(listingService.getListingById(99L)).thenThrow(new IllegalArgumentException("No listing found with ID: 99"));

    mockMvc.perform(get("/api/listing/99"))
            .andExpect(status().isNotFound());

    verify(listingService, times(1)).getListingById(99L);
  }

  @Test
  @WithMockUser("test")
  void getRecommendedListings_validUserId_returnsOk() throws Exception {
    Long userId = 123L;
    Pageable pageable = PageRequest.of(1, 20, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

    when(listingService.getRecommendedListings(eq(userId), eq(pageable))).thenReturn(multipleListingsResponse);

    mockMvc.perform(get("/api/listing/user/recommended")
                    .param("userId", userId.toString())
                    .param("page", String.valueOf(pageable.getPageNumber()))
                    .param("size", String.valueOf(pageable.getPageSize()))
                    .param("sort", "createdAt,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.elements").isArray())
            .andExpect(jsonPath("$.elements.length()").value(1));

    verify(listingService, times(1)).getRecommendedListings(eq(userId), eq(pageable));
  }

  /**
   * Tests the getRecommendedListings endpoint with an invalid user ID, returning BadRequest.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getRecommendedListings_invalidUserId_returnsBadRequest() throws Exception {
    Long userId = 999L;
    Pageable pageable = PageRequest.of(1, 20);

    when(listingService.getRecommendedListings(eq(userId), eq(pageable)))
            .thenThrow(new IllegalArgumentException("Invalid user ID: " + userId));

    mockMvc.perform(get("/api/listing/user/recommended")
                    .param("userId", userId.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.elements").isArray())
            .andExpect(jsonPath("$.elements.length()").value(0));

    verify(listingService, times(1)).getRecommendedListings(eq(userId), eq(pageable));
  }

  /**
   * Tests the getRecommendedListings endpoint when the service throws a generic exception,
   * returning InternalServerError.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void getRecommendedListings_serviceError_returnsInternalServerError() throws Exception {
    Long userId = 123L;
    Pageable pageable = PageRequest.of(1, 20);

    when(listingService.getRecommendedListings(userId, pageable))
            .thenThrow(new RuntimeException("Error while fetching recommendations"));

    mockMvc.perform(get("/api/listing/user/recommended")
                    .param("userId", userId.toString()))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.elements").isArray())
            .andExpect(jsonPath("$.elements.length()").value(0));

    verify(listingService, times(1)).getRecommendedListings(userId, pageable);
  }

  /**
   * Tests the updateListing method with a valid request.
   * Expects a 200 OK response with a success message.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void updateListing_validRequest_returnsOkAndSuccessMessage() throws Exception {
    LocationDTO locationDTO = new LocationDTO(7050, "Trondheim", "Norway", 10.0, 10.0);
    ModifyListingRequest request = new ModifyListingRequest(
            "Updated Title", 2L, 150.0, "Updated brief", "Updated description",
            "fair", Collections.singletonList("image.jpg"), locationDTO
    );

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(tokenHeader)).thenReturn(extractedToken);
      doNothing().when(listingService).updateListing(eq(listing1.getId()),
              any(ModifyListingRequest.class), eq(extractedToken));

      mockMvc.perform(MockMvcRequestBuilders.put("/api/listing/" + listing1.getId())
                      .header("Authorization", tokenHeader)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Listing updated successfully"));
    }
  }

  /**
   * Tests the updateListing method with an invalid request.
   * Expects an Unauthorized response with an error message.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void updateListing_unauthorized_returnsUnauthorizedStatusAndMessage() throws Exception {
    LocationDTO locationDTO = new LocationDTO(7050, "Trondheim", "Norway", 10.0, 10.0);
    ModifyListingRequest request = new ModifyListingRequest(
            "Updated Title", 2L, 150.0, "Updated brief", "Updated description",
            "fair", Collections.singletonList("image.jpg"), locationDTO
    );

    try (MockedStatic<TokenExtractor> mockedTokenExtractor = mockStatic(TokenExtractor.class)) {
      mockedTokenExtractor.when(() -> TokenExtractor.extractToken(tokenHeader))
              .thenReturn(extractedToken);
      doThrow(new IllegalAccessException("User ID does not match token")).when(listingService)
              .updateListing(eq(listing1.getId()), any(ModifyListingRequest.class),
                      eq(extractedToken));

      mockMvc.perform(MockMvcRequestBuilders.put("/api/listing/" + listing1.getId())
                      .header("Authorization", tokenHeader)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(MockMvcResultMatchers.status().isUnauthorized())
              .andExpect(MockMvcResultMatchers.content().string("Unauthorized: User ID does not match token"));
    }
  }

  /**
   * Tests the updateListingStatus method with a valid request.
   * Expects a 200 OK response with a success message.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void updateListingStatus_success() throws Exception {
    Long listingIdToUpdate = listing1.getId();
    ListingStatusRequest newStatus = new ListingStatusRequest("SOLD");

    doNothing().when(listingService).updateListingStatus(listingIdToUpdate, newStatus, extractedToken);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/listing/" + listingIdToUpdate
                            + "/status", listingIdToUpdate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newStatus))
                    .header("Authorization", tokenHeader))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Listing status updated successfully"));

    verify(listingService, times(1)).updateListingStatus(listingIdToUpdate, newStatus, extractedToken);
  }

  /**
   * Tests the updateListingStatus method when the user is unauthorized.
   * Expects a 401 Unauthorized response with an error message.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  @WithMockUser("test")
  void updateListingStatus_unauthorized() throws Exception {
    Long listingIdToUpdate = listing1.getId();
    ListingStatusRequest newStatus = new ListingStatusRequest("SOLD");
    String errorMessage = "User is unauthorized to update this listing";


    doThrow(new IllegalAccessException(errorMessage)).when(listingService)
            .updateListingStatus(listingIdToUpdate, newStatus, extractedToken);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/listing/" +
                            listingIdToUpdate + "/status", listingIdToUpdate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newStatus))
                    .header("Authorization", tokenHeader))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.content().string("Unauthorized: " + errorMessage));

    verify(listingService, times(1)).updateListingStatus(listingIdToUpdate, newStatus, extractedToken);
  }

}
