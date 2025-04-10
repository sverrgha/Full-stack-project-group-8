package ntnu.idatt2105.project.backend.security;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for generating and validating JWT tokens.
 * It uses the HmacSHA256 algorithm to sign the tokens.
 * The secret key is generated using the KeyGenerator class.
 * The tokens are generated with a subject (username), issued date, expiration date,
 * and claims.
 * 
 */

@Component
public class JwtUtil {
    /**
     * The secret key is used to sign the JWT tokens.
     */
    public String secretKey = "";
    /**
     * The constructor generates a secret key using the HmacSHA256 algorithm.
     * The key is then encoded to a Base64 string for storage.
     */
    public JwtUtil() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method generates a JWT token with the given username.
     * The token is signed with the secret key and has an expiration time of 60 minutes.
     * 
     * @param username The username to be included in the token.
     * @return The generated JWT token.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) // 5 min
                .and()
                .signWith(getKey())
                .compact();
    }

    /**
     * This method extracts the username from the given JWT token.
     * 
     * @param token The JWT token from which to extract the username.
     * @return The username extracted from the token.
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(getKey().getEncoded()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * This method validates the given JWT token against the provided user details.
     * It checks if the username in the token matches the username in the user details
     * and if the token is not expired.
     * 
     * @param token The JWT token to be validated.
     * @param userDetails The user details to validate against.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * This method checks if the given JWT token is expired.
     * 
     * @param token The JWT token to be checked.
     * @return true if the token is expired, false otherwise.
     */
    boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(getKey().getEncoded()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * This method retrieves the expiration date from the given JWT token.
     * It returns null if the token is invalid or expired.
     * @param token The JWT token from which to extract the expiration date.
     * @return The expiration date of the token, or null if the token is invalid or expired.
     */
    public Date getExpirationDate(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(getKey().getEncoded()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    /**
     * This method retrieves the secret key used to sign the JWT tokens.
     * 
     * @return The secret key as a Key object.
     */
    Key getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
