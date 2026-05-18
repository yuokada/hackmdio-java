package io.github.yuokada.hackmd.transport;

import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.github.yuokada.hackmd.model.Note;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import java.util.Set;

/** Transport abstraction for HackMD API operations used by the service layer. */
public interface HackmdTransport {

  Set<NoteDetailResponse> getNotes();

  Note createNote(CreateNoteRequest request);

  NoteDetailResponse getNote(String noteId);
}
