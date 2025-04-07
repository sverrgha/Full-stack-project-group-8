package ntnu.idatt2105.project.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration
public class TestDatabaseConfig {

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
