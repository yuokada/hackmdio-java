package io.github.yuokada.quarkus;

import io.github.yuokada.quarkus.model.Note;
import io.github.yuokada.quarkus.model.NoteDetailResponse;
import io.github.yuokada.quarkus.service.CouchbaseLiteService;
import io.github.yuokada.quarkus.service.HackMdService;
import jakarta.inject.Inject;
import java.util.Set;
import picocli.CommandLine.Command;

/**
 * A command to index notes from HackMD to local Couchbase Lite database.
 */
@Command(name = "index", description = "Index notes from HackMD to local database")
public class IndexCommand implements Runnable {

  @Inject
  HackMdService hackMdService;

  @Inject
  CouchbaseLiteService couchbaseLiteService;

  @Override
  public void run() {
    System.out.println("Starting indexing process...");

    // Create FTS index
    couchbaseLiteService.createFtsIndex();

    // Get all notes from API
    Set<Note> notes = hackMdService.listNotes();

    if (notes.isEmpty()) {
      System.out.println("No notes found.");
      return;
    }

    int totalNotes = notes.size();
    int newNotes = 0;
    int updatedNotes = 0;
    int skippedNotes = 0;
    int currentProgress = 0;

    System.out.printf("Found %d notes from HackMD API.%n", totalNotes);
    System.out.println("Processing notes...");

    for (Note note : notes) {
      currentProgress++;

      // Check if note needs update
      boolean needsUpdate = couchbaseLiteService.needsUpdate(note.id(), note.lastChangedAt());

      if (needsUpdate) {
        // Fetch full note content from API
        NoteDetailResponse fullNote = hackMdService.getNote(note.id());

        // Determine if it's new or updated
        if (couchbaseLiteService.getNote(note.id()) == null) {
          newNotes++;
        } else {
          updatedNotes++;
        }

        // Save to Couchbase Lite
        couchbaseLiteService.saveNote(fullNote);
      } else {
        skippedNotes++;
      }

      // Display progress
      if (currentProgress % 10 == 0 || currentProgress == totalNotes) {
        System.out.printf("Progress: %d/%d (%.1f%%)%n",
            currentProgress, totalNotes, (currentProgress * 100.0 / totalNotes));
      }
    }

    // Display summary
    System.out.println("\n=== Indexing Summary ===");
    System.out.printf("Total notes: %d%n", totalNotes);
    System.out.printf("New notes: %d%n", newNotes);
    System.out.printf("Updated notes: %d%n", updatedNotes);
    System.out.printf("Skipped notes: %d%n", skippedNotes);
    System.out.println("Indexing completed successfully.");
  }
}
