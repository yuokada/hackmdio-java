package io.github.yuokada.hackmd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.transport.HackMdTransport;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HackMdServiceTest {

    private HackMdService service;
    private HackMdTransport transport;

    @BeforeEach
    void setUp() {
        transport = mock(HackMdTransport.class);
        service = new HackMdService(transport);
    }

    @Test
    void listsNotesThroughTransport() {
        when(transport.listNotes()).thenReturn(List.of(note("abc")));

        assertEquals("abc", service.listNotes().get(0).id());
    }

    @Test
    void createsNotesThroughTransport() {
        when(transport.createNote(new CreateNoteRequest("Title", "Body", "owner", "owner")))
                .thenReturn(note("abc"));

        assertEquals("abc", service.createNote("Title", "Body").id());
    }

    @Test
    void getsNotesThroughTransport() {
        when(transport.getNote("abc")).thenReturn(note("abc"));

        assertEquals("abc", service.getNote("abc").id());
        verify(transport).getNote("abc");
    }

    private static NoteDetailResponse note(String id) {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
        return new NoteDetailResponse(
                id, "Title", List.of(), timestamp, timestamp, timestamp, "view", timestamp, null, null, id, "Body",
                timestamp, null, null, null, "owner", "owner");
    }
}
