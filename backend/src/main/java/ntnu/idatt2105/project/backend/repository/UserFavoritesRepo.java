package ntnu.idatt2105.project.backend.repository;

import ntnu.idatt2105.project.backend.model.UserFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

@Repository
public class UserFavoritesRepo {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public UserFavoritesRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void save(long userId, long listingId) {
    String sql = "INSERT INTO sverrgha_datab.user_favorites (user_id, listing_id) VALUES (?, ?)";
    jdbcTemplate.update(sql, userId, listingId);
  }

  public List<UserFavorite> getByUserId(long userId) {
    String sql = "SELECT * FROM sverrgha_datab.user_favorites WHERE user_id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> new UserFavorite(
            rs.getLong("user_id"),
            rs.getLong("listing_id")
    ), userId);
  }

  public void deleteByUserIdAndListingId(long userId, long listingId) {
    String sql = "DELETE FROM sverrgha_datab.user_favorites WHERE user_id = ? AND listing_id = ?";
    jdbcTemplate.update(sql, userId, listingId);
  }
}
