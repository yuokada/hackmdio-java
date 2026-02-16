package io.github.yuokada.hackmd;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.service.HackMdService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpenCommandTest {

  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private ByteArrayOutputStream outputCapture;
  private ByteArrayOutputStream errorCapture;
  private OpenCommand command;

  @BeforeEach
  void setUp() {
    outputCapture = new ByteArrayOutputStream();
    errorCapture = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputCapture));
    System.setErr(new PrintStream(errorCapture));

    command = new OpenCommand();
    command.hackMdService = mock(HackMdService.class);
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  void runWithValidPermalink() {
    NoteDetailResponse note =
        new NoteDetailResponse(
            "test-id",
            "Test Note",
            Collections.emptyList(),
            Instant.now(),
            Instant.now(),
            Instant.now(),
            "view",
            null,
            null,
            "https://hackmd.io/@user/test-note",
            "abc123",
            "Test content",
            Instant.now(),
            null,
            null,
            null,
            "owner",
            "signed_in");

    command.noteId = "test-id";
    when(command.hackMdService.getNote("test-id")).thenReturn(note);

    command.run();

    String output = outputCapture.toString();
    // Either opened successfully or shows manual URL (depending on Desktop support)
    assertTrue(
        output.contains("https://hackmd.io/@user/test-note") || output.contains("Opened:"),
        "Output should contain the publish link URL");
  }

  @Test
  void runWithNullPermalink() {
    NoteDetailResponse note =
        new NoteDetailResponse(
            "test-id",
            "Test Note",
            Collections.emptyList(),
            Instant.now(),
            Instant.now(),
            Instant.now(),
            "view",
            null,
            null,
            null,
            "abc123",
            "Test content",
            Instant.now(),
            null,
            null,
            null,
            "owner",
            "signed_in");

    command.noteId = "test-id";
    when(command.hackMdService.getNote("test-id")).thenReturn(note);

    command.run();

    String error = errorCapture.toString();
    assertTrue(error.contains("Note does not have a publish link"));
  }

  @Test
  void runWithEmptyPermalink() {
    NoteDetailResponse note =
        new NoteDetailResponse(
            "test-id",
            "Test Note",
            Collections.emptyList(),
            Instant.now(),
            Instant.now(),
            Instant.now(),
            "view",
            null,
            null,
            "",
            "abc123",
            "Test content",
            Instant.now(),
            null,
            null,
            null,
            "owner",
            "signed_in");

    command.noteId = "test-id";
    when(command.hackMdService.getNote("test-id")).thenReturn(note);

    command.run();

    String error = errorCapture.toString();
    assertTrue(error.contains("Note does not have a publish link"));
  }

  @Test
  void runWithServiceException() {
    command.noteId = "invalid-id";
    when(command.hackMdService.getNote("invalid-id"))
        .thenThrow(new RuntimeException("Note not found"));

    command.run();

    String error = errorCapture.toString();
    assertTrue(error.contains("Error opening note"));
  }
}
