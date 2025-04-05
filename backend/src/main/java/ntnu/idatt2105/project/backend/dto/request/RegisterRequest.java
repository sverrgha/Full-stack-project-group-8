package ntnu.idatt2105.project.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
  @NotBlank(message = "First name is required, and cannot be blank")
  private String firstname;

  @NotBlank(message = "Last name is required, and cannot be blank")
  private String lastname;

  @NotBlank(message = "Email is required, and cannot be blank")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Phone number is required, and cannot be blank")
  private String phoneNumber;

  @NotBlank(message = "Password is required, and cannot be blank")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

}
