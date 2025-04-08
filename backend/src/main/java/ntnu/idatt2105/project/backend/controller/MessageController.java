package ntnu.idatt2105.project.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ntnu.idatt2105.project.backend.dto.request.MessageRequest;
import ntnu.idatt2105.project.backend.dto.response.ConversationSummaryResponse;
import ntnu.idatt2105.project.backend.dto.response.MessageResponse;
import ntnu.idatt2105.project.backend.model.Message;
import ntnu.idatt2105.project.backend.model.User;
import ntnu.idatt2105.project.backend.repository.UserRepo;
import ntnu.idatt2105.project.backend.security.JwtUtil;
import ntnu.idatt2105.project.backend.service.MessageService;

/**
 * This class handles incoming HTTP requests related to messaging functionality.
 * It provides endpoints for sending messages, retrieving inbox messages, and fetching conversations.
 * It uses the MessageService to perform the actual operations and the JwtUtil for token validation.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    
    @Autowired
    private final MessageService messageService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = Logger.getLogger(MessageController.class.getName());
    private final UserRepo userRepo;

    public MessageController(MessageService messageService, JwtUtil jwtUtil, UserRepo userRepo) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.messageService = messageService;
    }

    /**
     * Handles the HTTP POST request to send a message.
     * It validates the request, checks the sender's identity, and sends the message using the MessageService.
     * If successful, it returns a MessageResponse with the message details.
     * If the sender is not authorized or an error occurs, it returns an appropriate HTTP status.
     * 
     * @param httpRequest The HTTP request containing the authorization token
     * @param request The MessageRequest containing the sender, receiver, and message content
     * @return ResponseEntity with the MessageResponse or an error status
     * @throws Exception if an error occurs during message sending
     * @Valid Ensures that the request body is valid according to the MessageRequest class
     */
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(HttpServletRequest httpRequest, @Valid @RequestBody MessageRequest request) {
        logger.info("Received message request from user: " + request.getSender() + " to user: " + request.getReceiver());
        try {
            logger.info("HttpServletRequest: " + httpRequest.toString());
            String token = extractToken(httpRequest);
            String email = jwtUtil.extractUsername(token);
            Optional<User> optionalUser = userRepo.findByEmail(email);
    
            if (optionalUser.isEmpty()) {
                logger.warning("Unauthorized access attempt");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
    
            User authenticatedUser = optionalUser.get();
    
            if (authenticatedUser.getId() != request.getSender()) {
                logger.warning("User " + authenticatedUser.getId() + " attempted to send a message as another user: " + request.getSender());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
    
            Message message = messageService.sendMessage(request);
            MessageResponse response = new MessageResponse(
                    message.getSender(),
                    message.getReceiver(),
                    message.getContent(),
                    message.getTimestamp(),
                    message.isRead()
            );
            logger.info("Message sent successfully");
            return ResponseEntity.ok(response);
    
        } catch (Exception e) {
            logger.warning("Error sending message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Handles the HTTP GET request to retrieve the inbox messages for the authenticated user.
     * It extracts the token from the request, validates it, and retrieves the inbox messages using the MessageService.
     * If successful, it returns a list of ConversationSummaryResponse objects.
     * If the user is not found or an error occurs, it returns an appropriate HTTP status.
     * 
     * @param request The HTTP request containing the authorization token
     * @return ResponseEntity with the list of ConversationSummaryResponse or an error status
     * @throws Exception if an error occurs during inbox retrieval
     */
    @GetMapping("/inbox")
    public ResponseEntity<?> getInbox(HttpServletRequest request) {
        logger.info("Received inbox request");
        try {
            String token = extractToken(request);
            String email = jwtUtil.extractUsername(token); 
            Optional<User> user = userRepo.findByEmail(email);
    
            List<ConversationSummaryResponse> messages = messageService.getInboxList(user.get().getId());
            if (messages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No messages found.");
            }
            logger.info("Inbox retrieved successfully for user: " + email);
            return ResponseEntity.ok(messages);
        } catch (Exception e) { 
            logger.warning("Error retrieving inbox: " + e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving inbox: " + e.getMessage());
        }
    }

    /**
     * Handles the HTTP GET request to retrieve the conversation between two users.
     * It extracts the token from the request, validates it, and retrieves the conversation messages using the MessageService.
     * If successful, it returns a list of MessageResponse objects.
     * If the user is not found, the conversation does not exist, or an error occurs, it returns an appropriate HTTP status.
     * 
     * 
     * @param request The HTTP request containing the authorization token
     * @param endUser The User object representing the other user in the conversation
     * @return ResponseEntity with the list of MessageResponse or an error status
     * @throws Exception if an error occurs during conversation retrieval
     */
    @GetMapping("/conversation")
    public ResponseEntity<?> getConversation(HttpServletRequest request, 
                                         @RequestBody User endUser) {
        logger.info("Received conversation request from user: " + endUser.getId());
        try {
            String token = extractToken(request);
            String email = jwtUtil.extractUsername(token);
            Optional<User> optionalUser = userRepo.findByEmail(email);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user1 = optionalUser.get();

            if (user1.getId() == endUser.getId()) {
                return ResponseEntity.badRequest().body("Cannot fetch conversation with yourself");
            }

            if (!messageService.conversationExists(user1.getId(), endUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: No conversation found between users");
            }

            messageService.markConverasationAsRead(user1.getId(), endUser.getId());

            List<MessageResponse> messages = messageService
                    .getConversation(user1.getId(), endUser.getId())
                    .stream()
                    .filter(message -> 
                        (message.getSender() == user1.getId() && message.getReceiver() == endUser.getId()) ||
                        (message.getSender() == endUser.getId() && message.getReceiver() == user1.getId())
                    )
                    .map(message -> new MessageResponse(
                            message.getSender(),
                            message.getReceiver(),
                            message.getContent(),
                            message.getTimestamp(),
                            message.isRead()
                    ))
                    .toList();
            logger.info("Conversation retrieved successfully between user: " + user1.getId() + " and user: " + endUser.getId());
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            logger.warning("Error retrieving conversation: " + e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving conversation: " + e.getMessage());
        }
    }


    /**
     * Extracts the token from the HTTP request.
     * It looks for the "Authorization" header and retrieves the token if it starts with "Bearer ".
     * If the token is not found, it returns null.
     * 
     * @param request The HTTP request containing the authorization header
     * @return The extracted token or null if not found
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

