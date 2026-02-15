package io.github.yuokada.quarkus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

/**
 * Represents a HackMD note.
 *
 * @param id            The note ID.
 * @param title         The title of the note.
 * @param content       The content of the note.
 * @param tags          The tags associated with the note.
 * @param publishedAt   The timestamp when the note was published.
 * @param shortId       The short ID of the note.
 * @param lastChangedAt The timestamp when the note was last updated.
 */
public record Note(
    String id,
    String title,
    String content,
    List<String> tags,
    @JsonProperty("publishedAt")
    Instant publishedAt,
    @JsonProperty("shortId")
    String shortId,
    @JsonProperty("lastChangedAt")
    Instant lastChangedAt
) {
}
