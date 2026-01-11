package io.github.yuokada.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
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
  void testFilterAddsAuthorizationHeaderWithValidToken() throws IOException {
    // Given
    filter.apiToken = "test-api-token-12345";

    // When
    filter.filter(requestContext);

    // Then
    assertTrue(headers.containsKey("Authorization"));
    assertEquals("Bearer test-api-token-12345", headers.getFirst("Authorization"));
  }

  @Test
  void testFilterDoesNotAddHeaderWhenTokenIsNull() throws IOException {
    // Given
    filter.apiToken = null;

    // When
    filter.filter(requestContext);

    // Then
    assertFalse(headers.containsKey("Authorization"));
  }

  @Test
  void testFilterDoesNotAddHeaderWhenTokenIsEmpty() throws IOException {
    // Given
    filter.apiToken = "";

    // When
    filter.filter(requestContext);

    // Then
    assertFalse(headers.containsKey("Authorization"));
  }

  @Test
  void testFilterDoesNotAddHeaderWhenTokenIsBlank() throws IOException {
    // Given
    filter.apiToken = "   ";

    // When
    filter.filter(requestContext);

    // Then
    assertFalse(headers.containsKey("Authorization"));
  }

  @Test
  void testFilterAddsHeaderWithCorrectBearerPrefix() throws IOException {
    // Given
    filter.apiToken = "my-token";

    // When
    filter.filter(requestContext);

    // Then
    String authHeader = (String) headers.getFirst("Authorization");
    assertTrue(authHeader.startsWith("Bearer "));
    assertEquals("Bearer my-token", authHeader);
  }

  @Test
  void testFilterHandlesTokenWithSpaces() throws IOException {
    // Given
    filter.apiToken = "token with spaces";

    // When
    filter.filter(requestContext);

    // Then
    assertEquals("Bearer token with spaces", headers.getFirst("Authorization"));
  }

  @Test
  void testFilterHandlesSpecialCharactersInToken() throws IOException {
    // Given
    filter.apiToken = "token!@#$%^&*()_+-=[]{}|;':,.<>?";

    // When
    filter.filter(requestContext);

    // Then
    assertEquals("Bearer token!@#$%^&*()_+-=[]{}|;':,.<>?", headers.getFirst("Authorization"));
  }
}
