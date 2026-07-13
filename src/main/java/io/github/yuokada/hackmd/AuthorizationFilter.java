package io.github.yuokada.hackmd;

import io.github.yuokada.hackmd.auth.HackmdCredentialsProvider;
import io.github.yuokada.hackmd.auth.HackmdRequestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import org.jboss.logging.Logger;

/**
 * A client request filter that adds the Authorization header to all outgoing requests.
 */
@ApplicationScoped
public class AuthorizationFilter implements ClientRequestFilter {

  private static final Logger LOGGER = Logger.getLogger(AuthorizationFilter.class.getName());

  private static final String BEARER_PREFIX = "Bearer ";

  @Inject HackmdCredentialsProvider credentialsProvider;

  @Override
  public void filter(ClientRequestContext requestContext) {
    HackmdRequestContext context =
        new HackmdRequestContext(requestContext.getMethod(), requestContext.getUri());
    String apiToken = credentialsProvider.token(context);
    if (apiToken != null && !apiToken.isBlank()) {
      String authorization = BEARER_PREFIX + apiToken;
      requestContext.getHeaders().add("Authorization", authorization);
    } else {
      LOGGER.warn("API token is not set. Authorization header will not be added.");
    }
  }
}
