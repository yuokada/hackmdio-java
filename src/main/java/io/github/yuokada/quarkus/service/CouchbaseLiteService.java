package io.github.yuokada.quarkus.service;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.FullTextIndex;
import com.couchbase.lite.FullTextIndexItem;
import com.couchbase.lite.IndexBuilder;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import io.github.yuokada.quarkus.model.NoteDetailResponse;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Service for managing Couchbase Lite database operations.
 */
@ApplicationScoped
@Startup
public class CouchbaseLiteService {
  private static final Logger logger = Logger.getLogger(CouchbaseLiteService.class);

  private static final String DATABASE_NAME = "hackmd_notes";
  private static final String FTS_INDEX_NAME = "content_fts_index";
  private static final String ID_PREFIX = "note::";

  private Database database;

  @ConfigProperty(name = "couchbase.lite.database.path", defaultValue = ".")
  String databasePath;

  /**
   * Initializes the Couchbase Lite database.
   */
  public void init() {
    try {
      CouchbaseLite.init();
      DatabaseConfiguration config = new DatabaseConfiguration();
      config.setDirectory(databasePath);
      database = new Database(DATABASE_NAME, config);
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize Couchbase Lite database", e);
    }
  }

  /**
   * Creates a full-text search index for note content.
   */
  public void createFtsIndex() {
    try {
      if (database == null) {
        init();
      }
      // Create FTS index on content and title fields
      FullTextIndex ftsIndex =
          IndexBuilder.fullTextIndex(
              FullTextIndexItem.property("content"), FullTextIndexItem.property("title"));
      database.createIndex(FTS_INDEX_NAME, ftsIndex);
    } catch (Exception e) {
      // Index might already exist, which is fine
      logger.error("FTS Index creation note: " + e.getMessage());
    }
  }

  /**
   * Saves or updates a note in the database.
   *
   * @param note The note to save.
   */
  public void saveNote(NoteDetailResponse note) {
    try {
      if (database == null) {
        init();
      }
      MutableDocument doc = new MutableDocument(toDocId(note.id()));
      doc.setString("id", note.id());
      doc.setString("shortId", note.shortId());
      doc.setString("title", note.title());
      doc.setString("content", note.content());

      if (note.tags() != null) {
        com.couchbase.lite.MutableArray tagsArray = new com.couchbase.lite.MutableArray();
        for (String tag : note.tags()) {
          tagsArray.addString(tag);
        }
        doc.setArray("tags", tagsArray);
      }

      if (note.lastChangedAt() != null) {
        doc.setString("updatedAt", note.lastChangedAt().toString());
      }

      if (note.publishedAt() != null) {
        doc.setString("publishedAt", note.publishedAt().toString());
      }

      // Set downloadedAt to current time
      doc.setString("downloadedAt", Instant.now().toString());

      database.save(doc);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save note: " + note.id(), e);
    }
  }

  /**
   * Retrieves a note document from the database.
   *
   * @param noteId The ID of the note to retrieve.
   * @return A map containing the note data, or null if not found.
   */
  public Map<String, Object> getNote(String noteId) {
    try {
      if (database == null) {
        init();
      }
      com.couchbase.lite.Document doc = database.getDocument(toDocId(noteId));
      if (doc == null) {
        return null;
      }
      return doc.toMap();
    } catch (Exception e) {
      throw new RuntimeException("Failed to get note: " + noteId, e);
    }
  }

  /**
   * Checks if a note needs to be updated based on updatedAt timestamp.
   *
   * @param noteId    The ID of the note.
   * @param updatedAt The updatedAt timestamp from the API.
   * @return true if the note needs to be updated, false otherwise.
   */
  public boolean needsUpdate(String noteId, Instant updatedAt) {
    try {
      if (database == null) {
        init();
      }
      com.couchbase.lite.Document doc = database.getDocument(toDocId(noteId));
      if (doc == null) {
        return true; // Note doesn't exist, needs to be fetched
      }

      String storedUpdatedAtStr = doc.getString("updatedAt");

      if (updatedAt == null) {
        return false; // No updatedAt from API, skip update
      }

      if (storedUpdatedAtStr == null) {
        return true; // No stored updatedAt, fetch the note
      }

      Instant storedUpdatedAt = Instant.parse(storedUpdatedAtStr);
      return updatedAt.isAfter(storedUpdatedAt);
    } catch (Exception e) {
      return true; // On error, fetch the note
    }
  }

  /**
   * Searches notes using full-text search.
   *
   * @param searchTerm The term to search for.
   * @return A list of search results.
   */
  public List<Map<String, Object>> searchNotes(String searchTerm) {
    try {
      if (database == null) {
        init();
      }

      Query query =
          QueryBuilder.select(
                  SelectResult.expression(com.couchbase.lite.Meta.id),
                  SelectResult.property("id"),
                  SelectResult.property("shortId"),
                  SelectResult.property("title"),
                  SelectResult.property("content"),
                  SelectResult.property("tags"),
                  SelectResult.property("updatedAt"))
              .from(com.couchbase.lite.DataSource.database(database))
              .where(
                  com.couchbase.lite.FullTextFunction.match(
                      com.couchbase.lite.Expression.fullTextIndex(FTS_INDEX_NAME), searchTerm));

      ResultSet results = query.execute();
      List<Map<String, Object>> searchResults = new ArrayList<>();

      for (Result result : results) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", result.getString("id"));
        resultMap.put("shortId", result.getString("shortId"));
        resultMap.put("title", result.getString("title"));
        resultMap.put("content", result.getString("content"));
        resultMap.put("updatedAt", result.getString("updatedAt"));

        com.couchbase.lite.Array tagsArray = result.getArray("tags");
        if (tagsArray != null) {
          List<String> tags = new ArrayList<>();
          for (int i = 0; i < tagsArray.count(); i++) {
            tags.add(tagsArray.getString(i));
          }
          resultMap.put("tags", tags);
        }

        searchResults.add(resultMap);
      }

      return searchResults;
    } catch (Exception e) {
      throw new RuntimeException("Failed to search notes", e);
    }
  }

  private static String toDocId(String noteId) {
    if (noteId.startsWith(ID_PREFIX)) {
      return noteId;
    }
    return ID_PREFIX + noteId;
  }

  /**
   * Closes the database.
   */
  public void close() {
    try {
      if (database != null) {
        database.close();
      }
    } catch (Exception e) {
      logger.warn("Failed to close Couchbase Lite database", e);
    }
  }
}
