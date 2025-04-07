package ntnu.idatt2105.project.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing listing images in the database.
 * It provides methods to save, delete, and retrieve images associated with listings.
 */
@Repository
public class ListingImageRepo {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public ListingImageRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Deletes all images associated with a given listing ID.
   *
   * @param listingId the ID of the listing whose images are to be deleted
   */
  public void deleteAllImagesByListingId(long listingId) {
    String sql = "DELETE FROM sverrgha_datab.listing_images WHERE listing_id = ?";
    jdbcTemplate.update(sql, listingId);
  }

  /**
   * Gets the path to the image associated with a given listing ID.
   *
   * @param listingId the ID of the listing whose image path is to be retrieved
   * @return a list of the paths to the image as a String.
   */
  public Optional<String> getOneImageByListingId(long listingId) {
    String sql = "SELECT path_to_image FROM sverrgha_datab.listing_images "
    + "WHERE listing_id = ? LIMIT 1";
    return Optional.of(jdbcTemplate.queryForObject(sql, String.class, listingId));
  }

  public List<String> getAllImagesByListingId(long listingId) {
    String sql = "SELECT path_to_image FROM sverrgha_datab.listing_images "
    + "WHERE listing_id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("path_to_image"), listingId);
  }

  public void save(long listingId, String imagePath) {
    String sql = "INSERT INTO sverrgha_datab.listing_images (listing_id, path_to_image) VALUES (?, ?)";
    jdbcTemplate.update(sql, listingId, imagePath);
  }
}
