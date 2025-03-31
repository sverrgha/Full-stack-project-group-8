package ntnu.idatt2105.project.backend.model;

import java.sql.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/*
 * This class represents a user in the system.
 * It contains the user's information such as username, password, and roles.
 * The class is used for authentication and authorization purposes.
 * It is part of the backend model package and is used in conjunction with the UserRepo interface
 * to interact with the database.
 */

@Table("users")
public class User {
    
    @Id
    private long id;

    @Column("firstname")
    private String firstname;
    
    @Column("lastname")
    private String lastname;
    
    @Column("email")
    private String email;
    
    @Column("phonenumber")
    private long phonenumber;
    
    @Column("password")
    private String password;
    
    @Column("admin")
    private boolean admin;

    @Column("createdAt")
    private Date createdAt;
    

    public User() {
    }

    public User(long id, String firstname, String lastname, String email, long phonenumber, String password) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phonenumber = phonenumber;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(long phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}

    
