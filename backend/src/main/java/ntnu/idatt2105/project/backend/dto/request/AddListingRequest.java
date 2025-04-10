package ntnu.idatt2105.project.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * DTO class for adding a new listing.
 * It contains all the necessary fields required to create a new listing.
 * The fields are validated using annotations from the Jakarta Bean Validation API.
 */
@Data
public class AddListingRequest {
  @NotBlank(message = "title is required")
  private String title;

  @NotNull(message = "categoryId is required")
  private Long categoryId;

  @NotNull(message = "price is required")
  private double price;

  private String briefDescription;

  private String description;

  @NotNull(message = "userId is required")
  private Long userId;

  @NotBlank(message = "condition is required")
  private String condition;

  private List<String> images;

  @NotNull
  private LocationDTO location;
}
