package io.github.yuokada.hackmd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class NoteDetailResponseTest {

  @Test
  void deserializeEpochMillisJson() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    String json =
        """
        {
          "id": "demo-long-id",
          "title": "demo contents title",
          "tags": [
            "gatling",
            "td"
          ],
          "createdAt": 1723685238684,
          "titleUpdatedAt": 1723692520478,
          "tagsUpdatedAt": 1723692520477,
          "publishType": "view",
          "publishedAt": null,
          "permalink": null,
          "publishLink": "https://hackmd.io/@myid/demo-short-id",
          "shortId": "demo-short-id",
          "content": "demo content",
          "lastChangedAt": 1723704200160,
          "lastChangeUser": {
            "name": "demo-display-name",
            "userPath": "myid",
            "photo": "https://pbs.twimg.com/profile_images/1615717189/reonald_normal.jpg",
            "biography": null
          },
          "userPath": "demo-user-path",
          "teamPath": null,
          "readPermission": "owner",
          "writePermission": "owner"
        }
        """;

    NoteDetailResponse response = mapper.readValue(json, NoteDetailResponse.class);

    assertEquals("demo-long-id", response.id());
    assertEquals("demo contents title", response.title());
    assertEquals(2, response.tags().size());
    assertEquals(Instant.ofEpochMilli(1723685238684L), response.createdAt());
    assertEquals(Instant.ofEpochMilli(1723692520478L), response.titleUpdatedAt());
    assertEquals(Instant.ofEpochMilli(1723692520477L), response.tagsUpdatedAt());
    assertEquals("view", response.publishType());
    assertNull(response.permalink());
    assertEquals("https://hackmd.io/@myid/demo-short-id", response.publishLink());
    assertEquals("demo-short-id", response.shortId());
    assertNotNull(response.content());
    assertEquals(Instant.ofEpochMilli(1723704200160L), response.lastChangedAt());
    assertEquals("demo-display-name", response.lastChangeUser().name());
    assertEquals("demo-user-path", response.userPath());
    assertEquals("owner", response.readPermission());
    assertEquals("owner", response.writePermission());
  }

  @Test
  void deserializeIsoTimestampsFallback() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    String json =
        """
        {
          "id": "fallback",
          "title": "iso timestamps",
          "tags": [],
          "createdAt": "2024-08-15T10:00:00Z",
          "titleUpdatedAt": "2024-08-15T10:05:00Z",
          "tagsUpdatedAt": "2024-08-15T10:06:00Z",
          "publishType": "owner",
          "publishedAt": null,
          "permalink": null,
          "publishLink": null,
          "shortId": "iso",
          "content": "",
          "lastChangedAt": "2024-08-15T10:07:00Z",
          "lastChangeUser": {
            "name": "tester",
            "userPath": "tester",
            "photo": null,
            "biography": null
          },
          "userPath": "tester",
          "teamPath": null,
          "readPermission": "owner",
          "writePermission": "owner"
        }
        """;

    NoteDetailResponse response = mapper.readValue(json, NoteDetailResponse.class);

    assertEquals(Instant.parse("2024-08-15T10:00:00Z"), response.createdAt());
    assertEquals(Instant.parse("2024-08-15T10:05:00Z"), response.titleUpdatedAt());
    assertEquals(Instant.parse("2024-08-15T10:06:00Z"), response.tagsUpdatedAt());
    assertEquals(Instant.parse("2024-08-15T10:07:00Z"), response.lastChangedAt());
  }
}
