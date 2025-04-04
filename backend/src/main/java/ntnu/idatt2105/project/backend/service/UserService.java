package ntnu.idatt2105.project.backend.service;

import java.util.ArrayList;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.dto.request.LoginRequest;
import ntnu.idatt2105.project.backend.dto.request.RegisterRequest;
import ntnu.idatt2105.project.backend.dto.response.LoginResponse;
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
   *      (token is null if user is not saved).
   */
  public LoginResponse registerUser(RegisterRequest request) {
    String email = request.getEmail();
    String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
    String firstName = request.getFirstname();
    String lastName = request.getLastname();
    String phoneNumber = request.getPhoneNumber();

    Optional<User> existingUser = userRepo.findByEmail(email);
    if (existingUser.isPresent()) {
      return new LoginResponse(email, "User already exists", null);
    }

    try {
      userRepo.save(new User(firstName, lastName, email, phoneNumber, hashedPassword));
    } catch (Exception e) {
      return new LoginResponse(email, "Error saving user: " + e.getMessage(), null);
    }
    String token = jwtUtil.generateToken(email);

    return new LoginResponse(email, "User registered successfully", token);
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
  public LoginResponse loginUser(LoginRequest request) {
    String email = request.getEmail();
    Optional<User> user = userRepo.findByEmail(email);
    if (user.isEmpty()) {
      return new LoginResponse(email, "User not found", null);
    }
    if (!PasswordUtil.verifyPassword(request.getPassword(), user.get().getPassword())) {
      return new LoginResponse(email, "Invalid password", null);
    }

    String token = jwtUtil.generateToken(email);

    return new LoginResponse(email, "User logged in successfully", token);
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
}
