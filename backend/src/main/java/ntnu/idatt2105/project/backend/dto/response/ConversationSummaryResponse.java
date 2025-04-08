package ntnu.idatt2105.project.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationSummaryResponse {
    private String otherUserEmail;
    private String lastMessage;
    private String timestamp;
    private boolean isRead;
}
