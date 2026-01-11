package io.github.yuokada.quarkus;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * A command to search notes in the local Couchbase Lite database.
 */
@Command(name = "search", description = "Search notes in local database")
public class SearchCommand implements Runnable {

  @Inject
  CouchbaseLiteService couchbaseLiteService;

  @Parameters(index = "0", description = "The search term.", paramLabel = "SEARCH_TERM")
  String searchTerm;

  @Override
  public void run() {
    System.out.printf("Searching for: \"%s\"%n%n", searchTerm);

    List<Map<String, Object>> results = couchbaseLiteService.searchNotes(searchTerm);

    if (results.isEmpty()) {
      System.out.println("No results found.");
      return;
    }

    System.out.printf("Found %d result(s):%n%n", results.size());

    // Calculate column widths
    int maxIdLength = "ID".length();
    int maxTitleLength = "Title".length();

    for (Map<String, Object> result : results) {
      String shortId = (String) result.get("shortId");
      String title = (String) result.get("title");

      if (shortId != null && shortId.length() > maxIdLength) {
        maxIdLength = shortId.length();
      }
      if (title != null && title.length() > maxTitleLength) {
        maxTitleLength = title.length();
      }
    }

    String format = "| %-" + maxIdLength + "s | %-" + maxTitleLength + "s |%n";

    // Header
    System.out.format(format, "ID", "Title");
    // Separator
    System.out.format(format, "-".repeat(maxIdLength), "-".repeat(maxTitleLength));

    // Rows
    for (Map<String, Object> result : results) {
      String shortId = (String) result.get("shortId");
      String title = (String) result.get("title");
      System.out.format(format, shortId != null ? shortId : "N/A", title != null ? title : "N/A");
    }

    System.out.println();
  }
}
