package ntnu.idatt2105.project.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ntnu.idatt2105.project.backend.dto.request.MessageRequest;
import ntnu.idatt2105.project.backend.dto.response.ConversationSummaryResponse;
import ntnu.idatt2105.project.backend.dto.response.MessageResponse;
import ntnu.idatt2105.project.backend.model.User;
import ntnu.idatt2105.project.backend.security.JwtUtil;
import ntnu.idatt2105.project.backend.service.MessageService;
import ntnu.idatt2105.project.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static ntnu.idatt2105.project.backend.util.TokenExtractor.extractToken;

/**
 * This class handles incoming HTTP requests related to messaging functionality.
 * It provides endpoints for sending messages, retrieving inbox messages, and fetching conversations.
 * It uses the MessageService to perform the actual operations and the JwtUtil for token validation.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {


  private static final Logger logger = Logger.getLogger(MessageController.class.getName());
  @Autowired
  private final MessageService messageService;
  private final JwtUtil jwtUtil;
  private final UserService userService;

  public MessageController(MessageService messageService, JwtUtil jwtUtil, UserService userService) {
    this.userService = userService;
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
   * @param request     The MessageRequest containing the sender, receiver, and message content
   * @return ResponseEntity with the MessageResponse or an error status
   */
  @PostMapping("/send")
  public ResponseEntity<MessageResponse> sendMessage(HttpServletRequest httpRequest, @Valid @RequestBody MessageRequest request) {
    logger.info("Received send message request from user: " + request.getSender());
    try {
      String token = extractToken(httpRequest);
      String email = jwtUtil.extractUsername(token);
      Optional<User> optionalUser = userService.findByEmail(email);

      if (optionalUser.isEmpty()) {
        logger.warning("User not found: " + email);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      User authenticatedUser = optionalUser.get();

      if (!authenticatedUser.getId().equals(request.getSender())) {
        logger.warning("Unauthorized sender: " + request.getSender());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      MessageResponse response = messageService.sendMessage(request);
      logger.info("Message sent successfully from user: " + request.getSender() + " to user: " + request.getReceiver());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      logger.warning("Error sending message: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
   */
  @GetMapping("/inbox")
  public ResponseEntity<?> getInbox(HttpServletRequest request) {
    logger.info("Received inbox request");
    try {
      String token = extractToken(request);
      String email = jwtUtil.extractUsername(token);
      Optional<User> user = userService.findByEmail(email);

      if (user.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
      }

      List<ConversationSummaryResponse> messages = messageService.getInboxList(user.get().getId());
      if (messages.isEmpty()) {
        logger.warning("No messages found for user: " + email);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No messages found.");
      }
      logger.info("Inbox retrieved successfully for user: " + email);
      return ResponseEntity.ok(messages);
    } catch (Exception e) {
      logger.warning("Error retrieving inbox: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving inbox: " + e.getMessage());
    }
  }

  /**
   * Handles the HTTP GET request to retrieve the conversation between two users.
   * It extracts the token from the request, validates it, and retrieves the conversation messages using the MessageService.
   * If successful, it returns a list of MessageResponse objects.
   * If the user is not found, the conversation does not exist, or an error occurs, it returns an appropriate HTTP status.
   *
   * @param request   The HTTP request containing the authorization token
   * @param endUserId The User object representing the other user in the conversation
   * @return ResponseEntity with the list of MessageResponse or an error status
   */
  /*
  @GetMapping("/conversation/{endUserId}")
  public ResponseEntity<?> getConversation(HttpServletRequest request,
                                           @RequestParam(required = false) Long endUserId) {
    logger.info("Received conversation request from user: " + endUserId);
    try {
      String token = extractToken(request);
      String email = jwtUtil.extractUsername(token);
      Optional<User> optionalUser = userService.findByEmail(email);

      if (optionalUser.isEmpty()) {
        logger.warning("User not found: " + email);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
      }

      User user1 = optionalUser.get();

      if (user1.getId().equals(endUserId)) {
        logger.warning("Cannot fetch conversation with yourself");
        return ResponseEntity.badRequest().body("Cannot fetch conversation with yourself");
      }

      if (!messageService.conversationExists(user1.getId(), endUserId)) {
        logger.warning("No conversation found between user: " + user1.getId() + " and user: " + endUserId);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: No conversation found between users");
      }

      messageService.markConversationAsRead(user1.getId(), endUserId);

      List<MessageResponse> messages = messageService
          .getConversation(user1.getId(), endUserId)
          .stream()
          .filter(message ->
              (message.getSender().equals(user1.getId()) && message.getReceiver().equals(endUserId)) ||
                  (message.getSender().equals(endUserId) && message.getReceiver().equals(user1.getId()))
          )
          .map(message -> new MessageResponse(
              message.getSender(),
              message.getReceiver(),
              message.getContent(),
              message.getTimestamp(),
              message.isRead()
          ))
          .toList();
      logger.info("Conversation retrieved successfully between user: " + user1.getId() + " and user: " + endUserId);
      return ResponseEntity.ok(messages);

    } catch (Exception e) {
      logger.warning("Error retrieving conversation: " + e.getMessage());
      return ResponseEntity.status(500).body("Error retrieving conversation: " + e.getMessage());
    }
  }

   */

  @GetMapping("/conversation/{endUserEmail}")
  public ResponseEntity<?> getConversation(HttpServletRequest request,
                                           @PathVariable String endUserEmail) {
    logger.info("Received conversation request for user: " + endUserEmail);
    try {
      String token = extractToken(request);
      String currentUserEmail = jwtUtil.extractUsername(token);

      // Get current user
      Optional<User> currentUser = userService.findByEmail(currentUserEmail);
      if (currentUser.isEmpty()) {
        logger.warning("Current user not found: " + currentUserEmail);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
      }

      // Get end user
      Optional<User> endUser = userService.findByEmail(endUserEmail);
      if (endUser.isEmpty()) {
        logger.warning("End user not found: " + endUserEmail);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
      }

      User user1 = currentUser.get();
      User user2 = endUser.get();

      if (currentUserEmail.equals(endUserEmail)) {
        logger.warning("Cannot fetch conversation with yourself");
        return ResponseEntity.badRequest().body("Cannot fetch conversation with yourself");
      }

      if (!messageService.conversationExists(user1.getId(), user2.getId())) {
        logger.warning("No conversation found between users: " + currentUserEmail + " and " + endUserEmail);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No conversation found between users");
      }

      messageService.markConversationAsRead(user1.getId(), user2.getId());

      List<MessageResponse> messages = messageService
        .getConversation(user1.getId(), user2.getId())
        .stream()
        .filter(message ->
          (message.getSender().equals(user1.getId()) && message.getReceiver().equals(user2.getId())) ||
            (message.getSender().equals(user2.getId()) && message.getReceiver().equals(user1.getId()))
        )
        .map(message -> new MessageResponse(
          message.getSender(),
          message.getReceiver(),
          message.getContent(),
          message.getTimestamp(),
          message.isRead()
        ))
        .toList();

      logger.info("Conversation retrieved successfully between users: " + currentUserEmail + " and " + endUserEmail);
      return ResponseEntity.ok(messages);

    } catch (Exception e) {
      logger.warning("Error retrieving conversation: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving conversation: " + e.getMessage());
    }
  }
}

