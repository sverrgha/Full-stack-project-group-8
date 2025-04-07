package ntnu.idatt2105.project.backend.service;

import ntnu.idatt2105.project.backend.repository.UserFavoritesRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    favoriteService.addListingAsFavorite(userId, listingId, token);
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
                    favoriteService.addListingAsFavorite(userId, listingId, token),
            "User ID does not match token"
    );
    verify(userFavoritesRepo, never()).save(anyLong(), anyLong());
  }

  /**
   * Test for removing a listing as a favorite with invalid input.
   * Verifies that no exception is not raised.
   */
  @Test
  void removeListingAsFavorite_validInput_deletes() {
    when(userService.validateUserIdMatchesToken(userId, token)).thenReturn(true);
    favoriteService.removeListingAsFavorite(userId, listingId, token);
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
                    favoriteService.removeListingAsFavorite(userId, listingId, token),
            "User ID does not match token"
    );
    verify(userFavoritesRepo, never()).deleteByUserIdAndListingId(anyLong(), anyLong());
  }
}
