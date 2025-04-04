package ntnu.idatt2105.project.backend.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
 * This class represents a user in the system.
 * It contains the user's information such as username, password, and roles.
 * The class is used for authentication and authorization purposes.
 * It is part of the backend model package and is used in conjunction with the UserRepo interface
 * to interact with the database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private long id;

  private String firstname;

  private String lastname;

  private String email;

  private String phoneNumber;

  private String password;

  private boolean isAdmin;

  private Date createdAt;

  /**
   * Constructor for User class. This constructor is used to create a new user object, where
   * isAdmin is set to false by default.
   *
   * @param firstname   the first name of the user
   * @param lastname    the last name of the user
   * @param email       the email of the user
   * @param phoneNumber the phone number of the user
   * @param password    the password of the user
   */
  public User(String firstname, String lastname, String email, String phoneNumber, String password) {

    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.password = password;
    this.isAdmin = false;
  }

  /**
   * Constructor for User class. This constructor is used to create a new user object
   * including if they are admin or not.
   *
   * @param firstname   the first name of the user
   * @param lastname    the last name of the user
   * @param email       the email of the user
   * @param phoneNumber the phone number of the user
   * @param password    the password of the user
   * @param isAdmin     the admin status of the user
   */
  public User(String firstname, String lastname, String email, String phoneNumber, String password, boolean isAdmin) {

    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email.toLowerCase();
    this.phoneNumber = phoneNumber;
    this.password = password;
    this.isAdmin = isAdmin;
  }
}

    
