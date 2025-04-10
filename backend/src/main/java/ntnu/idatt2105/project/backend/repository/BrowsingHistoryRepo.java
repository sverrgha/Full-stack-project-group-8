package ntnu.idatt2105.project.backend.repository;

import ntnu.idatt2105.project.backend.model.CategoryShare;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BrowsingHistoryRepo {
  private final JdbcTemplate jdbcTemplate;

  public BrowsingHistoryRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This method retrieves the category shares for a specific user.
   * It calculates the percentage of views for each category based on the user's browsing history.
   *
   * @param userId The ID of the user
   * @return A list of CategoryShare objects containing category ID, view count, and percentage
   */
  public List<CategoryShare> getUsersCategoryShares(Long userId) {
    String categorySql = """
                SELECT l.category_id, COUNT(*) as view_count, 
                       COUNT(*) * 100.0 / (SELECT COUNT(*) FROM browsing_history WHERE user_id = ?) as percentage
                FROM browsing_history bh
                JOIN listings l ON bh.listing_id = l.id
                WHERE bh.user_id = ?
                GROUP BY l.category_id
                ORDER BY view_count DESC
            """;

    return jdbcTemplate.query(
            categorySql,
            (rs, rowNum) -> new CategoryShare(
                    rs.getLong("category_id"),
                    rs.getInt("view_count"),
                    rs.getDouble("percentage")
            ),
            userId, userId
    );
  }

  /**
   * This method adds a new browsing history entry to the database.
   * It takes the user ID and listing ID as parameters.
   *
   * @param userId    The ID of the user
   * @param listingId The ID of the listing
   */
  public void addBrowsingHistory(Long userId, Long listingId) {
    String sql = "INSERT INTO browsing_history (user_id, listing_id) VALUES (?, ?)";
    jdbcTemplate.update(sql, userId, listingId);
  }
}
