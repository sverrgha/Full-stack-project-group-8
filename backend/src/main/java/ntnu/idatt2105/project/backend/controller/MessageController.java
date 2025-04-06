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

    @GetMapping("/inbox")
    public ResponseEntity<?> getInbox(HttpServletRequest request) {
        logger.info("Received inbox request");
        try {
            String token = extractToken(request);
            String email = jwtUtil.extractUsername(token); 
            Optional<User> user = userRepo.findByEmail(email);
    
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
    
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

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

