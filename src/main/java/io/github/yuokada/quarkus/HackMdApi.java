package io.github.yuokada.quarkus;

import io.github.yuokada.quarkus.model.CreateNoteRequest;
import io.github.yuokada.quarkus.model.Note;
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
  Set<Note> getNotes();

  @POST
  @Path("/notes")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Note createNote(CreateNoteRequest request);

  @GET
  @Path("/notes/{noteId}")
  @Produces(MediaType.APPLICATION_JSON)
  Note getNote(@PathParam("noteId") String noteId);
}