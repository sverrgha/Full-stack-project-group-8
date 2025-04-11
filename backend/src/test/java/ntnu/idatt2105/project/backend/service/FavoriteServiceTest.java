package ntnu.idatt2105.project.backend.service;

import ntnu.idatt2105.project.backend.dto.request.FavoriteRequest;
import ntnu.idatt2105.project.backend.dto.response.MultipleListingsResponse;
import ntnu.idatt2105.project.backend.dto.response.ShortListingResponse;
import ntnu.idatt2105.project.backend.model.Listing;
import ntnu.idatt2105.project.backend.repository.UserFavoritesRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Test class for FavoriteService.
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {
  @Mock
  private UserFavoritesRepo userFavoritesRepo;

  @Mock
  private ListingService listingService;

  @Mock
  private UserService userService;

  @InjectMocks
  private FavoriteService favoriteService;

  private static Long userId;
  private static Long listingId;
  private static String token;

  /**
   * Set up the test class with common parameters, to be used in all tests.
   */
  @BeforeAll
  static void setUp() {
    userId = 1L;
    listingId = 1L;
    token = "validToken";
  }

  /**
   * Test for adding a listing as a favorite with valid input.
   * Verifies that no exception is raised.
   */
  @Test
  void addListingAsFavorite_validInput_saves() {
    when(listingService.listingExists(listingId)).thenReturn(true);
    when(userService.userExists(userId)).thenReturn(true);
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    favoriteService.addListingAsFavorite(new FavoriteRequest(
            userId, listingId
    ), token);
    verify(userFavoritesRepo, times(1)).save(userId, listingId);
  }

  /**
   * Test for adding a listing as a favorite with invalid input.
   * Verifies that an IllegalArgumentException is raised.
   * And correct error message is returned.
   */
  @Test
  void addListingAsFavorite_userIdMismatch_throws() {
    when(listingService.listingExists(listingId)).thenReturn(true);
    when(userService.userExists(userId)).thenReturn(true);
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(false);
    assertThrows(IllegalArgumentException.class, () ->
                    favoriteService.addListingAsFavorite(new FavoriteRequest(
                            userId, listingId
                    ), token),
            "User ID does not match token"
    );
    verify(userFavoritesRepo, never()).save(anyLong(), anyLong());
  }

  /**
   * Test for removing a listing as a favorite with invalid input.
   * Verifies that no exception is raised.
   */
  @Test
  void removeListingAsFavorite_validInput_deletes() {
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    when(userFavoritesRepo.existsByUserIdAndListingId(userId, listingId)).thenReturn(true);
    favoriteService.removeListingAsFavorite(new FavoriteRequest(
            userId, listingId
    ), token);
    verify(userFavoritesRepo, times(1)).deleteByUserIdAndListingId(userId, listingId);
  }

  /**
   * Test for removing a listing as a favorite with invalid input.
   * Verifies that an IllegalArgumentException is raised.
   */
  @Test
  void removeListingAsFavorite_userIdMismatch_throws() {
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(false);
    assertThrows(IllegalArgumentException.class, () ->
                    favoriteService.removeListingAsFavorite(new FavoriteRequest(
                            userId, listingId
                    ), token),
            "User ID does not match token"
    );
    verify(userFavoritesRepo, never()).deleteByUserIdAndListingId(anyLong(), anyLong());
  }

  /**
   * Test for removing a listing as a favorite with invalid input.
   * Verifies that an IllegalArgumentException is raised.
   */
  @Test
  void getAllFavorites_validInput_returnsMultipleListingsResponse_verifyingResponseStructure() {
    Pageable pageable = PageRequest.of(0, 10);
    List<Long> favoriteIds = Arrays.asList(101L, 102L);
    List<Listing> listings = Arrays.asList(new Listing(), new Listing());
    Page<Listing> listingsPage = new PageImpl<>(listings, pageable, favoriteIds.size());

    MultipleListingsResponse mockedListingServiceResponse = new MultipleListingsResponse(
            Collections.singletonList(new ShortListingResponse()),
            listingsPage.getTotalElements(),
            listingsPage.getTotalPages(),
            listingsPage.getNumber() + 1,
            listingsPage.getSize(),
            listingsPage.isFirst(),
            listingsPage.isLast()
    );

    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    when(userFavoritesRepo.getFavoriteIdsByUserId(userId)).thenReturn(favoriteIds);
    when(listingService.getMultipleListingsById(favoriteIds, pageable)).thenReturn(mockedListingServiceResponse);

    MultipleListingsResponse actualResponse = favoriteService.getAllFavorites(userId, pageable, token);

    assertNotNull(actualResponse);
    assertInstanceOf(ShortListingResponse.class, actualResponse.getElements().get(0));
    assertEquals(listingsPage.getTotalElements(), actualResponse.getTotalElements());
    assertEquals(listingsPage.getTotalPages(), actualResponse.getTotalPages());
    assertEquals(listingsPage.getNumber() + 1, actualResponse.getCurrentPage());
    assertEquals(listingsPage.getSize(), actualResponse.getPageSize());
    assertEquals(listingsPage.isFirst(), actualResponse.isFirstPage());
    assertEquals(listingsPage.isLast(), actualResponse.isLastPage());
    assertNotNull(actualResponse.getElements());

    verify(userService, times(1)).validateUserIdMatchesToken(userId, token);
    verify(userFavoritesRepo, times(1)).getFavoriteIdsByUserId(userId);
    verify(listingService, times(1)).getMultipleListingsById(favoriteIds, pageable);
  }

  /**
   * Test for getting all favorites with invalid user ID.
   * Verifies that an IllegalArgumentException is raised.
   */
  @Test
  void getAllFavorites_invalidUserId_throwsIllegalArgumentException() {
    Pageable pageable = PageRequest.of(0, 10);
    long invalidUserId = 0L;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> favoriteService.getAllFavorites(invalidUserId, pageable, token));
    assertEquals("User ID must be positive", exception.getMessage());

    verifyNoInteractions(userService);
    verifyNoInteractions(userFavoritesRepo);
    verifyNoInteractions(listingService);
  }

  /**
   * Test for getting all favorites with user ID that does not match the token.
   * Verifies that an IllegalArgumentException is raised.
   */
  @Test
  void getAllFavorites_userIdDoesNotMatchToken_throwsIllegalArgumentException() {
    Pageable pageable = PageRequest.of(0, 10);
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> favoriteService.getAllFavorites(userId, pageable, token));
    assertEquals("User ID does not match token", exception.getMessage());

    verify(userService, times(1)).validateUserIdMatchesToken(userId, token);
    verifyNoInteractions(userFavoritesRepo);
    verifyNoInteractions(listingService);
  }

  /**
   * Test for getting all favorites with user ID that does not exist.
   * Verifies that an IllegalArgumentException is raised.
   */
  @Test
  void getAllFavorites_noFavorites_returnsEmptyMultipleListingsResponse() {
    Pageable pageable = PageRequest.of(0, 10);
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    when(userFavoritesRepo.getFavoriteIdsByUserId(userId)).thenReturn(Collections.emptyList());
    MultipleListingsResponse expectedResponse = new MultipleListingsResponse(
            Collections.emptyList(), 0, 0, 1, 10, true, true
    );
    when(listingService.getMultipleListingsById(anyList(), any()))
            .thenReturn(expectedResponse);

    MultipleListingsResponse actualResponse = favoriteService.getAllFavorites(userId, pageable, token);

    assertEquals(Collections.emptyList(), actualResponse.getElements());
    assertEquals(expectedResponse.getTotalElements(), actualResponse.getTotalElements());
    assertEquals(expectedResponse.getTotalPages(), actualResponse.getTotalPages());
    assertEquals(expectedResponse.getCurrentPage(), actualResponse.getCurrentPage());
    assertEquals(expectedResponse.getPageSize(), actualResponse.getPageSize());
    assertEquals(expectedResponse.isFirstPage(), actualResponse.isFirstPage());
    assertEquals(expectedResponse.isLastPage(), actualResponse.isLastPage());

    verify(userService, times(1)).validateUserIdMatchesToken(userId, token);
    verify(userFavoritesRepo, times(1)).getFavoriteIdsByUserId(userId);
    verify(listingService, times(1)).getMultipleListingsById(anyList(), any());
  }

  /**
   * Test for getting all favorites with empty favorite IDs.
   * Verifies that an empty MultipleListingsResponse is returned.
   */
  @Test
  void getAllFavorites_emptyFavoriteIds_returnsEmptyMultipleListingsResponse() {
    Pageable pageable = PageRequest.of(0, 10);
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    when(userFavoritesRepo.getFavoriteIdsByUserId(userId)).thenReturn(Collections.emptyList());
    MultipleListingsResponse expectedResponse = new MultipleListingsResponse(
            Collections.emptyList(), 0, 0, 1, 10, true, true
    );
    when(listingService.getMultipleListingsById(anyList(), any()))
            .thenReturn(expectedResponse);

    MultipleListingsResponse actualResponse = favoriteService.getAllFavorites(userId, pageable, token);

    assertEquals(expectedResponse, actualResponse);
    verify(userService, times(1)).validateUserIdMatchesToken(userId, token);
    verify(userFavoritesRepo, times(1)).getFavoriteIdsByUserId(userId);
    verify(listingService, times(1)).getMultipleListingsById(anyList(), any());
  }

  /**
   * Test for getting all favorites when the listing service returns null.
   * Verifies that null is returned.
   */
  @Test
  void getAllFavorites_listingServiceReturnsNull_returnsNull() {
    Pageable pageable = PageRequest.of(0, 10);
    List<Long> favoriteIds = Arrays.asList(101L, 102L);

    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    when(userFavoritesRepo.getFavoriteIdsByUserId(userId)).thenReturn(favoriteIds);
    when(listingService.getMultipleListingsById(favoriteIds, pageable)).thenReturn(null);

    MultipleListingsResponse actualResponse = favoriteService.getAllFavorites(userId, pageable, token);

    assertNull(actualResponse);
    verify(userService, times(1)).validateUserIdMatchesToken(userId, token);
    verify(userFavoritesRepo, times(1)).getFavoriteIdsByUserId(userId);
    verify(listingService, times(1)).getMultipleListingsById(favoriteIds, pageable);
  }
}
