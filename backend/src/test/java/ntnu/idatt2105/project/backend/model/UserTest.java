package ntnu.idatt2105.project.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;

import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    void testUserConstructorAndGetters() {

        long id = 1L;
        String firstname = "Sverre";
        String lastname = "Goat";
        String email = "sv.goat@SecondGo.com";
        String phonenumber = "+47 123 45 678";
        String password = "password123";
        boolean admin = true;
        Date createdAt = Date.valueOf("2025-01-01");

        User user = new User(id, firstname, lastname, email, phonenumber, password, admin, createdAt);

        assertEquals(id, user.getId());
        assertEquals(firstname, user.getFirstname());
        assertEquals(lastname, user.getLastname());
        assertEquals(email, user.getEmail());
        assertEquals(phonenumber, user.getPhoneNumber());
        assertEquals(password, user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(createdAt, user.getCreatedAt());
    }
    @Test
    void testSetters() {

        User user = new User();
        long id = 2L;
        String firstname = "Anders";
        String lastname = "God";
        String email = "anderpanders.god@SecondGo.com";
        String phonenumber = "+45 87654321";
        String password = "securepassword";
        boolean admin = false;
        Date createdAt = Date.valueOf("2025-02-01");

        user.setId(id);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setPhoneNumber(phonenumber);
        user.setPassword(password);
        user.setAdmin(admin);
        user.setCreatedAt(createdAt);

        assertEquals(id, user.getId());
        assertEquals(firstname, user.getFirstname());
        assertEquals(lastname, user.getLastname());
        assertEquals(email, user.getEmail());
        assertEquals(phonenumber, user.getPhoneNumber());
        assertEquals(password, user.getPassword());
        assertFalse(user.isAdmin());
        assertEquals(createdAt, user.getCreatedAt());
    }
    @Test
    void testEqualsAndHashCode() {

        User user1 = new User(1L, "Emil", "Fantastic", "emil.fan@secondgo.com", "+48 12345678", "password123", true, Date.valueOf("2025-01-01"));
        User user2 = new User(1L, "Emil", "Fantastic", "emil.fan@secondgo.com", "+48 12345678", "password123", true, Date.valueOf("2025-01-01"));
        User user3 = new User(2L, "Lukas", "Arntsen", "luke.arne@bestson.com", "87654321", "securepassword", false, Date.valueOf("2025-02-01"));

        assertEquals(user1, user2); 
        assertNotEquals(user1, user3); 
        assertEquals(user1.hashCode(), user2.hashCode()); 
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }
    @Test
    void testToString() {

        User user = new User(1L, "Johan", "Best", "best.jo@secondgo.com", "12345678", "password123", true, Date.valueOf("2025-01-01"));

        String userString = user.toString();

        assertTrue(userString.contains("Johan"));
        assertTrue(userString.contains("Best"));
        assertTrue(userString.contains("best.jo@secondgo.com"));
    }
}
