package io.github.yuokada.quarkus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yuokada.quarkus.model.Note;
import io.github.yuokada.quarkus.service.HackMdService;
import jakarta.inject.Inject;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * A command to list notes.
 */
@Command(name = "list", description = "List notes")
public class ListCommand implements Runnable {

  @Inject
  HackMdService hackMdService;

  @Inject
  ObjectMapper objectMapper; // Inject ObjectMapper for JSON serialization

  @Option(names = {"--json"}, description = "Output notes in JSON format.")
  boolean jsonOutput;

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

  @Override
  public void run() {
    Set<Note> notesSet = hackMdService.listNotes();
    if (notesSet.isEmpty()) {
      System.out.println("No notes found.");
      return;
    }

    List<Note> notes = new ArrayList<>(notesSet);
    // Sort by publishedAt descending, with nulls at the end.
    notes.sort(Comparator.comparing(Note::publishedAt,
        Comparator.nullsLast(Comparator.reverseOrder())));

    if (jsonOutput) {
      try {
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notes));
      } catch (Exception e) {
        System.err.println("Error serializing notes to JSON: " + e.getMessage());
      }
    } else {
      int maxIdLength = "ID".length();
      int maxTitleLength = "Title".length();

      for (Note note : notes) {
        if (note.shortId() != null && note.shortId().length() > maxIdLength) {
          maxIdLength = note.shortId().length();
        }
        if (note.title() != null && note.title().length() > maxTitleLength) {
          maxTitleLength = note.title().length();
        }
      }

      String format = "| %-" + maxIdLength + "s | %-" + maxTitleLength + "s | %-19s |%n";

      // Header
      System.out.format(format, "ID", "Title", "Published At");
      // Separator
      System.out.format(format, "-".repeat(maxIdLength), "-".repeat(maxTitleLength),
          "-".repeat(19));

      // Rows
      for (Note note : notes) {
        String publishedAtStr = (note.publishedAt() == null) ? "N/A"
            : formatter.format(note.publishedAt());
        System.out.format(format, note.shortId(), note.title(), publishedAtStr);
      }
    }
  }
}
