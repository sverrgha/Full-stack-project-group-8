package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntnu.idatt2105.project.backend.model.Listing;

import java.sql.Date;
import java.util.List;

/**
 * DTO class for the full response of a listing.
 * It contains all the necessary fields required to represent a listing.
 * The fields are populated from the Listing model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullListingResponse {
    private Long id;
    private String title;
    private Long categoryId;
    private Double price;
    private String briefDescription;
    private String description;
    private Long userId;
    private String status;
    private String condition;
    private Date createdAt;
    private Long reservedByUserId;
    private Date reservedAt;
    private Date soldAt;
    private Long soldToUserId;
    private Integer postalCode;
    private int viewsCount;
    private List<String> images;
    private String city;
    private String isFavorite;
}
