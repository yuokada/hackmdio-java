package io.github.yuokada.hackmd.service;

import io.github.yuokada.hackmd.client.HackmdRestClient;
import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.github.yuokada.hackmd.model.Note;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * A service to interact with the HackMD API and persist note metadata.
 */
@ApplicationScoped
public class HackMdService {

  @Inject @RestClient HackmdRestClient hackmdRestClient;

  /**
   * Lists all notes from the API and synchronizes them with the local database.
   *
   * @return A set of notes.
   */
  public List<Note> listNotes() {
    return hackmdRestClient.listNotes().stream().map(NoteDetailResponse::toNote).toList();
  }

  public List<NoteDetailResponse> listNoteDetails() {
    return hackmdRestClient.listNotes();
  }

  /**
   * Creates a new note via the API and saves it to the local database.
   *
   * @param title   The title of the note.
   * @param content The content of the note.
   * @return The created note.
   */
  public Note createNote(String title, String content) {
    var request = new CreateNoteRequest(title, content, "owner", "owner");
    return hackmdRestClient.createNote(request).toNote();
  }

  /**
   * Gets a specific note by ID from the API and updates the local database.
   *
   * @param noteId The ID of the note to get.
   * @return The note.
   */
  public NoteDetailResponse getNote(String noteId) {
    return hackmdRestClient.getNote(noteId);
  }
}
