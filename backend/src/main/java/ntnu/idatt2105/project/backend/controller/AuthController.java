package ntnu.idatt2105.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ntnu.idatt2105.project.backend.dto.request.LoginRequest;
import ntnu.idatt2105.project.backend.dto.request.RegisterRequest;
import ntnu.idatt2105.project.backend.dto.response.AuthResponse;
import ntnu.idatt2105.project.backend.enums.AuthResponseMessage;
import ntnu.idatt2105.project.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger;

/**
 * AuthController handles authentication-related requests such as user registration and login.
 * It uses the UserService to perform the actual operations and returns appropriate responses.
 * All requests are returning a AuthResponse object containing the email, a message, token and user ID.
 */
@Tag(name = "Authentication", description = "Endpoint for user registration, login and " +
        "refreshing JWT token")
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
   * @return ResponseEntity with login response containing email, message, token and user ID
   */
  @Operation(
          summary = "Register a new user",
          description = "This endpoint allows a new user to register by providing their " +
                  "email, password, firstname, lastname and phone number.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "User registration details",
                  required = true,
                  content = @Content(schema = @Schema(implementation = RegisterRequest.class))
          )
  )
  @ApiResponse(
          responseCode = "201",
          description = "User registered successfully",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(
          responseCode = "400",
          description = "User registration failed, due to invalid input or existing user",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(
          responseCode = "500",
          description = "Internal server error, unable to register user",
          content = @Content(mediaType = "application/json", schema =
          @Schema(implementation = AuthResponse.class)))
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
    logger.info("Received register request for user: " + request.getEmail());
    try {
      AuthResponse response = userService.registerUser(request);

      if (response.getToken() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }
      logger.info("User registered successfully: " + request.getEmail());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
      logger.warning("Error registering user: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
              new AuthResponse(request.getEmail(),
                      AuthResponseMessage.SAVING_USER_ERROR.getMessage()
                              + e.getMessage(), null, null, null));
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
   * @return ResponseEntity with login response containing email, message, token and user ID
   */
  @Operation(
          summary = "Log in an existing user",
          description = "Authenticates a user and returns authentication details.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "User login details",
                  required = true,
                  content = @Content(schema = @Schema(implementation = LoginRequest.class))
          )
  )
  @ApiResponse(responseCode = "200", description = "User logged in successfully",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(responseCode = "401", description = "Invalid login credentials",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal server error during login",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
    logger.info("Received login request for user: " + request.getEmail());
    try {
      AuthResponse response = userService.loginUser(request);

      if (response.getMessage().equals(AuthResponseMessage.INVALID_CREDENTIALS.getMessage())) {
        logger.warning("Invalid credentials for user: " + request.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
      }
      logger.info("User logged in successfully: " + request.getEmail());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      logger.warning("Error logging in user: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
              new AuthResponse(request.getEmail(),
                      AuthResponseMessage.USER_LOGIN_ERROR.getMessage()
                              + e.getMessage(), null, null, null));
    }
  }


  /**
   * Endpoint for refreshing a JWT token.
   * This method validates the provided token, extracts the username, and generates a new token.
   * If the token is invalid or expired, it returns an appropriate error response.
   *
   * @param currentToken The current JWT token provided in the "Authorization" header.
   *                     It must start with the "Bearer" prefix.
   * @return ResponseEntity containing an AuthResponse object with the new token and user information,
   * or an error message if the token is invalid or expired.
   */
  @Operation(
          summary = "Refresh JWT token",
          description = "Refreshes an expired or soon-to-expire JWT token.",
          security = @SecurityRequirement(name = "BearerAuth")
  )
  @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(responseCode = "400", description = "Invalid refresh token format",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(responseCode = "401", description = "Token is invalid or expired",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal server error during token refresh",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class)))
  @PostMapping("/refreshToken")
  public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String currentToken) {
    logger.info("Received refresh token request");
    try {
      if (currentToken == null || !currentToken.startsWith("Bearer")) {
        logger.warning("Invalid refresh token format");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new AuthResponse(null, "Invalid refresh token format", null, null, null)
        );
      }

      String token = currentToken.substring(7);
      AuthResponse response = userService.refreshToken(token);

      if (response.getToken() == null) {
        logger.warning("Invalid or expired refresh token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
      }

      logger.info("Token refreshed successfully");
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      logger.warning("Error refreshing token: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
              new AuthResponse(null,
                      AuthResponseMessage.TOKEN_REFRESH_ERROR.getMessage()
                              + e.getMessage(), null, null, null)
      );
    }
  }
}