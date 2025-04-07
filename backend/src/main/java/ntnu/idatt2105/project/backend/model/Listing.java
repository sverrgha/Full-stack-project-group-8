package ntnu.idatt2105.project.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * Listing class represents a listing in the system.
 * It contains various attributes such as title, price, description,
 * status, condition, and user ID.
 * It also includes information about the listing's creation date,
 * reservation, sale, and views count.
 * The class uses Lombok annotations to generate boilerplate code
 * such as getters, setters, and toString methods.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Listing {
  /**
   * The status of the listing.
   * It can be one of the following:
   * - ACTIVE: The listing is currently active and available for sale.
   * - SOLD: The listing has been sold.
   * - RESERVED: The listing is reserved by a user.
   * - ARCHIVED: The listing has been archived and is no Longer active.
   */
  public enum Status {
    ACTIVE,
    SOLD,
    RESERVED,
    ARCHIVED
  }

  /**
   * The condition of the listing.
   * It can be one of the following:
   * - NEW: The item is brand new and unused.
   * - LIKE_NEW: The item is in excellent condition, almost new.
   * - GOOD: The item is in good condition with minor wear and tear.
   * - FAIR: The item is in fair condition with noticeable wear and tear.
   * - POOR: The item is in poor condition and may not be fully functional.
   */
  public enum Condition {
    NEW,
    LIKE_NEW,
    GOOD,
    FAIR,
    POOR
  }

  /**
   * The ID of the listing.
   * This is a unique identifier for the listing in the database.
   */
  private Long id;

  /**
   * The title of the listing.
   */
  private String title;

  /**
   * The ID of the category to which the listing belongs.
   * This is a foreign key reference to the Category table.
   */
  private Long categoryId;

  /**
   * The price of the listing.
   * This is a double value representing the cost of the item.
   */
  private double price;

  /**
   * The brief description of the listing.
   * This is a short summary of the item being sold.
   */
  private String briefDescription;

  /**
   * The detailed description of the listing.
   * This is a more comprehensive explanation of the item being sold.
   */
  private String description;

  /**
   * The ID of the user who created the listing.
   * This is a foreign key reference to the User table.
   */
  private Long userId;

  /**
   * The status of the listing.
   * This indicates whether the listing is active, sold, reserved, or archived.
   */
  private Status status;

  /**
   * The condition of the listing.
   * This indicates the physical state of the item being sold.
   */
  private Condition condition;

  /**
   * The date when the listing was created.
   * This is a timestamp indicating when the listing was added to the system.
   */
  private Date createdAt;

  /**
   * The id of the user who reserved the listing.
   * This is a foreign key reference to the User table.
   * It is set to null if the listing is not reserved.
   */
  private Long reservedByUserId;

  /**
   * The date when the listing was reserved.
   * This is a timestamp indicating when the listing was put on hold for a user.
   * It is set to null if the listing is not reserved.
   */
  private Date reservedAt;

  /**
   * The ID of the user who bought the listing.
   * This is a foreign key reference to the User table.
   * It is set to null if the listing is not sold.
   */
  private Long soldToUserId;

  /**
   * The date when the listing was sold.
   * This is a timestamp indicating when the listing was purchased.
   * It is set to null if the listing is not sold.
   */
  private Date soldAt;

  /**
   * The postal code of the listing.
   * This is used to indicate the location of the item being sold.
   */
  private int postalCode;

  /**
   * The amount of views the listing has received.
   * This is used to track the popularity of the listing.
   */
  private int viewsCount;

}
