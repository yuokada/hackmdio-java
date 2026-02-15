package io.github.yuokada.quarkus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.yuokada.quarkus.model.NoteDetailResponse.LastChangeUser;
import java.time.Instant;
import java.util.List;

public record IndexedNote(
    //  Give the prefix "note::" when storing in Couchbase Lite
    String id,
    String originalId,
    String title,
    List<String> tags,
    @JsonProperty("createdAt") Instant createdAt,
    @JsonProperty("titleUpdatedAt") Instant titleUpdatedAt,
    @JsonProperty("tagsUpdatedAt") Instant tagsUpdatedAt,
    String publishType,
    @JsonProperty("publishedAt") Instant publishedAt,
    String permalink,
    String publishLink,
    String shortId,
    String content,
    @JsonProperty("lastChangedAt") Instant lastChangedAt,
    @JsonProperty("lastChangeUser") LastChangeUser lastChangeUser,
    String userPath,
    String teamPath,
    String readPermission,
    String writePermission) {

  private static final String ID_PREFIX = "note::";

  public static IndexedNote fromNote(NoteDetailResponse note) {
    return new IndexedNote(
        ID_PREFIX + note.id(),
        note.id(),
        note.title(),
        note.tags(),
        note.createdAt(),
        note.titleUpdatedAt(),
        note.tagsUpdatedAt(),
        note.publishType(),
        note.publishedAt(),
        note.permalink(),
        note.publishLink(),
        note.shortId(),
        note.content(),
        note.lastChangedAt(),
        note.lastChangeUser(),
        note.userPath(),
        note.teamPath(),
        note.readPermission(),
        note.writePermission());
  }
}
