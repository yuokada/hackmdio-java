package io.github.yuokada.hackmd.auth;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/** Default credentials provider backed by application configuration. */
@ApplicationScoped
public class ConfigHackmdCredentialsProvider implements HackmdCredentialsProvider {

  @ConfigProperty(name = "hackmd.api.token", defaultValue = "")
  String apiToken;

  @Override
  public String token(HackmdRequestContext context) {
    return apiToken;
  }
}
