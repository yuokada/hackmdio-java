package io.github.yuokada.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yuokada.quarkus.service.CouchbaseLiteService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchCommandTest {

  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream outputCapture;
  private SearchCommand command;

  @BeforeEach
  void setUp() {
    outputCapture = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputCapture));

    command = new SearchCommand();
    command.couchbaseLiteService = mock(CouchbaseLiteService.class);
    command.objectMapper = new ObjectMapper();
    command.searchTerm = "test";
    command.logger = Logger.getLogger(SearchCommandTest.class);
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void runWithEmptyResults() {
    when(command.couchbaseLiteService.searchNotes("test")).thenReturn(List.of());

    command.run();

    String output = outputCapture.toString();
    assertTrue(output.contains("No results found."));
  }

  @Test
  void runWithTableOutput() {
    Map<String, Object> result = new HashMap<>();
    result.put("shortId", "abc123");
    result.put("title", "Test Note");
    result.put("content", "This is a test content");
    result.put("tags", List.of("java", "quarkus"));
    result.put("updatedAt", "2024-08-15T10:00:00Z");

    when(command.couchbaseLiteService.searchNotes("test")).thenReturn(List.of(result));

    command.run();

    String output = outputCapture.toString();
    assertTrue(output.contains("Found 1 result(s):"));
    assertTrue(output.contains("abc123"));
    assertTrue(output.contains("Test Note"));
    assertTrue(output.contains("java, quarkus"));
    assertTrue(output.contains("[test]"));
    assertTrue(output.contains("ID"));
    assertTrue(output.contains("Title"));
    assertTrue(output.contains("Tags"));
    assertTrue(output.contains("Updated At"));
    assertTrue(output.contains("Snippet"));
  }

  @Test
  void runWithJsonOutput() {
    command.jsonOutput = true;

    Map<String, Object> result = new HashMap<>();
    result.put("shortId", "abc123");
    result.put("title", "Test Note");
    result.put("content", "This is a test content");

    when(command.couchbaseLiteService.searchNotes("test")).thenReturn(List.of(result));

    command.run();

    String output = outputCapture.toString();
    assertTrue(output.contains("\"shortId\" : \"abc123\""));
    assertTrue(output.contains("\"title\" : \"Test Note\""));
  }

  @Test
  void runWithNullFields() {
    Map<String, Object> result = new HashMap<>();
    result.put("shortId", null);
    result.put("title", null);
    result.put("content", null);
    result.put("updatedAt", null);

    when(command.couchbaseLiteService.searchNotes("test")).thenReturn(List.of(result));

    command.run();

    String output = outputCapture.toString();
    assertTrue(output.contains("N/A"));
  }

  @Test
  void formatTagsWithValidTags() throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("tags", List.of("a", "b", "c"));

    String tags = invokeFormatTags(result);
    assertEquals("a, b, c", tags);
  }

  @Test
  void formatTagsWithEmptyList() throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("tags", List.of());

    String tags = invokeFormatTags(result);
    assertEquals("N/A", tags);
  }

  @Test
  void formatTagsWithNull() throws Exception {
    Map<String, Object> result = new HashMap<>();

    String tags = invokeFormatTags(result);
    assertEquals("N/A", tags);
  }

  @Test
  void formatUpdatedAtWithValidInstant() throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("updatedAt", "2024-08-15T10:00:00Z");

    String formatted = invokeFormatUpdatedAt(result);
    assertTrue(formatted.contains("2024"));
    assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
  }

  @Test
  void formatUpdatedAtWithNull() throws Exception {
    Map<String, Object> result = new HashMap<>();

    String formatted = invokeFormatUpdatedAt(result);
    assertEquals("N/A", formatted);
  }

  @Test
  void formatUpdatedAtWithEmpty() throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("updatedAt", "");

    String formatted = invokeFormatUpdatedAt(result);
    assertEquals("N/A", formatted);
  }

  @Test
  void formatUpdatedAtWithInvalidString() throws Exception {
    Map<String, Object> result = new HashMap<>();
    result.put("updatedAt", "not-a-date");

    String formatted = invokeFormatUpdatedAt(result);
    assertEquals("not-a-date", formatted);
  }

  @Test
  void runWithMultipleResults() {
    Map<String, Object> result1 = new HashMap<>();
    result1.put("shortId", "id1");
    result1.put("title", "First");
    result1.put("content", "test in first");

    Map<String, Object> result2 = new HashMap<>();
    result2.put("shortId", "id2");
    result2.put("title", "Second");
    result2.put("content", "test in second");

    when(command.couchbaseLiteService.searchNotes("test"))
        .thenReturn(List.of(result1, result2));

    command.run();

    String output = outputCapture.toString();
    assertTrue(output.contains("Found 2 result(s):"));
    assertTrue(output.contains("id1"));
    assertTrue(output.contains("id2"));
  }

  @SuppressWarnings("unchecked")
  private static String invokeFormatTags(Map<String, Object> result) throws Exception {
    Method method = SearchCommand.class.getDeclaredMethod("formatTags", Map.class);
    method.setAccessible(true);
    return (String) method.invoke(null, result);
  }

  @SuppressWarnings("unchecked")
  private static String invokeFormatUpdatedAt(Map<String, Object> result) throws Exception {
    Method method = SearchCommand.class.getDeclaredMethod("formatUpdatedAt", Map.class);
    method.setAccessible(true);
    return (String) method.invoke(null, result);
  }
}
