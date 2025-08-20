package io.github.yuokada.quarkus;

import io.github.yuokada.quarkus.model.Note;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * A command to get a specific note by ID.
 */
@Command(name = "get", description = "Get a specific note by ID")
public class GetCommand implements Runnable {

  @Inject
  HackMdService hackMdService;

  @Parameters(index = "0", description = "The ID of the note to get.", paramLabel = "NOTE_ID")
  String noteId;

  @Override
  public void run() {
    Note note = hackMdService.getNote(noteId);
    System.out.printf("Title: %s%n", note.title());
    System.out.println("---");
    System.out.println(note.content());
  }
}