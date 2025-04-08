package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for the response after adding a new listing.
 * It contains the ID of the newly created listing (null if unsuccessful)
 * and a message indicating the result of the operation.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddListingResponse {
  private Long id;
  private String message;
}
