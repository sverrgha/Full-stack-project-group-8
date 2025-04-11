package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponse {
    private String sender;
    private String receiver;
    private String content;
    private String timestamp;
    private boolean isRead;
}
