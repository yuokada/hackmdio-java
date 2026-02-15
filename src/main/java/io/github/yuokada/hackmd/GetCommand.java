package io.github.yuokada.hackmd;

import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.service.HackMdService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * A command to get a specific note by ID.
 */
@Command(name = "get", description = "Get a specific note by ID")
public class GetCommand implements Runnable {

  @Inject HackMdService hackMdService;

  @Parameters(index = "0", description = "The ID of the note to get.", paramLabel = "NOTE_ID")
  String noteId;

  @Override
  public void run() {
    NoteDetailResponse note = hackMdService.getNote(noteId);
    System.out.printf("Title: %s%n", note.title());
    System.out.println("---");
    System.out.println(note.content());
  }
}
