package ntnu.idatt2105.project.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateToken() {

        String username = "testuser";

        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
    }

    @Test
    void testExtractUsername() {

        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken() {

        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        UserDetails userDetails = User.withUsername(username).password("password").authorities("USER").build();

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenExpired() {

        String expiredToken = Jwts.builder()
            .subject("testuser")
            .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 time siden
            .expiration(new Date(System.currentTimeMillis() - 1000 * 60)) // Utløpt for 1 minutt siden
            .signWith(Keys.hmacShaKeyFor(jwtUtil.getKey().getEncoded()))
            .compact();

        boolean isExpired = jwtUtil.isTokenExpired(expiredToken);

        assertTrue(isExpired);
    }
    @Test
    void testIsTokenNotExpired() {

        String notExpiredToken = Jwts.builder()
            .subject("testuser")
            .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 time siden
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60)) // Utløper om 1 minutt
            .signWith(Keys.hmacShaKeyFor(jwtUtil.getKey().getEncoded()))
            .compact();

        boolean isExpired = jwtUtil.isTokenExpired(notExpiredToken);

        assertFalse(isExpired);
    }
}