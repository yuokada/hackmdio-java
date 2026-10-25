package io.github.yuokada.hackmd.auth;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/** Default credentials provider backed by application configuration. */
@DefaultBean
@ApplicationScoped
public class ConfigHackmdCredentialsProvider implements HackmdCredentialsProvider {

    private final String apiToken;

    @Inject
    public ConfigHackmdCredentialsProvider(
            @ConfigProperty(name = "hackmd.api.token", defaultValue = "") String apiToken) {
        this.apiToken = apiToken;
    }

    @Override
    public String token(HackmdRequestContext context) {
        return apiToken;
    }
}
