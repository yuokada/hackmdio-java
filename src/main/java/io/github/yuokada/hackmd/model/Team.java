package io.github.yuokada.hackmd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;

/**
 * Represents a HackMD team definition.
 */
public record Team(
    String id,
    String ownerId,
    String name,
    String logo,
    String path,
    String description,
    TeamVisibility visibility,
    boolean upgraded,
    @JsonProperty("createdAt") @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
        Instant createdAt) {}
