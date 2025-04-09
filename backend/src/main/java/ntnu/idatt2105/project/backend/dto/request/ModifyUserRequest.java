package ntnu.idatt2105.project.backend.dto.request;


import lombok.Data;

@Data
public class ModifyUserRequest {
  private String username;
  private String firstname;
  private String lastname;
  private String email;
  private String phoneNumber;
  private String password;
}
