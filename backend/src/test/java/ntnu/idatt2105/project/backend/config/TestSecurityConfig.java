package ntnu.idatt2105.project.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test configuration class for security settings.
 * This class is used to disable security for integration tests.
 * It allows all requests without authentication or authorization.
 * This is useful for testing purposes where security is not a concern.
 */
@TestConfiguration
public class TestSecurityConfig {
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
            .authorizeRequests().anyRequest().permitAll();
    return http.build();
  }
}
