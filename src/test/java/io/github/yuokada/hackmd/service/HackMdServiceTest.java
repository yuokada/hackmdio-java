package io.github.yuokada.hackmd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.yuokada.hackmd.client.HackmdRestClient;
import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HackMdServiceTest {

  private HackMdService service;
  private HackmdRestClient restClient;

  @BeforeEach
  void setUp() {
    service = new HackMdService();
    restClient = mock(HackmdRestClient.class);
    service.hackmdRestClient = restClient;
  }

  @Test
  void listsNotesThroughCanonicalClient() {
    when(restClient.listNotes()).thenReturn(List.of(note("abc")));

    assertEquals("abc", service.listNotes().get(0).id());
  }

  @Test
  void createsNotesThroughCanonicalClient() {
    when(restClient.createNote(new CreateNoteRequest("Title", "Body", "owner", "owner")))
        .thenReturn(note("abc"));

    assertEquals("abc", service.createNote("Title", "Body").id());
  }

  @Test
  void getsNotesThroughCanonicalClient() {
    when(restClient.getNote("abc")).thenReturn(note("abc"));

    assertEquals("abc", service.getNote("abc").id());
    verify(restClient).getNote("abc");
  }

  private static NoteDetailResponse note(String id) {
    Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
    return new NoteDetailResponse(
        id,
        "Title",
        List.of(),
        timestamp,
        timestamp,
        timestamp,
        "view",
        timestamp,
        null,
        null,
        id,
        "Body",
        timestamp,
        null,
        null,
        null,
        "owner",
        "owner");
  }
}
