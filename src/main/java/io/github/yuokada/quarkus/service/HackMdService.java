package io.github.yuokada.quarkus.service;

import io.github.yuokada.quarkus.HackMdApi;
import io.github.yuokada.quarkus.model.CreateNoteRequest;
import io.github.yuokada.quarkus.model.Note;
import io.github.yuokada.quarkus.model.NoteDetailResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * A service to interact with the HackMD API and persist note metadata.
 */
@ApplicationScoped
public class HackMdService {

  @Inject
  @RestClient
  HackMdApi hackMdApi;

  /**
   * Lists all notes from the API and synchronizes them with the local database.
   *
   * @return A set of notes.
   */
  @Transactional
  public Set<Note> listNotes() {
    Set<NoteDetailResponse> notes = hackMdApi.getNotes();
    return notes.stream().map(NoteDetailResponse::toNote).collect(Collectors.toSet());
  }

  public Set<NoteDetailResponse> listNoteDetails() {
      // TODO: Limit 5 notes for testing
      return hackMdApi.getNotes().stream().limit(5).collect(Collectors.toSet());
  }

  /**
   * Creates a new note via the API and saves it to the local database.
   *
   * @param title   The title of the note.
   * @param content The content of the note.
   * @return The created note.
   */
  @Transactional
  public Note createNote(String title, String content) {
    var request = new CreateNoteRequest(title, content, "owner", "owner");
    Note newNote = hackMdApi.createNote(request);
    return newNote;
  }

  /**
   * Gets a specific note by ID from the API and updates the local database.
   *
   * @param noteId The ID of the note to get.
   * @return The note.
   */
  @Transactional
  public NoteDetailResponse getNote(String noteId) {
    NoteDetailResponse note = hackMdApi.getNote(noteId);
    return note;
  }
}
