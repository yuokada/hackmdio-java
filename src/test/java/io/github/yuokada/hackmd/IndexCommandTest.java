package io.github.yuokada.hackmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.service.CouchbaseLiteService;
import io.github.yuokada.hackmd.service.HackMdService;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream output;
    private IndexCommand command;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        command = new IndexCommand();
        command.hackMdService = mock(HackMdService.class);
        command.couchbaseLiteService = mock(CouchbaseLiteService.class);
        command.logger = Logger.getLogger(IndexCommandTest.class);
        command.sleeper = milliseconds -> {};
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void removesAllLocalNotesWhenRemoteListIsEmpty() {
        when(command.hackMdService.listNoteDetails()).thenReturn(List.of());
        when(command.couchbaseLiteService.removeMissingNotes(java.util.Set.of()))
                .thenReturn(2);

        assertEquals(0, command.call());

        assertTrue(output.toString().contains("Deleted notes: 2"));
    }

    @Test
    void returnsFailureWhenANoteCannotBeIndexed() {
        NoteDetailResponse note = TestFixtures.note("abc", "Title", "Body", Instant.now());
        when(command.hackMdService.listNoteDetails()).thenReturn(List.of(note));
        when(command.couchbaseLiteService.needsUpdate("note::abc", note.lastChangedAt()))
                .thenThrow(new RuntimeException("database failure"));
        when(command.couchbaseLiteService.removeMissingNotes(anySet())).thenReturn(0);

        assertEquals(1, command.call());

        assertTrue(output.toString().contains("Error notes: 1"));
    }

    @Test
    void retriesRateLimitedReadsWithoutRealSleeping() {
        NoteDetailResponse note = TestFixtures.note("abc", "Title", "Body", Instant.now());
        ClientWebApplicationException rateLimited = mock(ClientWebApplicationException.class);
        when(rateLimited.getResponse()).thenReturn(Response.status(429).build());
        when(command.hackMdService.listNoteDetails()).thenReturn(List.of(note));
        when(command.couchbaseLiteService.needsUpdate("note::abc", note.lastChangedAt()))
                .thenReturn(true);
        when(command.hackMdService.getNote("abc")).thenThrow(rateLimited).thenReturn(note);
        when(command.couchbaseLiteService.removeMissingNotes(anySet())).thenReturn(0);
        List<Long> sleeps = new ArrayList<>();
        command.sleeper = sleeps::add;

        assertEquals(0, command.call());

        verify(command.hackMdService, times(2)).getNote("abc");
        verify(command.couchbaseLiteService).saveNote(note);
        assertEquals(List.of(6000L, 500L), sleeps);
    }
}
