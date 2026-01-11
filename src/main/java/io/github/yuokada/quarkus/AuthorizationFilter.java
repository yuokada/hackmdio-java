package io.github.yuokada.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * A client request filter that adds the Authorization header to all outgoing requests.
 */
@ApplicationScoped
public class AuthorizationFilter implements ClientRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(AuthorizationFilter.class.getName());

    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    @ConfigProperty(name = "hackmd.api.token", defaultValue = "")
    String apiToken;

    @Override
    public void filter(ClientRequestContext requestContext) {
        if (apiToken != null && !apiToken.isBlank()) {
            String authorization = BEARER_PREFIX + apiToken;
            requestContext.getHeaders().add("Authorization", authorization);
        } else {
            LOGGER.warn("API token is not set. Authorization header will not be added.");
        }
    }
}
