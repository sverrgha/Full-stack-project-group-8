package ntnu.idatt2105.project.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static ntnu.idatt2105.project.backend.util.TokenExtractor.extractToken;

/**
 * This class handles incoming HTTP requests related to messaging functionality.
 * It provides endpoints for sending messages, retrieving inbox messages, and fetching conversations.
 * It uses the MessageService to perform the actual operations and the JwtUtil for token validation.
 */
@Tag(name = "Messages", description = "Endpoints for managing user messages and conversations")
@RestController
@RequestMapping("/api/messages")
public class MessageController {
  private static final Logger logger = Logger.getLogger(MessageController.class.getName());
  private final MessageService messageService;
  private final JwtUtil jwtUtil;
  private final UserService userService;

  @Autowired
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
  @Operation(
          summary = "Send a message",
          description = "Sends a message from one user to another.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "Bearer token for authentication")
          },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Message details",
                  required = true,
                  content = @Content(schema = @Schema(implementation = MessageRequest.class))
          ),
          responses = {
                  @ApiResponse(responseCode = "200", description = "Message sent successfully",
                          content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                  @ApiResponse(responseCode = "400", description = "Invalid message request"),
                  @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                  @ApiResponse(responseCode = "403", description = "Forbidden - Sender not authorized"),
                  @ApiResponse(responseCode = "404", description = "Sender or receiver not found"),
                  @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @PostMapping("/send")
  public ResponseEntity<MessageResponse> sendMessage(HttpServletRequest httpRequest, @Valid @RequestBody MessageRequest request) {
    logger.info("Received send message request from user: " + request.getSenderEmail());
    try {
      String token = extractToken(httpRequest);
      String authenticatedEmail = jwtUtil.extractUsername(token);

      if (!authenticatedEmail.equals(request.getSenderEmail())) {
        logger.warning("Unauthorized sender: " + request.getSenderEmail());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      Optional<User> sender = userService.findByEmail(request.getSenderEmail());
      if (sender.isEmpty()) {
        logger.warning("Sender not found: " + request.getSenderEmail());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      Optional<User> receiver = userService.findByEmail(request.getReceiverEmail());
      if (receiver.isEmpty()) {
        logger.warning("Receiver not found: " + request.getReceiverEmail());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      MessageRequest internalRequest = new MessageRequest();
      internalRequest.setSender(sender.get().getEmail());
      internalRequest.setReceiver(receiver.get().getEmail());
      internalRequest.setContent(request.getContent());

      MessageResponse response = messageService.sendMessage(internalRequest);
      logger.info("Message sent successfully from user: " + request.getSenderEmail() + " to user: " + request.getReceiverEmail());
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
  @Operation(
          summary = "Get user's inbox",
          description = "Retrieves a summary of the conversations in the user's inbox.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "Bearer token for authentication")
          },
          responses = {
                  @ApiResponse(responseCode = "200", description = "Inbox retrieved successfully",
                          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConversationSummaryResponse.class, type = "array"))),
                  @ApiResponse(responseCode = "401", description = "Unauthorized - User not found"),
                  @ApiResponse(responseCode = "404", description = "No messages found"),
                  @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
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
   * It extracts the token from the request, validates it, and retrieves the conversation using the MessageService.
   * If successful, it returns a list of MessageResponse objects.
   * If the users are not found or an error occurs, it returns an appropriate HTTP status.
   *
   * @param request      The HTTP request containing the authorization token
   * @param endUserEmail The email of the other user in the conversation
   * @return ResponseEntity with the list of MessageResponse or an error status
   */
  @Operation(
          summary = "Get conversation with another user",
          description = "Retrieves the conversation history between the authenticated user and another user.",
          security = @SecurityRequirement(name = "BearerAuth"),
          parameters = {
                  @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "Bearer token for authentication"),
                  @Parameter(name = "endUserEmail", in = ParameterIn.PATH, required = true, description = "Email of the other user in the conversation")
          },
          responses = {
                  @ApiResponse(responseCode = "200", description = "Conversation retrieved successfully",
                          content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class, type = "array"))),
                  @ApiResponse(responseCode = "401", description = "Unauthorized - Current user not found"),
                  @ApiResponse(responseCode = "404", description = "Other user not found"),
                  @ApiResponse(responseCode = "400", description = "Bad Request - Cannot fetch conversation with yourself"),
                  @ApiResponse(responseCode = "500", description = "Internal server error")
          }
  )
  @GetMapping("/conversation/{endUserEmail}")
  public ResponseEntity<List<MessageResponse>> getConversation(HttpServletRequest request,
                                                               @PathVariable String endUserEmail) {
    try {
      String token = extractToken(request);
      String currentUserEmail = jwtUtil.extractUsername(token);

      User currentUser = userService.findByEmail(currentUserEmail)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
      User endUser = userService.findByEmail(endUserEmail)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

      if (currentUserEmail.equals(endUserEmail)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Cannot fetch conversation with yourself");
      }

      List<MessageResponse> messages = messageService
              .getConversation(currentUser.getId(), endUser.getId())
              .stream()
              .map(message -> new MessageResponse(
                      message.getSender(),
                      message.getReceiver(),
                      message.getContent(),
                      message.getTimestamp(),
                      message.isRead()
              ))
              .toList();

      return ResponseEntity.ok(messages);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      logger.warning("Error retrieving conversation: " + e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
              "Error retrieving conversation");
    }
  }
}

