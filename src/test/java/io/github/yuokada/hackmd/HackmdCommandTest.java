package io.github.yuokada.hackmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

@QuarkusTest
class HackmdCommandTest {

  @Inject CommandLine commandLine;

  @Test
  void registersAllSubcommandsAndInjectsCommandMethods() {
    assertEquals(
        Set.of("list", "create", "get", "index", "search", "open"),
        commandLine.getSubcommands().keySet());

    assertCommandMethod("list");
    assertCommandMethod("create");
    assertCommandMethod("get");
    assertCommandMethod("open");

    HackmdCommand rootCommand = assertInstanceOf(HackmdCommand.class, commandLine.getCommand());
    assertNotNull(rootCommand.hackMdService);
    assertNotNull(rootCommand.objectMapper);
  }

  private void assertCommandMethod(String name) {
    Method method =
        assertInstanceOf(Method.class, commandLine.getSubcommands().get(name).getCommand());
    assertEquals(HackmdCommand.class, method.getDeclaringClass());
    assertEquals(name, method.getName());
  }
}
