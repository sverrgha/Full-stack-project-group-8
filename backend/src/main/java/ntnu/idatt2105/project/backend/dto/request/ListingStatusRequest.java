package ntnu.idatt2105.project.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListingStatusRequest {
  private String status;
}
