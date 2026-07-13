package io.github.yuokada.hackmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yuokada.hackmd.model.Note;
import io.github.yuokada.hackmd.service.HackMdService;
import jakarta.inject.Inject;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.Comparator;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;

/**
 * A command to list notes.
 */
@Command(name = "list", description = "List notes")
public class ListCommand implements Callable<Integer> {

  @Inject HackMdService hackMdService;

  @Inject ObjectMapper objectMapper; // Inject ObjectMapper for JSON serialization

  @Option(
      names = {"--json"},
      description = "Output notes in JSON format.")
  boolean jsonOutput;

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

  @Override
  public Integer call() {
    List<Note> notes = new ArrayList<>(hackMdService.listNotes());
    if (notes.isEmpty() && !jsonOutput) {
      System.out.println("No notes found.");
      return ExitCode.OK;
    }

    // Sort by publishedAt descending, with nulls at the end.
    notes.sort(
        Comparator.comparing(Note::publishedAt, Comparator.nullsLast(Comparator.reverseOrder())));

    if (jsonOutput) {
      try {
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notes));
      } catch (Exception e) {
        System.err.println("Error serializing notes to JSON: " + e.getMessage());
        return ExitCode.SOFTWARE;
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
      System.out.format(
          format, "-".repeat(maxIdLength), "-".repeat(maxTitleLength), "-".repeat(19));

      // Rows
      for (Note note : notes) {
        String publishedAtStr =
            (note.publishedAt() == null) ? "N/A" : formatter.format(note.publishedAt());
        System.out.format(format, note.shortId(), note.title(), publishedAtStr);
      }
    }
    return ExitCode.OK;
  }
}
