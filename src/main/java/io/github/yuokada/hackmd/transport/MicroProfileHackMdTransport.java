package io.github.yuokada.hackmd.transport;

import io.github.yuokada.hackmd.client.HackmdRestClient;
import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/** MicroProfile REST Client based transport implementation. */
@DefaultBean
@ApplicationScoped
public class MicroProfileHackMdTransport implements HackMdTransport {

    private final HackmdRestClient hackmdRestClient;

    @Inject
    public MicroProfileHackMdTransport(@RestClient HackmdRestClient hackmdRestClient) {
        this.hackmdRestClient = hackmdRestClient;
    }

    @Override
    public List<NoteDetailResponse> listNotes() {
        return hackmdRestClient.listNotes();
    }

    @Override
    public NoteDetailResponse createNote(CreateNoteRequest request) {
        return hackmdRestClient.createNote(request);
    }

    @Override
    public NoteDetailResponse getNote(String noteId) {
        return hackmdRestClient.getNote(noteId);
    }
}
