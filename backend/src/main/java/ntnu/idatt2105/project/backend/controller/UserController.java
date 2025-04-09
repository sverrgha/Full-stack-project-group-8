package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.dto.request.ModifyUserRequest;
import ntnu.idatt2105.project.backend.dto.response.UserResponse;
import ntnu.idatt2105.project.backend.service.UserService;
import ntnu.idatt2105.project.backend.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private static final Logger logger = Logger.getLogger(UserController.class.getName());
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

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
