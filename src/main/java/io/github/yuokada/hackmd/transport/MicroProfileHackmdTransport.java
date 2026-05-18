package io.github.yuokada.hackmd.transport;

import io.github.yuokada.hackmd.HackMdApi;
import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.github.yuokada.hackmd.model.Note;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Set;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/** MicroProfile REST Client based transport implementation. */
@ApplicationScoped
public class MicroProfileHackmdTransport implements HackmdTransport {

  @Inject @RestClient HackMdApi hackMdApi;

  @Override
  public Set<NoteDetailResponse> getNotes() {
    return hackMdApi.getNotes();
  }

  @Override
  public Note createNote(CreateNoteRequest request) {
    return hackMdApi.createNote(request);
  }

  @Override
  public NoteDetailResponse getNote(String noteId) {
    return hackMdApi.getNote(noteId);
  }
}
