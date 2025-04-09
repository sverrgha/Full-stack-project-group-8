package ntnu.idatt2105.project.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.MessageRequest;
import ntnu.idatt2105.project.backend.dto.response.ConversationSummaryResponse;
import ntnu.idatt2105.project.backend.model.Message;
import ntnu.idatt2105.project.backend.repository.MessageRepo;


/**
 * This class provides services related to messaging functionality.
 * It includes methods for sending messages, retrieving conversations,
 * It acts as a bridge between the controller and the repository layer.
 * It uses the MessageRepo to perform database operations.
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepo messageRepo;

    /**
     * Sends a message from one user to another.
     *
     * @param request The MessageRequest object containing the sender, receiver, and message content.
     * @return The sent Message object.
     */
    public Message sendMessage(MessageRequest request) {
        Long sender = request.getSender();
        Long receiver = request.getReceiver();
        String content = request.getContent();

        if (sender <= 0 || receiver <= 0) {
            throw new IllegalArgumentException("Invalid sender or receiver ID");
        }
    try {
        messageRepo.sendMessage(sender, receiver, content);
    } catch (Exception e) {
        throw new RuntimeException("Error sending message: " + e.getMessage());
    }
        Message message = new Message(sender, receiver, content, false);
        return message;
    }


    /**
     * Retrieves the conversation between two users.
     *
     * @param sender   The ID of the sender.
     * @param receiver The ID of the receiver.
     * @return A list of messages in the conversation.
     */
    public List<Message> getConversation(Long sender, Long receiver) {
        return messageRepo.findBySenderAndReceiver(sender, receiver);
    }

    /**
     * Retrieves all messages for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of messages for the user.
     */
    public List<Message> getMessagesForUser(Long userId) {
        return messageRepo.getAllMessagesForUser(userId);
    }

    /**
     * Retrieves the inbox list for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of conversation summaries for the user's inbox.
     */
    public List<ConversationSummaryResponse> getInboxList(Long userId) {
        return messageRepo.getInboxViewForUser(userId);
    }

    /**
     * Deletes a message by its ID.
     *
     * @param messageId The ID of the message to delete.
     */
    public void deleteMessage(Long messageId) {
        messageRepo.deleteMessage(messageId);
    }

    /**
     * Checks if a conversation exists between two users.
     *
     * @param userId1 The ID of the first user.
     * @param userId2 The ID of the second user.
     * @return True if a conversation exists, false otherwise.
     */
    public boolean conversationExists(Long userId1, Long userId2) {
        return messageRepo.conversationExistsBetween(userId1, userId2);
    }


    /**
     * Marks a conversation as read for two users.
     *
     * @param id  The ID of the first user.
     * @param id2 The ID of the second user.
     */
    public void markConversationAsRead(Long id, Long id2) {
        messageRepo.markAsReadForConveration(id, id2);
    }
}
