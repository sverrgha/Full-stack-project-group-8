package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO class for the response containing multiple listings.
 * It contains a list of ShortListingResponse objects,
 * along with pagination information such as total elements,
 * total pages, current page, page size,
 * and flags indicating if it's the first or last page.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleListingsResponse {
  private List<ShortListingResponse> elements;
  private long totalElements;
  private int totalPages;
  private int currentPage;
  private int pageSize;
  private boolean isFirstPage;
  private boolean isLastPage;
}
