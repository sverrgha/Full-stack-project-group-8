package ntnu.idatt2105.project.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull(message = "Sender ID is required, and cannot be null")
    private String sender;
    @NotNull(message = "Receiver ID is required, and cannot be null")
    private String receiver;
    @NotBlank(message = "Content is required, and cannot be blank")
    private String content;

    public String getSenderEmail() {
        return sender;
    }

    public String getReceiverEmail() {
        return receiver;
    }
}
