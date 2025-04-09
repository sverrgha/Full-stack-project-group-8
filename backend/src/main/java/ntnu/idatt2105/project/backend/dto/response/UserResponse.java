package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
  private Long id;
  private String firstname;
  private String lastname;
  private String email;
  private String phoneNumber;
  private boolean isAdmin;
  private Date createdAt;
}
