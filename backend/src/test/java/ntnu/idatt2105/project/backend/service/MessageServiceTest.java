package ntnu.idatt2105.project.backend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import ntnu.idatt2105.project.backend.dto.response.MessageResponse;
import ntnu.idatt2105.project.backend.model.User;
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

    @Mock
    private UserService userService;

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
    public void testSendMessage() {
        // Arrange
        MessageRequest request = new MessageRequest();
        request.setSender("sender@example.com");
        request.setReceiver("receiver@example.com");
        request.setContent("Hello, this is a test message");

        // Create mock users that will be returned
        User senderUser = new User();
        senderUser.setId(1L); // Set appropriate ID
        senderUser.setEmail("sender@example.com");

        User receiverUser = new User();
        receiverUser.setId(2L); // Set appropriate ID
        receiverUser.setEmail("receiver@example.com");

        // Mock userService behavior
        when(userService.findByEmail("sender@example.com")).thenReturn(Optional.of(senderUser));
        when(userService.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiverUser));

        // Mock messageRepo behavior
        doNothing().when(messageRepo).sendMessage(anyLong(), anyLong(), anyString());

        // Act
        MessageResponse response = messageService.sendMessage(request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getSender(), response.getSender());
        assertEquals(request.getReceiver(), response.getReceiver());
        assertEquals(request.getContent(), response.getContent());
        assertFalse(response.isRead());

        // Verify interactions
        verify(userService).findByEmail("sender@example.com");
        verify(userService).findByEmail("receiver@example.com");
        verify(messageRepo).sendMessage(senderUser.getId(), receiverUser.getId(), request.getContent());
    }

    /**
     * Test the getConversation method.
     * This test checks if the getConversation method correctly retrieves
     * the conversation between two users and returns a list of messages.
     */
    /**


    @Test
    void testGetConversation() {


        Message message1 = new Message("jane.smith@example.com", "peter.jones@example.com", "Hi!", false);
        Message message2 = new Message("peter.jones@example.com", "jane.smith@example.com", "Hello!", true);
        when(messageRepo.findBySenderAndReceiver(anyLong(), anyLong())).thenReturn(List.of(message1, message2));

        List<Message> messages = messageService.getConversation(1L, 2L);

        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals("Hi!", messages.get(0).getContent());
        assertEquals("Hello!", messages.get(1).getContent());
    }
     */

    @Test
    void testGetConversation() {
        // Create test messages
        Message message1 = new Message("jane.smith@example.com", "peter.jones@example.com", "Hi!", false);
        Message message2 = new Message("peter.jones@example.com", "jane.smith@example.com", "Hello!", true);

        // Mock the repository method that's actually being called
        when(messageRepo.findByParticipants(anyLong(), anyLong()))
            .thenReturn(List.of(message1, message2));

        // Call the service method
        List<Message> messages = messageService.getConversation(1L, 2L);

        // Verify results
        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals("Hi!", messages.get(0).getContent());
        assertEquals("Hello!", messages.get(1).getContent());

        // Verify the repository method was called with correct parameters
        verify(messageRepo).findByParticipants(1L, 2L);
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