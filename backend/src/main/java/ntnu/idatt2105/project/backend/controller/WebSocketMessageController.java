package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.dto.response.MessageResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageController {

  @MessageMapping("/chat/send")
  @SendTo("/topic/messages")
  public MessageResponse sendMessage(MessageResponse message) {
    // Here you can process the incoming message and send it to the topic
    return message; // Echoing back the received message for demonstration
  }
}
