package ntnu.idatt2105.project.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


/**
 * This class contains unit tests for the Message class.
 * It tests the constructors, getters, and setters of the Message class.
 */
class MessageTest {

    /**
     * Test the constructor of the Message class with all fields.
     * This test checks if the constructor correctly initializes the fields of the Message object.
     * It also verifies that the getters return the expected values.
     */
    @Test
    void testMessageConstructorWithAllFields() {

        Long id = 1L;
        Long sender = 2L;
        Long receiver = 3L;
        String content = "Hello, this is a test message.";
        String timestamp = "2025-04-05T12:00:00";
        boolean isRead = true;

        Message message = new Message(id, sender, receiver, content, timestamp, isRead);

        assertEquals(id, message.getId());
        assertEquals(sender, message.getSender());
        assertEquals(receiver, message.getReceiver());
        assertEquals(content, message.getContent());
        assertEquals(timestamp, message.getTimestamp());
        assertTrue(message.isRead());
    }

    /**
     * Test the constructor of the Message class without ID and timestamp.
     * This test checks if the constructor correctly initializes the fields of the Message object
     * when the ID and timestamp are not provided.
     * It also verifies that the getters return the expected values.
     */
    @Test
    void testMessageConstructorWithoutIdAndTimestamp() {

        Long sender = 2L;
        Long receiver = 3L;
        String content = "Hello, this is another test message.";
        boolean isRead = false;

        Message message = new Message(sender, receiver, content, isRead);

        assertEquals(sender, message.getSender());
        assertEquals(receiver, message.getReceiver());
        assertEquals(content, message.getContent());
        assertFalse(message.isRead());
    }

    /**
     * Test the setters and getters of the Message class.
     * This test checks if the setters correctly set the values of the fields
     * and if the getters return the expected values.
     * It also verifies that the isRead field is set correctly.
     */
    @Test
    void testSettersAndGetters() {

        Message message = new Message();
        Long id = 1L;
        Long sender = 2L;
        Long receiver = 3L;
        String content = "Testing setters and getters.";
        String timestamp = "2025-04-05T12:00:00";
        boolean isRead = true;

        message.setId(id);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(timestamp);
        message.setRead(isRead);

        assertEquals(id, message.getId());
        assertEquals(sender, message.getSender());
        assertEquals(receiver, message.getReceiver());
        assertEquals(content, message.getContent());
        assertEquals(timestamp, message.getTimestamp());
        assertTrue(message.isRead());
    }
}