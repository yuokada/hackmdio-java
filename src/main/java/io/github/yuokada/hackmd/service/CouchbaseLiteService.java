package io.github.yuokada.hackmd.service;

import com.couchbase.lite.Array;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.FullTextFunction;
import com.couchbase.lite.FullTextIndex;
import com.couchbase.lite.FullTextIndexItem;
import com.couchbase.lite.IndexBuilder;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.lite.logging.ConsoleLogSink;
import com.couchbase.lite.logging.LogSinks;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Service for managing Couchbase Lite database operations.
 *
 * <p>The database is initialized lazily on first use and closed by the CDI shutdown lifecycle.</p>
 */
@ApplicationScoped
public class CouchbaseLiteService {
    private static final Logger logger = Logger.getLogger(CouchbaseLiteService.class);

    private static final String DATABASE_NAME = "hackmd_notes";
    private static final String FTS_INDEX_NAME = "content_fts_index";
    private static final String ID_PREFIX = "note::";

    private Database database;
    private Collection collection;

    @ConfigProperty(name = "couchbase.lite.database.path", defaultValue = ".")
    String databasePath;

    /**
     * Initializes the Couchbase Lite database.
     */
    public synchronized void init() {
        if (database != null) {
            return;
        }
        try {
            CouchbaseLite.init();
            // Disable Couchbase Lite logging
            LogSinks.get().setConsole(new ConsoleLogSink(LogLevel.NONE));
            Path dbDir = Path.of(databasePath);
            Files.createDirectories(dbDir);
            if (dbDir.getFileSystem().supportedFileAttributeViews().contains("posix")) {
                Files.setPosixFilePermissions(dbDir, PosixFilePermissions.fromString("rwx------"));
            }
            DatabaseConfiguration config = new DatabaseConfiguration();
            config.setDirectory(databasePath);
            database = new Database(DATABASE_NAME, config);
            collection = database.getDefaultCollection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Couchbase Lite database", e);
        }
    }

    /**
     * Creates a full-text search index for note content.
     */
    public void createFtsIndex() {
        try {
            ensureInitialized();
            if (collection.getIndexes().contains(FTS_INDEX_NAME)) {
                return;
            }
            // Create FTS index on content and title fields
            FullTextIndex ftsIndex = IndexBuilder.fullTextIndex(
                    FullTextIndexItem.property("content"), FullTextIndexItem.property("title"));
            collection.createIndex(FTS_INDEX_NAME, ftsIndex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create FTS index", e);
        }
    }

    /**
     * Saves or updates a note in the database.
     *
     * @param note The note to save.
     */
    public void saveNote(NoteDetailResponse note) {
        try {
            ensureInitialized();
            String documentId = toDocId(note.id());
            Document existingDocument = collection.getDocument(documentId);
            MutableDocument doc =
                    existingDocument == null ? new MutableDocument(documentId) : existingDocument.toMutable();
            doc.setString("id", note.id());
            doc.setString("shortId", note.shortId());
            doc.setString("title", note.title());
            doc.setString("content", note.content());

            if (note.tags() != null) {
                MutableArray tagsArray = new MutableArray();
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

            collection.save(doc);
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
            ensureInitialized();
            Document doc = collection.getDocument(toDocId(noteId));
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
            ensureInitialized();
            Document doc = collection.getDocument(toDocId(noteId));
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
            ensureInitialized();

            Query query = QueryBuilder.select(
                            SelectResult.expression(Meta.id),
                            SelectResult.property("id"),
                            SelectResult.property("shortId"),
                            SelectResult.property("title"),
                            SelectResult.property("content"),
                            SelectResult.property("tags"),
                            SelectResult.property("updatedAt"))
                    .from(DataSource.collection(collection))
                    .where(FullTextFunction.match(Expression.fullTextIndex(FTS_INDEX_NAME), searchTerm));

            ResultSet results = query.execute();
            List<Map<String, Object>> searchResults = new ArrayList<>();

            for (Result result : results) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("id", result.getString("id"));
                resultMap.put("shortId", result.getString("shortId"));
                resultMap.put("title", result.getString("title"));
                resultMap.put("content", result.getString("content"));
                resultMap.put("updatedAt", result.getString("updatedAt"));

                Array tagsArray = result.getArray("tags");
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

    /**
     * Removes indexed note documents that are no longer present in a complete remote listing.
     *
     * @param remoteNoteIds note IDs returned by the HackMD API
     * @return the number of deleted local documents
     */
    public int removeMissingNotes(Set<String> remoteNoteIds) {
        try {
            ensureInitialized();
            Query query = QueryBuilder.select(SelectResult.expression(Meta.id).as("documentId"))
                    .from(DataSource.collection(collection));
            int deleted = 0;
            for (Result result : query.execute()) {
                String documentId = result.getString("documentId");
                if (documentId == null || !documentId.startsWith(ID_PREFIX)) {
                    continue;
                }
                String noteId = documentId.substring(ID_PREFIX.length());
                if (!remoteNoteIds.contains(noteId)) {
                    Document document = collection.getDocument(documentId);
                    if (document != null) {
                        collection.delete(document);
                        deleted++;
                    }
                }
            }
            return deleted;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove stale notes", e);
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
    @PreDestroy
    public synchronized void close() {
        try {
            if (database != null) {
                database.close();
                database = null;
                collection = null;
            }
        } catch (Exception e) {
            logger.warn("Failed to close Couchbase Lite database", e);
        }
    }

    private synchronized void ensureInitialized() {
        if (database == null) {
            init();
        }
    }
}
