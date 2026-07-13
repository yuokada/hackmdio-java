package io.github.yuokada.hackmd.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.yuokada.hackmd.model.CreateNoteRequest;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import java.util.Map;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@ConnectWireMock
@TestProfile(HackmdRestClientTest.Profile.class)
class HackmdRestClientTest {

  private static final String NOTE_JSON =
      """
      {
        "id": "note-1",
        "title": "Test note",
        "tags": ["java"],
        "createdAt": 1704067200000,
        "titleUpdatedAt": 1704067200000,
        "tagsUpdatedAt": 1704067200000,
        "publishType": "view",
        "publishedAt": 1704067200000,
        "shortId": "short-1",
        "content": "Body",
        "lastChangedAt": 1704067200000,
        "readPermission": "owner",
        "writePermission": "owner"
      }
      """;

  WireMock wireMock;

  @Inject @RestClient HackmdRestClient restClient;

  public static class Profile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
      return Map.of("hackmd.api.token", "test-token");
    }
  }

  @BeforeEach
  void resetWireMock() {
    wireMock.resetMappings();
    wireMock.resetRequests();
  }

  @Test
  void listsNotesWithAuthorization() {
    wireMock.register(
        get(urlEqualTo("/v1/notes"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[" + NOTE_JSON + "]")));

    assertEquals("note-1", restClient.listNotes().get(0).id());
    wireMock.verifyThat(
        getRequestedFor(urlEqualTo("/v1/notes"))
            .withHeader("Authorization", equalTo("Bearer test-token")));
  }

  @Test
  void createsNotesUsingTheCanonicalResponseType() {
    wireMock.register(
        post(urlEqualTo("/v1/notes"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(NOTE_JSON)));

    assertEquals(
        "note-1",
        restClient.createNote(new CreateNoteRequest("Test note", "Body", "owner", "owner")).id());
    wireMock.verifyThat(postRequestedFor(urlEqualTo("/v1/notes")));
  }
}
