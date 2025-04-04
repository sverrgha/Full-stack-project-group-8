package ntnu.idatt2105.project.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank(message = "Email is required, and cannot be blank")
  private String email;

  @NotBlank(message = "Password is required, and cannot be blank")
  private String password;
}
