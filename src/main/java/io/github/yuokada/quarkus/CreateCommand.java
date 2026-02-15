package io.github.yuokada.quarkus;

import io.github.yuokada.quarkus.model.Note;
import io.github.yuokada.quarkus.service.HackMdService;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * A command to create a new note.
 */
@Command(name = "create", description = "Create a new note")
public class CreateCommand implements Runnable {

  @Inject
  HackMdService hackMdService;

  @Option(names = {"-t", "--title"}, description = "Title of the note", required = true)
  String title;

  @Option(names = {"-c", "--content"}, description = "Content of the note", defaultValue = "")
  String content;

  @Override
  public void run() {
    Note newNote = hackMdService.createNote(title, content);
    System.out.println("Successfully created note:");
    System.out.printf("Title: %s%n", newNote.title());
    System.out.printf("ID: %s%n", newNote.shortId());
  }
}
