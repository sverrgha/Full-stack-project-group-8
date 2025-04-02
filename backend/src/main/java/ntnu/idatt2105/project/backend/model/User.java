package ntnu.idatt2105.project.backend.model;

import java.sql.Date;

import org.springframework.data.relational.core.mapping.Table;

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
@Table("users")
public class User {
    
    private long id;

    private String firstname;
    
    private String lastname;
    
    private String email;
    
    private String phonenumber;
    
    private String password;
    
    private boolean admin;

    private Date createdAt;
}

    
