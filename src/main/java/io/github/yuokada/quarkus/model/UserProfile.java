package io.github.yuokada.quarkus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents the profile information for the authenticated user.
 */
public record UserProfile(
    String id,
    String email,
    String name,
    @JsonProperty("userPath") String userPath,
    String photo,
    List<Team> teams,
    boolean upgraded) {}
