package io.github.yuokada.quarkus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yuokada.quarkus.service.CouchbaseLiteService;
import io.github.yuokada.quarkus.util.SnippetUtil;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * A command to search notes in the local Couchbase Lite database.
 */
@Command(name = "search", description = "Search notes in local database")
public class SearchCommand implements Runnable {

  @Inject
  CouchbaseLiteService couchbaseLiteService;

  @Inject
  ObjectMapper objectMapper;

  @Parameters(index = "0", description = "The search term.", paramLabel = "SEARCH_TERM")
  String searchTerm;

  @Option(names = {"--json"}, description = "Output results in JSON format.")
  boolean jsonOutput;

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Inject
    Logger logger;

    @Override
  public void run() {
    System.out.printf("Searching for: \"%s\"%n%n", searchTerm);

    List<Map<String, Object>> results = couchbaseLiteService.searchNotes(searchTerm);

    if (results.isEmpty()) {
      System.out.println("No results found.");
      return;
    }

    System.out.printf("Found %d result(s):%n%n", results.size());

    if (jsonOutput) {
      try {
        System.out.println(
            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results));
      } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
          logger.error("Error serializing results to JSON", e);
      }
      return;
    }

    // Calculate column widths
    int maxIdLength = "ID".length();
    int maxTitleLength = "Title".length();
    int maxTagsLength = "Tags".length();
    int dateLength = 19; // "yyyy-MM-dd HH:mm:ss"
    int maxSnippetLength = "Snippet".length();

    for (Map<String, Object> result : results) {
      String shortId = (String) result.get("shortId");
      String title = (String) result.get("title");
      String tagsStr = formatTags(result);
      String snippet = SnippetUtil.generateSnippet(
          (String) result.get("content"), searchTerm);

      if (shortId != null && shortId.length() > maxIdLength) {
        maxIdLength = shortId.length();
      }
      if (title != null && title.length() > maxTitleLength) {
        maxTitleLength = title.length();
      }
      if (tagsStr.length() > maxTagsLength) {
        maxTagsLength = tagsStr.length();
      }
      if (snippet.length() > maxSnippetLength) {
        maxSnippetLength = snippet.length();
      }
    }

    String format = "| %-" + maxIdLength + "s | %-" + maxTitleLength + "s | %-"
        + maxTagsLength + "s | %-" + dateLength + "s | %-" + maxSnippetLength + "s |%n";

    // Header
    System.out.format(format, "ID", "Title", "Tags", "Updated At", "Snippet");
    // Separator
    System.out.format(format, "-".repeat(maxIdLength), "-".repeat(maxTitleLength),
        "-".repeat(maxTagsLength), "-".repeat(dateLength), "-".repeat(maxSnippetLength));

    // Rows
    for (Map<String, Object> result : results) {
      String shortId = (String) result.get("shortId");
      String title = (String) result.get("title");
      String tagsStr = formatTags(result);
      String updatedAtStr = formatUpdatedAt(result);
      String snippet = SnippetUtil.generateSnippet(
          (String) result.get("content"), searchTerm);

      System.out.format(format,
          shortId != null ? shortId : "N/A",
          title != null ? title : "N/A",
          tagsStr,
          updatedAtStr,
          snippet);
    }

    System.out.println();
  }

  @SuppressWarnings("unchecked")
  private static String formatTags(Map<String, Object> result) {
    Object tags = result.get("tags");
    if (tags instanceof List) {
      List<String> tagList = (List<String>) tags;
      if (!tagList.isEmpty()) {
        return String.join(", ", tagList);
      }
    }
    return "N/A";
  }

  private static String formatUpdatedAt(Map<String, Object> result) {
    String updatedAt = (String) result.get("updatedAt");
    if (updatedAt == null || updatedAt.isEmpty()) {
      return "N/A";
    }
    try {
      return formatter.format(Instant.parse(updatedAt));
    } catch (java.time.format.DateTimeParseException e) {
      return updatedAt;
    }
  }
}
