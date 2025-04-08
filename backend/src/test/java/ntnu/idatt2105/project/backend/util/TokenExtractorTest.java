package ntnu.idatt2105.project.backend.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the TokenExtractor class.
 * This class is responsible for extracting Bearer tokens from HTTP requests or authString.
 * It includes tests for various scenarios, including valid and invalid tokens.
 */
class TokenExtractorTest {
  private final TokenExtractor tokenExtractor = new TokenExtractor();

  /**
   * Test class for extracting a valid Bearer token from an HttpServletRequest.
   * Validates the token format and ensures it returns the correct token.
   */
  @Test
  void extractToken_validBearerTokenInRequest_returnsToken() {
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    String authHeaderValue = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0Ijox"
            + "NTE2MjM5MDIyfQ.Sfl11xZ-4K8x_ZkOUj7t6-ehJ16i_T-UFlmC3l6vT2Q";
    when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeaderValue);

    String token = tokenExtractor.extractToken(mockRequest);

    assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI"
            + "xMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
            "Sfl11xZ-4K8x_ZkOUj7t6-ehJ16i_T-UFlmC3l6vT2Q", token);
  }

  /**
   * Test class for extracting a valid Bearer token from a String.
   * Validates the token format and ensures it returns the correct token.
   */
  @Test
  void extractToken_validBearerTokenString_returnsToken() {
    String authHeaderValue = "Bearer another.valid.token";

    String token = tokenExtractor.extractToken(authHeaderValue);

    assertEquals("another.valid.token", token);
  }

  /**
   * Test class for extracting a valid Bearer token from an HttpServletRequest.
   * Validates the token null is returned when the authHeader is missing in
   * the request.
   */
  @Test
  void extractToken_authorizationHeaderMissingInRequest_returnsNull() {
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    String token = tokenExtractor.extractToken(mockRequest);

    assertNull(token);
  }

  /**
   * Test class for extracting a valid Bearer token from a String.
   * Validates the token null is returned when the authHeader has no
   * string value.
   */
  @Test
  void extractToken_authorizationHeaderMissingString_returnsNull() {
    String authHeaderValue = null;

    String token = tokenExtractor.extractToken(authHeaderValue);

    assertNull(token);
  }

  /**
   * Test class for extracting a valid Bearer token from an HttpServletRequest.
   * Validates the token null is returned when the authHeader does not
   * start with the Bearer string.
   */
  @Test
  void extractToken_authorizationHeaderDoesNotStartWithBearerInRequest_returnsNull() {
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " +
            "dXNlcjpwYXNzd29yZA==");

    String token = tokenExtractor.extractToken(mockRequest);

    assertNull(token);
  }

  /**
   * Test class for extracting a valid Bearer token from a String.
   * Validates the token null is returned when the authHeader does not
   * start with the Bearer string.
   */
  @Test
  void extractToken_authorizationHeaderDoesNotStartWithBearerString_returnsNull() {
    String authHeaderValue = "Basic dXNlcjpwYXNzd29yZA==";

    String token = tokenExtractor.extractToken(authHeaderValue);

    assertNull(token);
  }

  /**
   * Test class for extracting a valid Bearer token from an HttpServletRequest.
   * Validates the token empty string is returned when the authHeader
   * is empty.
   */
  @Test
  void extractToken_authorizationHeaderOnlyBearerNoTokenInRequest_returnsEmptyString() {
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer ");

    String token = tokenExtractor.extractToken(mockRequest);

    assertEquals("", token);
  }

  /**
   * Test class for extracting a valid Bearer token from a String.
   * Validates the token empty string is returned when the authHeader
   * is empty.
   */
  @Test
  void extractToken_authorizationHeaderOnlyBearerNoTokenString_returnsEmptyString() {
    String authHeaderValue = "Bearer ";

    String token = tokenExtractor.extractToken(authHeaderValue);

    assertEquals("", token);
  }
}