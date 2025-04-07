package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.dto.request.AddListingRequest;
import ntnu.idatt2105.project.backend.dto.request.ListingFilterRequest;
import ntnu.idatt2105.project.backend.dto.response.AddListingResponse;
import ntnu.idatt2105.project.backend.dto.response.FullListingResponse;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.dto.response.ShortListingResponse;
import ntnu.idatt2105.project.backend.model.Listing;
import ntnu.idatt2105.project.backend.model.Location;
import ntnu.idatt2105.project.backend.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    addListingRequest.setPostalCode(5678);
    addListingRequest.setImages(Collections.emptyList());

    validFilterRequest = new ListingFilterRequest();
    validFilterRequest.setCategoryId(1L);
    validFilterRequest.setCity("Test City");

    invalidFilterRequest = new ListingFilterRequest();
    invalidFilterRequest.setConditions(Collections.singletonList("INVALID"));
  }

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

  @Test
  @WithMockUser("test")
  void getListingById_invalidId_returnsNotFound() throws Exception {
    when(listingService.getListingById(99L)).thenThrow(new IllegalArgumentException("No listing found with ID: 99"));

    mockMvc.perform(get("/api/listing/99"))
            .andExpect(status().isNotFound());

    verify(listingService, times(1)).getListingById(99L);
  }


}
