package io.github.yuokada.quarkus;

import io.github.yuokada.quarkus.model.CreateNoteRequest;
import io.github.yuokada.quarkus.model.Note;
import io.github.yuokada.quarkus.model.NoteEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Set;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * A service to interact with the HackMD API and persist note metadata.
 */
@ApplicationScoped
public class HackMdService {

  @Inject
  @RestClient
  HackMdApi hackMdApi;

  @Inject
  NoteRepository noteRepository;

  /**
   * Lists all notes from the API and synchronizes them with the local database.
   *
   * @return A set of notes.
   */
  @Transactional
  public Set<Note> listNotes() {
    Set<Note> notes = hackMdApi.getNotes();

    for (Note note : notes) {
      NoteEntity entity = noteRepository.findById(note.id());
      if (entity == null) {
        entity = new NoteEntity();
        entity.id = note.id();
      }
      entity.shortId = note.shortId();
      entity.title = note.title();
      entity.publishedAt = note.publishedAt();
      noteRepository.persist(entity);
    }
    return notes;
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

    NoteEntity entity = NoteEntity.from(newNote);
    noteRepository.persist(entity);

    return newNote;
  }

  /**
   * Gets a specific note by ID from the API and updates the local database.
   *
   * @param noteId The ID of the note to get.
   * @return The note.
   */
  @Transactional
  public Note getNote(String noteId) {
    Note note = hackMdApi.getNote(noteId);

    NoteEntity entity = noteRepository.findById(note.id());
    if (entity == null) {
      entity = new NoteEntity();
      entity.id = note.id();
    }
    entity.shortId = note.shortId();
    entity.title = note.title();
    entity.publishedAt = note.publishedAt();
    noteRepository.persist(entity);

    return note;
  }
}