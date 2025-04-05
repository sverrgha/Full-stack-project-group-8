package ntnu.idatt2105.project.backend.controller;

import ntnu.idatt2105.project.backend.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private static final Logger logger = Logger.getLogger(UserController.class.getName());
//  private final UserService userService;
}
