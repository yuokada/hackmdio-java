package io.github.yuokada.hackmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yuokada.hackmd.service.HackMdService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;
    private HackmdCommand command;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        command = new HackmdCommand();
        command.hackMdService = mock(HackMdService.class);
        command.objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void jsonOutputIsValidForEmptyResults() throws Exception {
        when(command.hackMdService.listNotes()).thenReturn(List.of());

        assertEquals(0, command.list(true));

        assertEquals(List.of(), new ObjectMapper().readValue(output.toString(), List.class));
    }

    @Test
    void jsonOutputIsValidForNotes() throws Exception {
        when(command.hackMdService.listNotes())
                .thenReturn(List.of(TestFixtures.note("note-1", "First", "body", Instant.parse("2024-01-02T00:00:00Z"))
                        .toNote()));

        assertEquals(0, command.list(true));

        List<?> notes = new ObjectMapper().readValue(output.toString(), List.class);
        assertEquals(1, notes.size());
    }

    @Test
    void humanOutputExplainsEmptyResults() {
        when(command.hackMdService.listNotes()).thenReturn(List.of());

        assertEquals(0, command.list(false));

        assertTrue(output.toString().contains("No notes found."));
    }
}
