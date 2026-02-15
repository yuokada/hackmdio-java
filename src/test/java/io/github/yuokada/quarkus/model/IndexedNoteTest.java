package io.github.yuokada.quarkus.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class IndexedNoteTest {

  @Test
  void fromNoteAddsIdPrefix() {
    NoteDetailResponse note = createNote("abc123", "Test Title");
    IndexedNote indexed = IndexedNote.fromNote(note);

    assertEquals("note::abc123", indexed.id());
    assertEquals("abc123", indexed.originalId());
  }

  @Test
  void fromNoteCopiesAllFields() {
    Instant now = Instant.now();
    NoteDetailResponse.LastChangeUser user =
        new NoteDetailResponse.LastChangeUser("user1", "path1", "photo.jpg", "bio");
    NoteDetailResponse note = new NoteDetailResponse(
        "id1", "title1", List.of("tag1", "tag2"),
        now, now, now, "view", now,
        "permalink1", "publishLink1", "short1", "content1",
        now, user, "userPath1", "teamPath1", "owner", "owner");

    IndexedNote indexed = IndexedNote.fromNote(note);

    assertEquals("note::id1", indexed.id());
    assertEquals("id1", indexed.originalId());
    assertEquals("title1", indexed.title());
    assertEquals(List.of("tag1", "tag2"), indexed.tags());
    assertEquals(now, indexed.createdAt());
    assertEquals(now, indexed.titleUpdatedAt());
    assertEquals(now, indexed.tagsUpdatedAt());
    assertEquals("view", indexed.publishType());
    assertEquals(now, indexed.publishedAt());
    assertEquals("permalink1", indexed.permalink());
    assertEquals("publishLink1", indexed.publishLink());
    assertEquals("short1", indexed.shortId());
    assertEquals("content1", indexed.content());
    assertEquals(now, indexed.lastChangedAt());
    assertEquals(user, indexed.lastChangeUser());
    assertEquals("userPath1", indexed.userPath());
    assertEquals("teamPath1", indexed.teamPath());
    assertEquals("owner", indexed.readPermission());
    assertEquals("owner", indexed.writePermission());
  }

  @Test
  void fromNoteHandlesNullFields() {
    NoteDetailResponse note = new NoteDetailResponse(
        "id2", null, null,
        null, null, null, null, null,
        null, null, null, null,
        null, null, null, null, null, null);

    IndexedNote indexed = IndexedNote.fromNote(note);

    assertEquals("note::id2", indexed.id());
    assertEquals("id2", indexed.originalId());
    assertNull(indexed.title());
    assertNull(indexed.tags());
    assertNull(indexed.content());
  }

  private static NoteDetailResponse createNote(String id, String title) {
    return new NoteDetailResponse(
        id, title, List.of(),
        null, null, null, null, null,
        null, null, "short-" + id, null,
        null, null, null, null, null, null);
  }
}
