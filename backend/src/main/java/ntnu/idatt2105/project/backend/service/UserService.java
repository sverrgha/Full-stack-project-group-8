package ntnu.idatt2105.project.backend.service;

import java.util.ArrayList;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.LoginRequest;
import ntnu.idatt2105.project.backend.dto.request.ModifyUserRequest;
import ntnu.idatt2105.project.backend.dto.request.RegisterRequest;
import ntnu.idatt2105.project.backend.dto.response.AuthResponse;
import ntnu.idatt2105.project.backend.dto.response.UserResponse;
import ntnu.idatt2105.project.backend.enums.AuthResponseMessage;
import ntnu.idatt2105.project.backend.security.JwtUtil;
import ntnu.idatt2105.project.backend.util.PasswordUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ntnu.idatt2105.project.backend.model.User;
import ntnu.idatt2105.project.backend.repository.UserRepo;


/**
 * This class implements the UserDetailsService interface to load user-specific data.
 * It is used by Spring Security to retrieve user details during authentication.
 * The loadUserByUsername method retrieves the user from the database using the UserRepo interface.
 * If the user is not found, it throws a UsernameNotFoundException.
 * The method returns a UserDetails object containing the user's information.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepo userRepo;
  private final JwtUtil jwtUtil;

  /**
   * This method registers a new user in the system. It takes a RegisterRequest object and
   * stores it if a user with the same email does not already exist.
   *
   * @param request The RegisterRequest object containing the user's information.
   * @return A RegisterResponse object containing the user's email, a message, and a JWT token
   * (token is null if user is not saved).
   */
  public AuthResponse registerUser(RegisterRequest request) {
    String email = request.getEmail();
    String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
    String firstName = request.getFirstname();
    String lastName = request.getLastname();
    String phoneNumber = request.getPhoneNumber();

    Optional<User> existingUser = userRepo.findByEmail(email);
    if (existingUser.isPresent()) {
      return new AuthResponse(email, AuthResponseMessage
              .USER_ALREADY_EXISTS.getMessage(), null, null);
    }

    try {
      userRepo.save(new User(firstName, lastName, email, phoneNumber, hashedPassword));
    } catch (Exception e) {
      return new AuthResponse(email, AuthResponseMessage
              .SAVING_USER_ERROR.getMessage() + e.getMessage(), null, null);
    }
    String token = jwtUtil.generateToken(email);

    return new AuthResponse(email, AuthResponseMessage
            .USER_REGISTERED_SUCCESSFULLY.getMessage(), token,
            jwtUtil.getExpirationDate(token));
  }

  /**
   * This method logs in a user by verifying their email and password.
   * It takes a LoginRequest object and checks if the user exists
   * and if the password is correct.
   * If the login is successful, it generates a JWT token.
   *
   * @param request The LoginRequest object containing the user's email and password.
   * @return A LoginResponse object containing the user's email, a message, and a JWT token
   */
  public AuthResponse loginUser(LoginRequest request) {
    String email = request.getEmail();
    Optional<User> user = userRepo.findByEmail(email);
    if (user.isEmpty()) {
      return new AuthResponse(email, AuthResponseMessage.USER_NOT_FOUND.getMessage(), null, null);
    }
    if (!PasswordUtil.verifyPassword(request.getPassword(), user.get().getPassword())) {
      return new AuthResponse(email, AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), null, null);
    }

    String token = jwtUtil.generateToken(email);

    return new AuthResponse(email,
            AuthResponseMessage.USER_LOGGED_IN_SUCCESSFULLY.getMessage(), token,
            jwtUtil.getExpirationDate(token));
  }

  /**
   * This method is called by Spring Security to load user-specific data.
   * It retrieves the user from the database using the UserRepo interface.
   * If the user is not found, it throws a UsernameNotFoundException.
   * The method returns a UserDetails object containing the user's information.
   *
   * @param email The email of the user to be loaded.
   * @return A UserDetails object containing the user's information.
   * @throws UsernameNotFoundException If the user is not found in the database.
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
            new ArrayList<>());
  }

  /**
   * This method checks if a user exists in the database by their ID.
   *
   * @param id The ID of the user to be checked.
   * @return true if the user exists, false otherwise.
   */
  public boolean userExists(long id) {
    return userRepo.findById(id).isPresent();
  }

  /**
   * This method checks if the user ID matches the email in the JWT token.
   * It retrieves the user from the database using the UserRepo interface
   * and compares the email in the token with the user's email.
   * @param id The ID of the user to be checked.
   * @param token The JWT token containing the user's email.
   * @return true if the user ID matches the email in the token, false otherwise.
   */
  public boolean validateUserIdMatchesToken(long id, String token) {
    Optional<User> user = userRepo.findById(id);
    if (user.isPresent()) {
      String email = user.get().getEmail();
      String tokenEmail = jwtUtil.extractUsername(token);
      return email.equals(tokenEmail);
    } else {
      return false;
    }
  }
  public UserResponse getUserById(Long id) {
    User user = userRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No user found with id: " + id));
    return new UserResponse(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(),
            user.getPhoneNumber(), user.isAdmin(), user.getCreatedAt());
  }


  public void updateUser(Long id, ModifyUserRequest request, String token) {
    if (!validateUserIdMatchesToken(id, token)) {
      throw new IllegalArgumentException("User ID does not match the token");
    }

    User oldUser = userRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No user found with id: " + id));

    String firstName = request.getFirstname() != null ? request.getFirstname()
            : oldUser.getFirstname();
    String lastName = request.getLastname() != null && !request.getLastname().isBlank()
            ? request.getLastname() : oldUser.getLastname();
    String email = request.getEmail() != null && request.getEmail()
            .matches("^[A-Za-z0-9+_.-]+@(.+)$") ? request.getEmail()
            : oldUser.getEmail();
    String phoneNumber = request.getPhoneNumber() != null &&
            !request.getPhoneNumber().isBlank() ? request.getPhoneNumber()
            : oldUser.getPhoneNumber();
    String password = PasswordUtil.hashPassword(request.getPhoneNumber() != null &&
            request.getPhoneNumber().length() >= 8 ? request.getPhoneNumber()
            : oldUser.getPhoneNumber());


    User newUser = new User(
            oldUser.getId(),
            firstName,
            lastName,
            email,
            phoneNumber,
            password,
            oldUser.isAdmin(),
            oldUser.getCreatedAt()
    );

    userRepo.update(newUser);
  }
}
