package ntnu.idatt2105.project.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * This class represents a message in the system.
 * It contains the message's information such as sender, receiver, content, and timestamp.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    
    private long id;

    private long sender;
    private long receiver;
    private String content;
    private String timestamp;
    private boolean isRead;

    /**
     * Constructor for Message class. This constructor is used to create a new message object.
     *
     * @param sender   the ID of the sender
     * @param receiver the ID of the receiver
     * @param content  the content of the message
     * @param isRead   the read status of the message
     */
    public Message(long sender, long receiver, String content, boolean isRead) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.isRead = isRead;
    }
}
