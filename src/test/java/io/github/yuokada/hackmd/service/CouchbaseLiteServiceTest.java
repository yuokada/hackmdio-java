package io.github.yuokada.hackmd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.couchbase.lite.Collection;
import com.couchbase.lite.MutableDocument;
import io.github.yuokada.hackmd.model.NoteDetailResponse;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CouchbaseLiteServiceTest {

    @TempDir
    Path databaseDirectory;

    private CouchbaseLiteService service;

    @AfterEach
    void tearDown() {
        if (service != null) {
            service.close();
        }
    }

    @Test
    void createsIndexIdempotentlyAndRemovesMissingNotes() throws Exception {
        service = new CouchbaseLiteService();
        service.databasePath = databaseDirectory.toString();
        service.init();
        service.createFtsIndex();
        service.createFtsIndex();
        service.saveNote(note("keep"));
        service.saveNote(note("delete"));
        Collection collection = collectionFrom(service);
        collection.save(new MutableDocument("other::document"));

        assertEquals(1, service.removeMissingNotes(Set.of("keep")));
        assertNotNull(service.getNote("keep"));
        assertNull(service.getNote("delete"));
        assertNotNull(collection.getDocument("other::document"));
    }

    private static NoteDetailResponse note(String id) {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
        return new NoteDetailResponse(
                id,
                "Title " + id,
                List.of("test"),
                timestamp,
                timestamp,
                timestamp,
                "view",
                timestamp,
                null,
                null,
                id,
                "Body " + id,
                timestamp,
                null,
                null,
                null,
                "owner",
                "owner");
    }

    private static Collection collectionFrom(CouchbaseLiteService service) throws Exception {
        Field field = CouchbaseLiteService.class.getDeclaredField("collection");
        field.setAccessible(true);
        return (Collection) field.get(service);
    }
}
