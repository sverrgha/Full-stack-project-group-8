package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * This class represents the response sent to the client after a successful authentication.
 * It contains the user's email, a message indicating the result of the authentication,
 * the generated JWT token, and the expiration date of the token.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
  private String email;
  private String message;
  private String token;
  private Date expirationDate;
}
