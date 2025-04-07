package ntnu.idatt2105.project.backend.repository;

import ntnu.idatt2105.project.backend.model.Listing;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ListingRepo class is a repository for managing listings in the database.
 * It provides methods to save, delete, and retrieve listings based on various criteria.
 * It uses JdbcTemplate for database operations.
 */
@Repository
public class ListingRepo {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public ListingRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Saves a listing to the database.
   *
   * @param listing the listing to be saved
   * @return the saved listing
   */
  public Optional<Listing> save(String title, Long categoryId, Double price,
                                String briefDescription, String description,
                                Long userId, Listing.Condition condition, int postalCode) {
    String sql = "INSERT INTO sverrgha_datab.listings (title, category_id, price, brief_description, description, user_id, `condition`, postal_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, title);
      ps.setLong(2, categoryId);
      ps.setDouble(3, price);
      ps.setString(4, briefDescription);
      ps.setString(5, description);
      ps.setLong(6, userId);
      ps.setString(7, condition.toString().toLowerCase());
      ps.setInt(8, postalCode);
      return ps;
    }, keyHolder);

    Number generatedId = keyHolder.getKey();

    if (generatedId != null) {
      return getListingById(generatedId.longValue());
    } else {
      return Optional.empty();
    }
  }

  /**
   * Deletes a listing based on its ID.
   *
   * @param id
   */
  public void deleteById(Long id) {
    String sql = "DELETE FROM sverrgha_datab.listings WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  public void increaseViewsCount(Long id) {
    String sql = "UPDATE sverrgha_datab.listings SET views_count = views_count + 1 WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  /**
   * Retrieves a listing by its ID.
   *
   * @param id the ID of the listing to be retrieved
   * @return the listing with the specified ID, or null if not found
   */
  public Optional<Listing> getListingById(Long id) {
    String sql = "SELECT * FROM sverrgha_datab.listings WHERE id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> mapResultSetToListing(rs), id)
            .stream().findFirst();
  }

  /**
   * Retrieves all listings of given category id
   *
   * @param categoryId the ID of the category whose listings are to be retrieved
   * @return an array of the paths to the image as a String.
   */
  public Listing[] getAllListingsByCriteria(Long categoryId, String city, Double minPrice,
                                            Double maxPrice, List<String> conditions,
                                            Pageable pageable) {
    StringBuilder sql = new StringBuilder("SELECT l.* FROM sverrgha_datab.listings l");
    sql.append(" JOIN sverrgha_datab.locations loc ON l.postal_code = loc.postal_code WHERE l.status = 'active'"); // Assuming 'active' was a typo and should be 'AVAILABLE'
    List<Object> params = new ArrayList<>();

    if (categoryId != null) {
      sql.append(" AND l.category_id = ?");
      params.add(categoryId);
    }

    if (city != null && !city.isEmpty()) {
      sql.append(" AND loc.city = ?");
      params.add(city);
    }

    if (minPrice != null) {
      sql.append(" AND l.price >= ?");
      params.add(minPrice);
    }

    if (maxPrice != null) {
      sql.append(" AND l.price <= ?");
      params.add(maxPrice);
    }

    if (conditions != null && !conditions.isEmpty()) {
      sql.append(" AND ("); // Start of the OR group
      boolean firstCondition = true;
      for (String condition : conditions) {
        if (!EnumUtils.isValidEnumIgnoreCase(Listing.Condition.class, condition)) {
          throw new IllegalArgumentException("Invalid condition: " + condition);
        }
        if (!firstCondition) {
          sql.append(" OR ");
        }
        sql.append("l.condition = ?");
        params.add(condition.toLowerCase());
        firstCondition = false;
      }
      sql.append(")");
    }

    if (pageable.getSort().isSorted()) {
      sql.append(" ORDER BY ");
      List<Sort.Order> orderList = new ArrayList<>();
      pageable.getSort().forEach(orderList::add);
      for (int i = 0; i < orderList.size(); i++) {
        Sort.Order order = orderList.get(i);
        sql.append("l.").append(order.getProperty()).append(" ").append(order.getDirection().name());
        if (i < orderList.size() - 1) {
          sql.append(", ");
        }
      }
    }

    sql.append(" LIMIT ? OFFSET ?");
    params.add(pageable.getPageSize());
    params.add(pageable.getOffset());

    return jdbcTemplate.query(sql.toString(), (rs, rowNum) ->
                    mapResultSetToListing(rs), params.toArray())
            .toArray(Listing[]::new);
  }


  private Listing mapResultSetToListing(ResultSet rs) throws SQLException {
    return new Listing(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getLong("category_id"),
            rs.getDouble("price"),
            rs.getString("brief_description"),
            rs.getString("description"),
            rs.getLong("user_id"),
            Listing.Status.valueOf(rs.getString("status").toUpperCase()),
            Listing.Condition.valueOf(rs.getString("condition").toUpperCase()),
            rs.getDate("created_at"),
            rs.getLong("reserved_by_user_id"),
            rs.getDate("reserved_at"),
            rs.getLong("sold_to_user_id"),
            rs.getDate("sold_at"),
            rs.getInt("postal_code"),
            rs.getInt("views_count")
    );
  }
}
