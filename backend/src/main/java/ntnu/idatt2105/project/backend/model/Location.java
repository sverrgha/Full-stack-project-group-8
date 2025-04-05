package ntnu.idatt2105.project.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.locationtech.jts.geom.Point;

/**
 * Location class represents a geographical location with postal code, city, country,
 * and geographical coordinates.
 * It uses the JTS (Java Topology Suite) library to represent the geographical point.
 */
@Data
@AllArgsConstructor
public class Location {
  /**
   * The postal code of the location.
   */
  int postalCode;

  /**
   * The city of the location.
   */
  String city;

  /**
   * The country of the location.
   */
  String country;

  /**
   * The geographical coordinates of the location represented as a Point.
   */
  Point geo;
}
