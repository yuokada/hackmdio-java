package io.github.yuokada.hackmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for AuthorizationFilter.
 */
class AuthorizationFilterTest {

  private AuthorizationFilter filter;
  private ClientRequestContext requestContext;
  private MultivaluedMap<String, Object> headers;

  @BeforeEach
  void setUp() {
    filter = new AuthorizationFilter();
    requestContext = mock(ClientRequestContext.class);
    headers = new MultivaluedHashMap<>();
    when(requestContext.getHeaders()).thenReturn(headers);
  }

  @Test
  void testFilterAddsAuthorizationHeaderWithValidToken() {
    // Given
    filter.apiToken = "test-api-token-12345";

    // When
    filter.filter(requestContext);

    // Then
    assertTrue(headers.containsKey("Authorization"));
    assertEquals("Bearer test-api-token-12345", headers.getFirst("Authorization"));
  }

  @Test
  @DisplayName("Test filter does not add header when token is null")
  void testFilterDoesNotAddHeaderWhenTokenIsNull() {
    filter.apiToken = null;
    filter.filter(requestContext);
    assertFalse(headers.containsKey("Authorization"));
  }

  @Test
  @DisplayName("Test filter does not add header when token is empty")
  void testFilterDoesNotAddHeaderWhenTokenIsEmpty() {
    // Given
    filter.apiToken = "";

    // When
    filter.filter(requestContext);

    // Then
    assertFalse(headers.containsKey("Authorization"));
  }

  @Test
  @DisplayName("Test filter does not add header when token is blank")
  void testFilterDoesNotAddHeaderWhenTokenIsBlank() {
    filter.apiToken = "   ";
    filter.filter(requestContext);
    assertFalse(headers.containsKey("Authorization"));
  }

  @Test
  void testFilterAddsHeaderWithCorrectBearerPrefix() {
    filter.apiToken = "my-token";
    filter.filter(requestContext);
    assertEquals("Bearer my-token", headers.getFirst("Authorization"));
  }

  @Test
  @DisplayName("Test filter handles token with leading and trailing spaces")
  void testFilterHandlesTokenWithSpaces() {
    filter.apiToken = "token with spaces";
    filter.filter(requestContext);
    assertEquals("Bearer token with spaces", headers.getFirst("Authorization"));
  }

  @Test
  @DisplayName("Test filter handles special characters in token")
  void testFilterHandlesSpecialCharactersInToken() {
    // Given
    filter.apiToken = "token!@#$%^&*()_+-=[]{}|;':,.<>?";

    // When
    filter.filter(requestContext);

    // Then
    assertEquals("Bearer token!@#$%^&*()_+-=[]{}|;':,.<>?", headers.getFirst("Authorization"));
  }
}
