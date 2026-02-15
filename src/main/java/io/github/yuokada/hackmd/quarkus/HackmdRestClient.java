package io.github.yuokada.hackmd.quarkus;

import io.github.yuokada.quarkus.AuthorizationFilter;
import io.github.yuokada.quarkus.model.CreateNoteRequest;
import io.github.yuokada.quarkus.model.NoteDetailResponse;
import io.github.yuokada.quarkus.model.Team;
import io.github.yuokada.quarkus.model.UpdateNoteRequest;
import io.github.yuokada.quarkus.model.UserProfile;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * A MicroProfile REST client derived from docs/swagger.json.
 */
@Path("/v1")
@RegisterRestClient(configKey = "hackmd-api")
@RegisterProvider(AuthorizationFilter.class)
public interface HackmdRestClient {

  @GET
  @Path("/notes")
  @Produces(MediaType.APPLICATION_JSON)
  List<NoteDetailResponse> listNotes();

  @POST
  @Path("/notes")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  NoteDetailResponse createNote(CreateNoteRequest request);

  @GET
  @Path("/notes/{noteId}")
  @Produces(MediaType.APPLICATION_JSON)
  NoteDetailResponse getNote(@PathParam("noteId") String noteId);

  @PATCH
  @Path("/notes/{noteId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  NoteDetailResponse updateNote(
      @PathParam("noteId") String noteId,
      UpdateNoteRequest request);

  @GET
  @Path("/teams")
  @Produces(MediaType.APPLICATION_JSON)
  List<Team> listTeams();

  @GET
  @Path("/teams/{teamPath}/notes")
  @Produces(MediaType.APPLICATION_JSON)
  List<NoteDetailResponse> listTeamNotes(@PathParam("teamPath") String teamPath);

  @POST
  @Path("/teams/{teamPath}/notes")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  NoteDetailResponse createTeamNote(
      @PathParam("teamPath") String teamPath,
      CreateNoteRequest request);

  @GET
  @Path("/teams/{teamPath}/notes/{noteId}")
  @Produces(MediaType.APPLICATION_JSON)
  NoteDetailResponse getTeamNote(
      @PathParam("teamPath") String teamPath,
      @PathParam("noteId") String noteId);

  @PATCH
  @Path("/teams/{teamPath}/notes/{noteId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  NoteDetailResponse updateTeamNote(
      @PathParam("teamPath") String teamPath,
      @PathParam("noteId") String noteId,
      UpdateNoteRequest request);

  @DELETE
  @Path("/teams/{teamPath}/notes/{noteId}")
  void deleteTeamNote(
      @PathParam("teamPath") String teamPath,
      @PathParam("noteId") String noteId);

  @GET
  @Path("/me")
  @Produces(MediaType.APPLICATION_JSON)
  UserProfile getCurrentUser();

  @GET
  @Path("/history")
  @Produces(MediaType.APPLICATION_JSON)
  List<Map<String, Object>> getHistory(@QueryParam("limit") Integer limit);
}
