package ntnu.idatt2105.project.backend.repository;

import ntnu.idatt2105.project.backend.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepo {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public CategoryRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Category getCategoryById(Long id) {
    String sql = "SELECT * FROM categories WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
            new Category(
                    rs.getLong("id"),
                    rs.getString("name_en"),
                    rs.getString("name_no")
            ), id);
  }

  public Category getCategoryByName(String name) {
    String sql = "SELECT * FROM categories WHERE name_en = ? OR name_no = ?";
    return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
            new Category(
                    rs.getLong("id"),
                    rs.getString("name_en"),
                    rs.getString("name_no")
            ), name, name);
  }

  public List<Category> getAllCategories() {
    String sql = "SELECT * FROM categories";
    return jdbcTemplate.query(sql, (rs, rowNum) ->
            new Category(
                    rs.getLong("id"),
                    rs.getString("name_en"),
                    rs.getString("name_no")
            ));
  }

  public void addCategory(String nameEn, String nameNo) {
    String sql = "INSERT INTO categories (name_en, name_no) VALUES (?, ?)";
    jdbcTemplate.update(sql, nameEn, nameNo);
  }

  public void deleteCategoryById(Long id) {
    String sql = "DELETE FROM categories WHERE id = ?";
    jdbcTemplate.update(sql, id);
  }

}
