package ntnu.idatt2105.project.backend.repository;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ntnu.idatt2105.project.backend.model.User;


/*
 * This interface extends the CrudRepository interface to provide CRUD operations for the User entity.
 * It defines methods to find users by username and email, and to delete a user by ID.
 * The interface is annotated with @Repository to indicate that it is a Spring Data repository.
 */
@Repository
public class UserRepo {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public UserRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<User> findByEmail(String email) {
    String sql = "SELECT * FROM sverrgha_datab.users WHERE email = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      User user = new User();
      user.setId(rs.getLong("id"));
      user.setFirstname(rs.getString("first_name"));
      user.setLastname(rs.getString("last_name"));
      user.setEmail(rs.getString("email"));
      user.setPhoneNumber(rs.getString("phone_number"));
      user.setPassword(rs.getString("password"));
      user.setAdmin(rs.getBoolean("admin"));
      return user;
    }, email).stream().findFirst();
  }

  public Optional<User> findById(Long id) {
    String sql = "SELECT * FROM sverrgha_datab.users WHERE id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      User user = new User();
      user.setId(rs.getLong("id"));
      user.setFirstname(rs.getString("first_name"));
      user.setLastname(rs.getString("last_name"));
      user.setEmail(rs.getString("email"));
      user.setPhoneNumber(rs.getString("phone_number"));
      user.setPassword(rs.getString("password"));
      user.setAdmin(rs.getBoolean("admin"));
      return user;
    }, id).stream().findFirst();
  }

  public void deleteById(Long id) {
    String sql = "DELETE FROM sverrgha_datab.users WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

  public Optional<User> save(User user) {
    String sql = "INSERT INTO sverrgha_datab.users (first_name, last_name, email, phone_number, password, admin) VALUES (?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, user.getFirstname(), user.getLastname(), user.getEmail(), user.getPhoneNumber(), user.getPassword(), user.isAdmin());

    return findByEmail(user.getEmail());
  }

  public void update(User user) {
    String sql = "UPDATE sverrgha_datab.users SET first_name = ?, last_name = ?, email = ?, phone_number = ?, password = ?, admin = ? WHERE id = ?";
    jdbcTemplate.update(sql, user.getFirstname(), user.getLastname(), user.getEmail(), user.getPhoneNumber(), user.getPassword(), user.isAdmin(), user.getId());
  }
}