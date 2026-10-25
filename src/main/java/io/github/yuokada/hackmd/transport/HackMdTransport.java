package io.github.yuokada.hackmd.transport;

import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import java.util.List;

/** Transport abstraction for HackMD API operations used by the service layer. */
public interface HackMdTransport {

    List<NoteDetailResponse> listNotes();

    NoteDetailResponse createNote(CreateNoteRequest request);

    NoteDetailResponse getNote(String noteId);
}
