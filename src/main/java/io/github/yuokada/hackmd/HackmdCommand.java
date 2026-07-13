package io.github.yuokada.hackmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yuokada.hackmd.model.Note;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.service.HackMdService;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import jakarta.inject.Inject;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * The main command for the HackMD CLI application.
 * It serves as the entry point and aggregates all subcommands.
 */
@TopCommand
@Command(
    mixinStandardHelpOptions = true,
    subcommands = {
      HackmdCommand.ListCommand.class,
      HackmdCommand.CreateCommand.class,
      HackmdCommand.GetCommand.class,
      IndexCommand.class,
      SearchCommand.class,
      HackmdCommand.OpenCommand.class
    })
public class HackmdCommand {

  /** A command to list notes. */
  @Command(name = "list", description = "List notes")
  public static class ListCommand implements Callable<Integer> {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Inject HackMdService hackMdService;

    @Inject ObjectMapper objectMapper;

    @Option(
        names = {"--json"},
        description = "Output notes in JSON format.")
    boolean jsonOutput;

    @Override
    public Integer call() {
      List<Note> notes = new ArrayList<>(hackMdService.listNotes());
      if (notes.isEmpty() && !jsonOutput) {
        System.out.println("No notes found.");
        return ExitCode.OK;
      }

      notes.sort(
          Comparator.comparing(Note::publishedAt, Comparator.nullsLast(Comparator.reverseOrder())));

      if (jsonOutput) {
        try {
          System.out.println(
              objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notes));
        } catch (Exception e) {
          System.err.println("Error serializing notes to JSON: " + e.getMessage());
          return ExitCode.SOFTWARE;
        }
      } else {
        printNotes(notes);
      }
      return ExitCode.OK;
    }

    private static void printNotes(List<Note> notes) {
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
      System.out.format(format, "ID", "Title", "Published At");
      System.out.format(
          format, "-".repeat(maxIdLength), "-".repeat(maxTitleLength), "-".repeat(19));

      for (Note note : notes) {
        String publishedAt =
            (note.publishedAt() == null) ? "N/A" : FORMATTER.format(note.publishedAt());
        System.out.format(format, note.shortId(), note.title(), publishedAt);
      }
    }
  }

  /** A command to create a new note. */
  @Command(name = "create", description = "Create a new note")
  public static class CreateCommand implements Callable<Integer> {

    @Inject HackMdService hackMdService;

    @Option(
        names = {"-t", "--title"},
        description = "Title of the note",
        required = true)
    String title;

    @Option(
        names = {"-c", "--content"},
        description = "Content of the note",
        defaultValue = "")
    String content;

    @Override
    public Integer call() {
      Note newNote = hackMdService.createNote(title, content);
      System.out.println("Successfully created note:");
      System.out.printf("Title: %s%n", newNote.title());
      System.out.printf("ID: %s%n", newNote.shortId());
      return ExitCode.OK;
    }
  }

  /** A command to get a specific note by ID. */
  @Command(name = "get", description = "Get a specific note by ID")
  public static class GetCommand implements Callable<Integer> {

    @Inject HackMdService hackMdService;

    @Parameters(index = "0", description = "The ID of the note to get.", paramLabel = "NOTE_ID")
    String noteId;

    @Override
    public Integer call() {
      NoteDetailResponse note = hackMdService.getNote(noteId);
      System.out.printf("Title: %s%n", note.title());
      System.out.println("---");
      System.out.println(note.content());
      return ExitCode.OK;
    }
  }

  /** A command to open a note's publish link in the default browser. */
  @Command(name = "open", description = "Open a note's publish link in the browser")
  public static class OpenCommand implements Callable<Integer> {

    @Inject HackMdService hackMdService;

    @Parameters(index = "0", description = "The ID of the note to open.", paramLabel = "NOTE_ID")
    String noteId;

    @Override
    public Integer call() {
      try {
        NoteDetailResponse note = hackMdService.getNote(noteId);
        String publishLink = note.publishLink();

        if (publishLink == null || publishLink.isEmpty()) {
          System.err.println("Error: Note does not have a publish link.");
          return ExitCode.SOFTWARE;
        }

        return openInBrowser(publishLink) ? ExitCode.OK : ExitCode.SOFTWARE;
      } catch (Exception e) {
        System.err.println("Error opening note: " + e.getMessage());
        return ExitCode.SOFTWARE;
      }
    }

    boolean openInBrowser(String url) throws IOException, URISyntaxException {
      if (!Desktop.isDesktopSupported()
          || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        System.out.println("Desktop browsing is not available on this system.");
        System.out.println("Please open the following URL manually: " + url);
        return false;
      }

      Desktop.getDesktop().browse(new URI(url));
      System.out.println("Opened: " + url);
      return true;
    }
  }
}
