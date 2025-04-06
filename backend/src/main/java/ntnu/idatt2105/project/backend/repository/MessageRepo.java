package ntnu.idatt2105.project.backend.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ntnu.idatt2105.project.backend.dto.response.ConversationSummaryResponse;
import ntnu.idatt2105.project.backend.model.Message;

@Repository
public class MessageRepo {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MessageRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Sends a message from one user to another.
     *
     * @param sender   The ID of the sender.
     * @param receiver The ID of the receiver.
     * @param content  The content of the message.
     */
    public void sendMessage(long sender, long receiver, String content) {
        String sql = "INSERT INTO sverrgha_datab.messages (from_user_id, to_user_id, message) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, sender, receiver, content);
    }

    /**
     * Finds messages between two users.
     *
     * @param user1 The ID of the first user.
     * @param user2 The ID of the second user.
     * @return A list of messages between the two users.
     */
    public List<Message> findBySenderAndReceiver(long user1, long user2) {
        String sql = "SELECT * FROM sverrgha_datab.messages WHERE (from_user_id= ? AND to_user_id= ?) OR (from_user_id = ? AND to_user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Message message = new Message();
            message.setId(rs.getLong("id"));
            message.setSender(rs.getLong("from_user_id"));
            message.setReceiver(rs.getLong("to_user_id"));
            message.setContent(rs.getString("message"));
            message.setTimestamp(rs.getString("sent_at"));
            return message;
        }, user1, user2, user2, user1);
    }

    /**
     * Checks if a conversation exists between two users.
     *
     * @param userId1 The ID of the first user.
     * @param userId2 The ID of the second user.
     * @return True if a conversation exists, false otherwise.
     */
    public boolean conversationExistsBetween(long userId1, long userId2) {
        String sql = "SELECT COUNT(*) FROM messages WHERE " +
                     "(from_user_id = ? AND to_user_id = ?) OR " +
                     "(from_user_id = ? AND to_user_id = ?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId1, userId2, userId2, userId1);
        return count != null && count > 0;
    }

    /**
     * Deletes a message by its ID.
     *
     * @param messageId The ID of the message to delete.
     */
    public void deleteMessage(long messageId) {
        String sql = "DELETE FROM sverrgha_datab.messages WHERE id = ?";
        jdbcTemplate.update(sql, messageId);
    }

    /**
     * Retrieves all messages for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of messages for the user.
     */
    public List<Message> getAllMessagesForUser(long userId) {
        String sql = "SELECT * FROM sverrgha_datab.messages WHERE from_user_id = ? OR to_user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Message message = new Message();
            message.setId(rs.getLong("id"));
            message.setSender(rs.getLong("from_user_id"));
            message.setReceiver(rs.getLong("to_user_id"));
            message.setContent(rs.getString("message"));
            message.setTimestamp(rs.getString("sent_at"));
            message.setRead(rs.getBoolean("read")); // Assuming you have a 'read' column
            return message;
        }, userId, userId);
    }

    /**
     * Retrieves a summary of conversations for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of conversation summaries for the user.
     */
    public List<ConversationSummaryResponse> getInboxViewForUser(long userId) {
        String sql = "SELECT " +
                     "  CASE " +
                     "    WHEN m.from_user_id = ? THEN m.to_user_id " +
                     "    ELSE m.from_user_id " +
                     "  END AS other_user_id, " +
                     "  u.email AS other_user_email, " +
                     "  m.message AS last_message, " +
                     "  m.sent_at AS latest_message_time " +
                     "FROM sverrgha_datab.messages m " +
                     "JOIN sverrgha_datab.users u ON u.id = " +
                     "  CASE " +
                     "    WHEN m.from_user_id = ? THEN m.to_user_id " +
                     "    ELSE m.from_user_id " +
                     "  END " +
                     "WHERE m.from_user_id = ? OR m.to_user_id = ? " +
                     "ORDER BY m.sent_at DESC";
    
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ConversationSummaryResponse summary = new ConversationSummaryResponse();
            summary.setOtherUserEmail(rs.getString("other_user_email")); 
            summary.setLastMessage(rs.getString("last_message"));
            summary.setTimestamp(rs.getTimestamp("latest_message_time").toString());
            return summary;
        }, userId, userId, userId, userId);
    }

    /**
     * Marks a conversation as read between two users.
     *
     * @param id  The ID of the first user.
     * @param id2 The ID of the second user.
     */
    public void markAsReadForConveration(long id, long id2) {
        String sql = "UPDATE sverrgha_datab.messages SET isRead = true WHERE (from_user_id = ? AND to_user_id = ?) OR (from_user_id = ? AND to_user_id = ?)";
        jdbcTemplate.update(sql, id, id2, id2, id);
    }
}
