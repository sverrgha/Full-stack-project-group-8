package ntnu.idatt2105.project.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ListingImage class represents an image associated with a listing.
 * It contains the ID of the image, the path to the image file, and the ID of the listing it belongs to.
 */
@Data
public class ListingImage {

  /**
   * The ID of the image.
   * This is a unique identifier for the image in the database.
   */
  private long id;

  /**
   * The path to the image file.
   * This is a string that represents the location of the image file in the storage.
   */
  private String pathToImage;

  /**
   * The ID of the listing that this image belongs to.
   * This is a foreign key reference to the Listing table.
   */
  private long listingId;

  /**
   * Constructor for ListingImage.
   *
   * @param pathToImage the path to the image file
   * @param listingId   the ID of the listing that this image belongs to
   */
  public ListingImage(String pathToImage, long listingId) {
    this.pathToImage = pathToImage;
    this.listingId = listingId;
  }
}
