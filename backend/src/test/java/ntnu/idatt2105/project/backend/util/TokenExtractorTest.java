package ntnu.idatt2105.project.backend.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class TokenExtractorTest {
  private final TokenExtractor tokenExtractor = new TokenExtractor();

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

  @Test
  void extractToken_validBearerTokenString_returnsToken() {
    String authHeaderValue = "Bearer another.valid.token";

    String token = tokenExtractor.extractToken(authHeaderValue);

    assertEquals("another.valid.token", token);
  }

  @Test
  void extractToken_authorizationHeaderMissingInRequest_returnsNull() {
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    String token = tokenExtractor.extractToken(mockRequest);

    assertNull(token);
  }

  @Test
  void extractToken_authorizationHeaderMissingString_returnsNull() {
    String authHeaderValue = null;

    String token = tokenExtractor.extractToken(authHeaderValue);

    assertNull(token);
  }

  @Test
  void extractToken_authorizationHeaderDoesNotStartWithBearerInRequest_returnsNull() {
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic " +
            "dXNlcjpwYXNzd29yZA==");

    String token = tokenExtractor.extractToken(mockRequest);

    assertNull(token);
  }

  @Test
  void extractToken_authorizationHeaderDoesNotStartWithBearerString_returnsNull() {
    String authHeaderValue = "Basic dXNlcjpwYXNzd29yZA==";

    String token = tokenExtractor.extractToken(authHeaderValue);

    assertNull(token);
  }

  @Test
  void extractToken_authorizationHeaderOnlyBearerNoTokenInRequest_returnsEmptyString() {
    HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer ");

    String token = tokenExtractor.extractToken(mockRequest);

    assertEquals("", token);
  }

  @Test
  void extractToken_authorizationHeaderOnlyBearerNoTokenString_returnsEmptyString() {
    String authHeaderValue = "Bearer ";

    String token = tokenExtractor.extractToken(authHeaderValue);

    assertEquals("", token);
  }
}