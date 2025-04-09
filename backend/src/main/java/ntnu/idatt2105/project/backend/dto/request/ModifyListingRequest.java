package ntnu.idatt2105.project.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ntnu.idatt2105.project.backend.model.Listing;

import java.sql.Date;
import java.util.List;

/**
 * ModifyListingRequest is a Data Transfer Object (DTO) that represents a request to modify
 * a listing.
 * It contains fields for the listing's title, category ID, price, brief description,
 * description, user ID, status, condition, reserved user ID, reserved date, sold date,
 * sold user ID, images, and location.
 */
@Data
public class ModifyListingRequest {
  @NotBlank(message = "Title cannot be blank")
  private String title;

  @NotNull(message = "Category cannot be null")
  private Long categoryId;

  @NotNull(message = "Price cannot be null")
  private Double price;

  private String briefDescription;
  private String description;

  @NotNull(message = "Condition cannot be null")
  private String condition;

  private List<String> images;

  private LocationDTO location;
}
