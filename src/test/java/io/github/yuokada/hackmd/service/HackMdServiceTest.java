package io.github.yuokada.hackmd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.yuokada.hackmd.model.Note;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.transport.HackmdTransport;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class HackMdServiceTest {

  private HackMdService service;
  private HackmdTransport transport;

  @BeforeEach
  void setUp() {
    service = new HackMdService();
    transport = Mockito.mock(HackmdTransport.class);
    service.hackmdTransport = transport;
  }

  @Test
  void listNotesDelegatesToTransportAndConvertsToNote() {
    NoteDetailResponse detail = createNote("note-id", "title");
    when(transport.getNotes()).thenReturn(Set.of(detail));

    Set<Note> notes = service.listNotes();

    verify(transport).getNotes();
    assertEquals(1, notes.size());
    assertEquals("note-id", notes.iterator().next().id());
  }

  @Test
  void getNoteDelegatesToTransport() {
    NoteDetailResponse detail = createNote("note-id", "title");
    when(transport.getNote("note-id")).thenReturn(detail);

    NoteDetailResponse actual = service.getNote("note-id");

    verify(transport).getNote("note-id");
    assertSame(detail, actual);
  }

  private static NoteDetailResponse createNote(String id, String title) {
    return new NoteDetailResponse(
        id, title, List.of(),
        null, null, null, null, null,
        null, null, "short-" + id, null,
        null, null, null, null, null, null);
  }
}
