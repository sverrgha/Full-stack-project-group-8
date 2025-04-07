package ntnu.idatt2105.project.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Location class represents a geographical location with postal code, city, country,
 * and geographical coordinates.
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
  /**
   * The postal code of the location.
   */
  private int postalCode;

  /**
   * The city of the location.
   */
  private String city;

  /**
   * The country of the location.
   */
  private String country;

  /**
   * The latitude of the location.
   */
  private Double latitude;

  /**
   * The longitude of the location.
   */
  private Double longitude;

}
