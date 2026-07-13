package io.github.yuokada.hackmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Set;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

@QuarkusTest
class HackmdCommandTest {

  @Inject CommandLine commandLine;

  @Test
  void registersAllSubcommandsAndInjectsNestedCommands() {
    assertEquals(
        Set.of("list", "create", "get", "index", "search", "open"),
        commandLine.getSubcommands().keySet());

    HackmdCommand.ListCommand listCommand =
        assertInstanceOf(
            HackmdCommand.ListCommand.class,
            commandLine.getSubcommands().get("list").getCommand());
    HackmdCommand.CreateCommand createCommand =
        assertInstanceOf(
            HackmdCommand.CreateCommand.class,
            commandLine.getSubcommands().get("create").getCommand());
    HackmdCommand.GetCommand getCommand =
        assertInstanceOf(
            HackmdCommand.GetCommand.class,
            commandLine.getSubcommands().get("get").getCommand());
    HackmdCommand.OpenCommand openCommand =
        assertInstanceOf(
            HackmdCommand.OpenCommand.class,
            commandLine.getSubcommands().get("open").getCommand());

    assertNotNull(listCommand.hackMdService);
    assertNotNull(listCommand.objectMapper);
    assertNotNull(createCommand.hackMdService);
    assertNotNull(getCommand.hackMdService);
    assertNotNull(openCommand.hackMdService);
  }
}
