package ntnu.idatt2105.project.backend.repository;

import ntnu.idatt2105.project.backend.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This class is a repository for managing categories in the database.
 * It provides methods to get, add, and delete categories.
 * It uses JdbcTemplate to interact with the database.
 */
@Repository
public class CategoryRepo {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Constructor for CategoryRepo.
   *
   * @param jdbcTemplate The JdbcTemplate used to interact with the database.
   */
  @Autowired
  public CategoryRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This method retrieves a category by its ID.
   *
   * @param id The ID of the category to retrieve.
   * @return The Category object with the specified ID.
   */
  public Category getCategoryById(Long id) {
    String sql = "SELECT * FROM categories WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
            new Category(
                    rs.getLong("id"),
                    rs.getString("name_en"),
                    rs.getString("name_no"),
                    rs.getString("url")
            ), id);
  }

  /**
   * This method retrieves a category by its name.
   * It searches for the category in both English and Norwegian names.
   *
   * @param name The name of the category to retrieve.
   * @return The Category object with the specified name.
   */
  public Category getCategoryByName(String name) {
    String sql = "SELECT * FROM categories WHERE name_en = ? OR name_no = ?";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
            new Category(
                    rs.getLong("id"),
                    rs.getString("name_en"),
                    rs.getString("name_no"),
                    rs.getString("url")
            ), name, name);
  }

  /**
   * This method retrieves all categories from the database.
   *
   * @return A list of Category objects representing all categories.
   */
  public List<Category> getAllCategories() {
    String sql = "SELECT * FROM categories";
    return jdbcTemplate.query(sql, (rs, rowNum) ->
            new Category(
                    rs.getLong("id"),
                    rs.getString("name_en"),
                    rs.getString("name_no"),
                    rs.getString("url")
            ));
  }

  /**
   * This method adds a new category to the database.
   * It takes the English and Norwegian names of the category as parameters.
   *
   * @param nameEn The English name of the category.
   * @param nameNo The Norwegian name of the category.
   */
  public void addCategory(String nameEn, String nameNo) {
    String sql = "INSERT INTO categories (name_en, name_no) VALUES (?, ?)";
    jdbcTemplate.update(sql, nameEn, nameNo);
  }

  /**
   * This method deletes a category from the database by its ID.
   *
   * @param id The ID of the category to delete.
   */
  public void deleteCategoryById(Long id) {
    String sql = "DELETE FROM categories WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

}
