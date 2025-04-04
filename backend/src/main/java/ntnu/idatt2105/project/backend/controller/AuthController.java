package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private UserService userService;
  private static final Logger logger = Logger.getLogger(AuthController.class.getName());

  @GetMapping("/register")
  public String registerUser() {
    logger.info("Registering user");
    return "User registered successfully";
  }
}
