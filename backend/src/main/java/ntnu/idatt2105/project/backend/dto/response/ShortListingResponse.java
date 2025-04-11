package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.LocationDTO;

/**
 * DTO class for the short response of a listing.
 * It contains a subset of fields from the FullListingResponse class,
 * which are used to represent a listing in a more compact form.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortListingResponse {
  private Long id;
  private String title;
  private Double price;
  private String briefDescription;
  private String pathToImage;
  private String condition;
  private LocationDTO location;
}
