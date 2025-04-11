package ntnu.idatt2105.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ntnu.idatt2105.project.backend.dto.request.ModifyUserRequest;
import ntnu.idatt2105.project.backend.dto.response.UserResponse;
import ntnu.idatt2105.project.backend.service.UserService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.logging.Logger;

@Tag(name = "User", description = "Endpoints for managing user information")
@RestController
@RequestMapping("/api/user")
public class UserController {
  private static final Logger logger = Logger.getLogger(UserController.class.getName());
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(
          summary = "Get user by ID",
          description = "Retrieves a user's information based on their unique ID.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "id", in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH, required = true, description = "ID of the user to retrieve")
          },
          responses = {
                  @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                          content = @Content(mediaType = "application/json",
                                  schema = @Schema(implementation = UserResponse.class))),
                  @ApiResponse(responseCode = "400", description = "Invalid user ID provided",
                          content = @Content(mediaType = "application/json",
                                  schema = @Schema(implementation = UserResponse.class))),
                  @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                  @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    logger.info("Received request for user with ID: " + id);
    try {
      UserResponse response = userService.getUserById(id);
      logger.info("User fetched successfully: " + response);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid user ID: " + id);
      return ResponseEntity.badRequest().body(new UserResponse());
    } catch (Exception e) {
      logger.severe("Error fetching user: " + e.getMessage());
      return ResponseEntity.internalServerError().body(new UserResponse());
    }
  }

  @Operation(
          summary = "Update user information",
          description = "Updates an existing user's information.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "id", in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH, required = true, description = "ID of the user to update"),
                  @Parameter(name = "Authorization", in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER, required = true, description = "Bearer token for authentication")
          },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "User information to update",
                  required = true,
                  content = @Content(schema = @Schema(implementation = ModifyUserRequest.class))
          ),
          responses = {
                  @ApiResponse(responseCode = "200", description = "Successfully updated user"),
                  @ApiResponse(responseCode = "400", description = "Invalid user ID provided"),
                  @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                  @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @PutMapping("/{id}")
  public ResponseEntity<String> updateUser(
          @PathVariable Long id,
          @RequestBody ModifyUserRequest request,
          @RequestHeader("Authorization") String authToken
  ) {
    logger.info("Received request to update user with ID: " + id);
    try {
      userService.updateUser(id, request,
              TokenExtractor.extractToken(authToken));
      logger.info("Successfully updated use with ID: " + id);
      return ResponseEntity.ok("Update successful");
    } catch (IllegalArgumentException e) {
      logger.warning("Invalid user ID: " + id);
      return ResponseEntity.badRequest().body("Invalid user ID");
    } catch (Exception e) {
      logger.severe("Error updating user: " + e.getMessage());
      return ResponseEntity.internalServerError().body("An error occurred");
    }
  }
}
