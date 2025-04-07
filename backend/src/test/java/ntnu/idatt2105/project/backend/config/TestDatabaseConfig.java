package ntnu.idatt2105.project.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

/**
 * Test configuration class for setting up a MySQL database container for integration tests.
 * This class uses Testcontainers to create a MySQL container
 * and initializes it with a database schema and data.
 */
@TestConfiguration
public class TestDatabaseConfig {

  /**
   * Creates a MySQL container for integration tests.
   * The container is configured with a database name, username, and password.
   * @return The MySQL container instance.
   */
  @Bean
  public MySQLContainer<?> mySQLContainer() {
    MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("db/init.sql");

    mysql.start();
    return mysql;
  }
}
