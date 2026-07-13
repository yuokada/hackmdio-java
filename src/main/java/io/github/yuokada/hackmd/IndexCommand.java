package io.github.yuokada.hackmd;

import io.github.yuokada.hackmd.model.IndexedNote;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.service.CouchbaseLiteService;
import io.github.yuokada.hackmd.service.HackMdService;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import picocli.CommandLine.Command;

/**
 * A command to index notes from HackMD to local Couchbase Lite database.
 */
@Command(name = "index", description = "Index notes from HackMD to local database")
public class IndexCommand implements Callable<Integer> {

  private static final int API_CALL_DELAY_MS = 500;
  private static final int MAX_RETRIES = 3;
  private static final int INITIAL_BACKOFF_MS = 6000;

  @Inject HackMdService hackMdService;

  @Inject CouchbaseLiteService couchbaseLiteService;
  @Inject Logger logger;

  Sleeper sleeper = Thread::sleep;

  @Override
  public Integer call() {
    System.out.println("Starting indexing process...");

    // Create FTS index
    couchbaseLiteService.createFtsIndex();

    // Get all notes from API
    var notes = hackMdService.listNoteDetails();

    if (notes.isEmpty()) {
      System.out.println("No notes found.");
      int deletedNotes = couchbaseLiteService.removeMissingNotes(Set.of());
      System.out.printf("Deleted notes: %d%n", deletedNotes);
      return 0;
    }

    int totalNotes = notes.size();
    int newNotes = 0;
    int updatedNotes = 0;
    int skippedNotes = 0;
    int errorNotes = 0;
    int currentProgress = 0;

    System.out.printf("Found %d notes from HackMD API.%n", totalNotes);
    System.out.println("Processing notes...");

    for (IndexedNote note : notes.stream().map(IndexedNote::fromNote).toList()) {
      currentProgress++;

      try {
        // Check if note needs update
        boolean needsUpdate = couchbaseLiteService.needsUpdate(note.id(), note.lastChangedAt());

        if (needsUpdate) {
          // Fetch full note content from API with retry on 429
          NoteDetailResponse fullNote = fetchNoteWithRetry(note.originalId());
          sleeper.sleep(API_CALL_DELAY_MS);

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
      } catch (Exception e) {
        errorNotes++;
        logger.error("Failed to index note %s: %s".formatted(note.originalId(), e.getMessage()));
      }

      // Display progress
      if (currentProgress % 10 == 0 || currentProgress == totalNotes) {
        System.out.printf(
            "Progress: %d/%d (%.1f%%)%n",
            currentProgress, totalNotes, (currentProgress * 100.0 / totalNotes));
      }
    }

    Set<String> remoteNoteIds =
        notes.stream().map(NoteDetailResponse::id).collect(Collectors.toSet());
    int deletedNotes = couchbaseLiteService.removeMissingNotes(remoteNoteIds);

    // Display summary
    System.out.println("\n=== Indexing Summary ===");
    System.out.printf("Total notes: %d%n", totalNotes);
    System.out.printf("New notes: %d%n", newNotes);
    System.out.printf("Updated notes: %d%n", updatedNotes);
    System.out.printf("Skipped notes: %d%n", skippedNotes);
    System.out.printf("Deleted notes: %d%n", deletedNotes);
    System.out.printf("Error notes: %d%n", errorNotes);
    if (errorNotes > 0) {
      System.err.println("Indexing completed with errors.");
      return 1;
    }
    System.out.println("Indexing completed successfully.");
    return 0;
  }

  private NoteDetailResponse fetchNoteWithRetry(String noteId) {
    int retries = 0;
    while (true) {
      try {
        return hackMdService.getNote(noteId);
      } catch (ClientWebApplicationException e) {
        if (e.getResponse().getStatus() == 429 && retries < MAX_RETRIES) {
          retries++;
          long backoff = INITIAL_BACKOFF_MS * (1L << (retries - 1));
          logger.warn(
              "Rate limited when fetching note %s. Retry %d/%d after %dms."
                  .formatted(noteId, retries, MAX_RETRIES, backoff));
          try {
            sleeper.sleep(backoff);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw e;
          }
        } else {
          throw e;
        }
      }
    }
  }

  @FunctionalInterface
  interface Sleeper {
    void sleep(long milliseconds) throws InterruptedException;
  }
}
