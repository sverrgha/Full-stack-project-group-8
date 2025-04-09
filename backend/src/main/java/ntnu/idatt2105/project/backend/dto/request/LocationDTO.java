package ntnu.idatt2105.project.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LocationDTO is a Data Transfer Object (DTO) that represents a location with city, country,
 * latitude, and longitude. It is used in requests that needs location data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
  @NotNull
  @Size(min = 1, max = 9999, message = "Postal code must be a number in range 1 to 9999")
  private int postalCode;

  @NotBlank(message = "City cannot be blank")
  private String city;

  @NotBlank(message = "Country cannot be blank")
  private String country;

  @NotNull(message = "Latitude cannot be null")
  private Double latitude;
  @NotNull(message = "Longitude cannot be null")
  private Double longitude;
}
