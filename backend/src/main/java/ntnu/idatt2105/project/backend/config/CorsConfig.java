package ntnu.idatt2105.project.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures CORS (Cross-Origin Resource Sharing) for the application.
 * This configuration allows cross-origin requests from the specified origin and
 * defines the allowed methods and headers.
 * <p>
 * It enables CORS globally for all endpoints ("/**") in the application, allowing
 * the frontend to access the backend API from another origin.
 * </p>
 */
@Configuration
public class CorsConfig {

  /**
   * Configures CORS for the application.
   * <p>
   * The configuration allows cross-origin requests from the specified origin
   * and defines the allowed methods (GET, POST, PUT, DELETE and OPTIONS) and
   * allows all headers. The preflight request is cached for 3600 seconds (1 hour)
   * to reduce the number of requests.
   * </p>
   *
   * @return a WebMvcConfigurer object with the CORS configuration.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        // Enable CORS for all endpoints
        registry.addMapping("/**")
                // Allow requests from the frontend origin
                .allowedOrigins("http://localhost:5173")
                // Define allowed methods
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                // Allow all headers
                .allowedHeaders("*")
                // Allow credentials (cookies, authorization headers)
                .allowCredentials(true)
                // Cache preflight request for 1 hour
                .maxAge(3600);
      }
    };
  }
}
