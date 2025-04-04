package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
  private String email;
  private String message;
  private String token;
}
