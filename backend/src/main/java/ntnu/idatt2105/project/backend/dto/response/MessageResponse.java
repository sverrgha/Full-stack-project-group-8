package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponse {
    private long sender;
    private long receiver;
    private String content;
    private String timestamp;
    private boolean isRead;
}
