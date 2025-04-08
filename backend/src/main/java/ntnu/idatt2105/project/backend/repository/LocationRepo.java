package ntnu.idatt2105.project.backend.repository;

import ntnu.idatt2105.project.backend.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * LocationRepo class is a repository for managing locations in the database.
 * It provides methods to save and retrieve locations based on postal code.
 */
@Repository
public class LocationRepo {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public LocationRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Saves a location to the database.
   *
   * @param location the location to be saved
   */
  public void saveLocation(Location location) {
    String sql = "INSERT INTO sverrgha_datab.locations (postal_code, city, country, "
            + "latitude, longitude) VALUES (?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql,
            location.getPostalCode(),
            location.getCity(),
            location.getCountry(),
            location.getLatitude(),
            location.getLongitude()
    );
  }

  /**
   * Retrieves a location from the database based on the postal code.
   *
   * @param postalCode the postal code of the location to be retrieved
   * @return an Optional containing the location if found, or empty if not found
   */
  public Optional<Location> getLocationByPostalCode(int postalCode) {
    String sql = "SELECT * FROM sverrgha_datab.locations WHERE postal_code = ?";
    return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Location(
            rs.getInt("postal_code"),
            rs.getString("city"),
            rs.getString("country"),
            rs.getDouble("latitude"),
            rs.getDouble("longitude")
    ), postalCode));
  }
}
