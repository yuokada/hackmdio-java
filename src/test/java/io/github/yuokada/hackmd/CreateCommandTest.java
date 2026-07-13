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

class CreateCommandTest {

  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream output;
  private HackmdCommand.CreateCommand command;

  @BeforeEach
  void setUp() {
    output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));
    command = new HackmdCommand.CreateCommand();
    command.hackMdService = mock(HackMdService.class);
    command.title = "Title";
    command.content = "Body";
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void createsAndPrintsNote() {
    when(command.hackMdService.createNote("Title", "Body"))
        .thenReturn(TestFixtures.note("abc", "Title", "Body", Instant.now()).toNote());

    assertEquals(0, command.call());

    assertTrue(output.toString().contains("ID: abc"));
  }
}
