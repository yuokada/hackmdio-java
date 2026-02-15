package io.github.yuokada.quarkus.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class NoteTest {

  private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Test
  void deserializeFromJson() throws Exception {
    String json =
        """
        {
          "id": "note-id-1",
          "title": "My Note",
          "content": "Some content here",
          "tags": ["java", "quarkus"],
          "publishedAt": null,
          "shortId": "abc",
          "lastChangedAt": null
        }
        """;

    Note note = mapper.readValue(json, Note.class);

    assertEquals("note-id-1", note.id());
    assertEquals("My Note", note.title());
    assertEquals("Some content here", note.content());
    assertEquals(List.of("java", "quarkus"), note.tags());
    assertNull(note.publishedAt());
    assertEquals("abc", note.shortId());
    assertNull(note.lastChangedAt());
  }

  @Test
  void serializeToJson() throws Exception {
    Instant now = Instant.parse("2024-08-15T10:00:00Z");
    Note note = new Note("id1", "Title", "Content", List.of("tag1"), now, "short1", now);

    String json = mapper.writeValueAsString(note);

    assertTrue(json.contains("\"id\":\"id1\""));
    assertTrue(json.contains("\"title\":\"Title\""));
    assertTrue(json.contains("\"shortId\":\"short1\""));
  }

  @Test
  void noteDetailResponseToNoteConversion() {
    Instant created = Instant.parse("2024-01-01T00:00:00Z");
    Instant published = Instant.parse("2024-01-02T00:00:00Z");
    Instant changed = Instant.parse("2024-01-03T00:00:00Z");

    NoteDetailResponse detail = new NoteDetailResponse(
        "detail-id", "Detail Title", List.of("t1", "t2"),
        created, created, created, "view", published,
        null, null, "short-detail", "Detail content",
        changed, null, "upath", null, "owner", "owner");

    Note note = detail.toNote();

    assertEquals("detail-id", note.id());
    assertEquals("Detail Title", note.title());
    assertEquals("Detail content", note.content());
    assertEquals(List.of("t1", "t2"), note.tags());
    assertEquals(published, note.publishedAt());
    assertEquals("short-detail", note.shortId());
    assertEquals(changed, note.lastChangedAt());
  }

  @Test
  void emptyTagsList() {
    Note note = new Note("id", "title", null, List.of(), null, "s", null);
    assertTrue(note.tags().isEmpty());
  }

  @Test
  void nullFields() {
    Note note = new Note(null, null, null, null, null, null, null);
    assertNull(note.id());
    assertNull(note.title());
    assertNull(note.tags());
    assertNull(note.shortId());
  }
}
