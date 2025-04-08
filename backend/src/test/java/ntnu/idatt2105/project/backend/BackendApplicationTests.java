package ntnu.idatt2105.project.backend;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {

	@Container
	private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
					.withDatabaseName("testdb")
					.withUsername("test")
					.withPassword("test");

	@BeforeAll
	static void beforeAll() {
		mysql.start();
		System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
		System.setProperty("spring.datasource.username", mysql.getUsername());
		System.setProperty("spring.datasource.password", mysql.getPassword());
	}

}
