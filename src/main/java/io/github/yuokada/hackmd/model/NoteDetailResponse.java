package io.github.yuokada.hackmd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.List;

/**
 * Detailed HackMD note response.
 *
 * <p>Based on example/note-detail.json, represents the structure of the HackMD API response.</p>
 */
public record NoteDetailResponse(
    String id,
    String title,
    List<String> tags,
    @JsonProperty("createdAt") @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
        Instant createdAt,
    @JsonProperty("titleUpdatedAt") @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
        Instant titleUpdatedAt,
    @JsonProperty("tagsUpdatedAt") @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
        Instant tagsUpdatedAt,
    String publishType,
    @JsonProperty("publishedAt") @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
        Instant publishedAt,
    String permalink,
    String publishLink,
    String shortId,
    String content,
    @JsonProperty("lastChangedAt") @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
        Instant lastChangedAt,
    @JsonProperty("lastChangeUser") LastChangeUser lastChangeUser,
    String userPath,
    String teamPath,
    String readPermission,
    String writePermission) {

  public record LastChangeUser(String name, String userPath, String photo, String biography) {}

  public Note toNote() {
    return new Note(
        this.id,
        this.title,
        this.content,
        this.tags,
        this.publishedAt,
        this.shortId,
        this.lastChangedAt);
  }
}
