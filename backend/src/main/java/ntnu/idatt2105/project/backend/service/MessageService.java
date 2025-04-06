package ntnu.idatt2105.project.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.MessageRequest;
import ntnu.idatt2105.project.backend.dto.response.ConversationSummaryResponse;
import ntnu.idatt2105.project.backend.model.Message;
import ntnu.idatt2105.project.backend.repository.MessageRepo;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepo messageRepo;

    public Message sendMessage(MessageRequest request) {
        long sender = request.getSender();
        long receiver = request.getReceiver();
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


    public List<Message> getConversation(long sender, long receiver) {
        return messageRepo.findBySenderAndReceiver(sender, receiver);
    }

    public List<Message> getMessagesForUser(long userId) {
        return messageRepo.getAllMessagesForUser(userId);
    }

    public List<ConversationSummaryResponse> getInboxList(long userId) {
        return messageRepo.getInboxViewForUser(userId);
    }

    public void deleteMessage(long messageId) {
        messageRepo.deleteMessage(messageId);
    }

    public boolean conversationExists(long userId1, long userId2) {
        return messageRepo.conversationExistsBetween(userId1, userId2);
    }


    public void markConverasationAsRead(long id, long id2) {
        messageRepo.markAsReadForConveration(id, id2);
    }


    
}
