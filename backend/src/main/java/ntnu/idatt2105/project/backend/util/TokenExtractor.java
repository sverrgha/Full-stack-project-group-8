package ntnu.idatt2105.project.backend.util;

import jakarta.servlet.http.HttpServletRequest;

public class TokenExtractor {
  public static String extractToken(String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }
    return null;
  }

  public static String extractToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");
    return extractToken(authorizationHeader);
  }
}
