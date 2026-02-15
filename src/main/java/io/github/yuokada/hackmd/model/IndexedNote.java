package io.github.yuokada.hackmd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.yuokada.hackmd.model.NoteDetailResponse.LastChangeUser;
import java.time.Instant;
import java.util.List;

/**
 * @param id The ID used for indexing, prefixed with "note::" to avoid collisions with other document types.
 * @param originalId The original note ID from HackMD, used for reference and retrieval.
 * @param title The title of the note, which can be used for searching and display.
 * @param tags The list of tags associated with the note, which can be used for searching and categorization.
 * @param createdAt The timestamp when the note was created, used for sorting and filtering.
 * @param titleUpdatedAt The timestamp when the title was last updated, used for sorting and filtering.
 * @param tagsUpdatedAt The timestamp when the tags were last updated, used for sorting and filtering.
 * @param publishType The publish type of the note (e.g., "published", "unpublished"), used for filtering.
 * @param publishedAt The timestamp when the note was published, used for sorting and filtering.
 * @param permalink The permalink URL of the note, which can be used for direct access.
 * @param publishLink The publish link URL of the note, which can be used for direct access.
 * @param shortId The short ID of the note, which can be used for display and reference.
 * @param content The content of the note, which can be used for searching and display.
 * @param lastChangedAt The timestamp when the note was last changed, used for sorting and filtering.
 * @param lastChangeUser The user who last changed the note, which can be used for display and reference.
 * @param userPath The user path of the note, which can be used for filtering and reference.
 * @param teamPath The team path of the note, which can be used for filtering and reference.
 * @param readPermission The read permission level of the note, which can be used for filtering and reference.
 * @param writePermission The write permission level of the note, which can be used for filtering and reference.
 */
public record IndexedNote(
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

  /**
   * Factory method to create an IndexedNote from a NoteDetailResponse.
   *
   * @param note The NoteDetailResponse object to convert into an IndexedNote.
   * @return An IndexedNote instance created from the given NoteDetailResponse.
   */
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
