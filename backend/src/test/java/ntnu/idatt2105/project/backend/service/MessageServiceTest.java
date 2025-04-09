package ntnu.idatt2105.project.backend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ntnu.idatt2105.project.backend.dto.request.MessageRequest;
import ntnu.idatt2105.project.backend.dto.response.ConversationSummaryResponse;
import ntnu.idatt2105.project.backend.model.Message;
import ntnu.idatt2105.project.backend.repository.MessageRepo;


/**
 * This class contains unit tests for the MessageService class.
 * It uses Mockito to mock dependencies and JUnit for assertions.
 * The tests cover various methods in the MessageService class.
 */
class MessageServiceTest {

    @Mock
    private MessageRepo messageRepo;

    @InjectMocks
    private MessageService messageService;
    /**
     * This method is called before each test case to initialize the mocks.
     * It uses MockitoAnnotations to create mock objects for the MessageRepo.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test the sendMessage method.
     * This test checks if the sendMessage method correctly sends a message
     * and returns the expected Message object.
     */
    @Test
    void testSendMessage() {

        MessageRequest request = new MessageRequest();
        request.setSender(1L);
        request.setReceiver(2L);
        request.setContent("Hello!");
        doNothing().when(messageRepo).sendMessage(anyLong(), anyLong(), anyString());

        Message message = messageService.sendMessage(request);

        assertNotNull(message);
        assertEquals(1L, message.getSender());
        assertEquals(2L, message.getReceiver());
        assertEquals("Hello!", message.getContent());
        assertFalse(message.isRead());
    }

    /**
     * Test the getConversation method.
     * This test checks if the getConversation method correctly retrieves
     * the conversation between two users and returns a list of messages.
     */
    @Test
    void testGetConversation() {

        Message message1 = new Message(1L, 2L, "Hi!", false);
        Message message2 = new Message(2L, 1L, "Hello!", true);
        when(messageRepo.findBySenderAndReceiver(anyLong(), anyLong())).thenReturn(List.of(message1, message2));

        List<Message> messages = messageService.getConversation(1L, 2L);

        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals("Hi!", messages.get(0).getContent());
        assertEquals("Hello!", messages.get(1).getContent());
    }

    /**
     * Test the getInboxList method.
     * This test checks if the getInboxList method correctly retrieves
     * the inbox messages for a specific user and returns a list of
     * ConversationSummaryResponse objects.
     * It also verifies that the inbox is not empty and contains the expected values.
     */
    @Test
    void testGetInboxList() {

        ConversationSummaryResponse summary = new ConversationSummaryResponse();
        summary.setOtherUserEmail("test@example.com");
        summary.setLastMessage("Hello!");
        summary.setTimestamp("2025-04-05T12:00:00");
        summary.setRead(false);
        when(messageRepo.getInboxViewForUser(anyLong())).thenReturn(List.of(summary));

        List<ConversationSummaryResponse> inbox = messageService.getInboxList(1L);

        assertNotNull(inbox);
        assertEquals(1, inbox.size());
        assertEquals("test@example.com", inbox.get(0).getOtherUserEmail());
        assertEquals("Hello!", inbox.get(0).getLastMessage());
    }

    /**
     * Test the deleteMessage method.
     * This test checks if the deleteMessage method correctly deletes a message
     * by its ID and verifies that the deleteMessage method in the repository is called once.
     * It also verifies that the message ID is passed correctly to the repository method.
     */
    @Test
    void testDeleteMessage() {

        doNothing().when(messageRepo).deleteMessage(anyLong());

        messageService.deleteMessage(1L);

        verify(messageRepo, times(1)).deleteMessage(1L);
    }

    /**
     * Test the conversationExists method.
     * This test checks if the conversationExists method correctly checks
     * if a conversation exists between two users.
     * It verifies that the conversationExistsBetween method in the repository
     * is called once and returns the expected value.
     */
    @Test
    void testConversationExists() {

        when(messageRepo.conversationExistsBetween(anyLong(), anyLong())).thenReturn(true);

        boolean exists = messageService.conversationExists(1L, 2L);

        assertTrue(exists);
        verify(messageRepo, times(1)).conversationExistsBetween(1L, 2L);
    }

    /**
     * Test the markConversationAsRead method.
     * This test checks if the markConversationAsRead method correctly marks
     * a conversation as read for two users.
     * It verifies that the markAsReadForConveration method in the repository
     * is called once and that the correct user IDs are passed to it.
     */
    @Test
    void testMarkConversationAsRead() {

        doNothing().when(messageRepo).markAsReadForConveration(anyLong(), anyLong());

        messageService.markConversationAsRead(1L, 2L);

        verify(messageRepo, times(1)).markAsReadForConveration(1L, 2L);
    }
}