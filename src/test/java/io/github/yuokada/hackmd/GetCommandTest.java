package io.github.yuokada.hackmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.yuokada.hackmd.service.HackMdService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void printsNoteContent() {
        HackmdCommand command = new HackmdCommand();
        command.hackMdService = mock(HackMdService.class);
        when(command.hackMdService.getNote("abc")).thenReturn(TestFixtures.note("abc", "Title", "Body", Instant.now()));

        assertEquals(0, command.get("abc"));

        assertTrue(output.toString().contains("Title: Title"));
        assertTrue(output.toString().contains("Body"));
    }
}
