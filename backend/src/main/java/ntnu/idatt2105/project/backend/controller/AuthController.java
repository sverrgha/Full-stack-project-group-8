package ntnu.idatt2105.project.backend.controller;

import jakarta.validation.Valid;
import ntnu.idatt2105.project.backend.dto.request.LoginRequest;
import ntnu.idatt2105.project.backend.dto.request.RegisterRequest;
import ntnu.idatt2105.project.backend.dto.response.LoginResponse;
import ntnu.idatt2105.project.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private UserService userService;
  private static final Logger logger = Logger.getLogger(AuthController.class.getName());

  /**
   * Endpoint for user registration.
   * This method tries to register a new user by passing it to the userService.
   * If the registration is successful, it returns a 201 Created response with
   * the login response. If the registration fails, it returns a 400 Bad Request response.
   *
   * @param request the registration request containing user details
   * @return ResponseEntity with login response containing email, message and token
   */
  @PostMapping("/register")
  public ResponseEntity<LoginResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
    logger.info("Received register request for user: " + request.getEmail());
    try {
      LoginResponse response = userService.registerUser(request);

      if (response.getToken().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }
      logger.info("User registered successfully: " + request.getEmail());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
      logger.warning("Error registering user: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
              new LoginResponse(request.getEmail(), "Error registering user: " + e.getMessage(), null));
    }
  }

  /**
   * Endpoint for user login.
   * This method tries to log in a user by passing the login request to the userService.
   * If the login is successful, it returns a 200 OK response with the login response.
   * If the login fails, it returns a 401 Unauthorized response.
   * If an error occurs, it returns a 500 Internal Server Error response.
   *
   * @param request the login request containing user email and password
   * @return ResponseEntity with login response containing email, message and token
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
    logger.info("Received login request for user: " + request.getEmail());
    try {
      LoginResponse response = userService.loginUser(request);

      if (response.getToken().isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
      }
      logger.info("User logged in successfully: " + request.getEmail());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      logger.warning("Error logging in user: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
              new LoginResponse(request.getEmail(), "Error logging in user: " + e.getMessage(), null));
    }
  }
}
