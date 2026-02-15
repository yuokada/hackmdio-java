package io.github.yuokada.hackmd;

import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.github.yuokada.hackmd.model.Note;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Set;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * The REST client interface for the HackMD API.
 */
@Path("/v1")
@RegisterRestClient(configKey = "hackmd-api")
@RegisterProvider(AuthorizationFilter.class)
public interface HackMdApi {

  @GET
  @Path("/notes")
  @Produces(MediaType.APPLICATION_JSON)
  Set<NoteDetailResponse> getNotes();

  @POST
  @Path("/notes")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Note createNote(CreateNoteRequest request);

  @GET
  @Path("/notes/{noteId}")
  @Produces(MediaType.APPLICATION_JSON)
  NoteDetailResponse getNote(@PathParam("noteId") String noteId);
}
