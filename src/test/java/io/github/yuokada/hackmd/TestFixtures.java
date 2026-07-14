package io.github.yuokada.hackmd;

import io.github.yuokada.hackmd.model.NoteDetailResponse;
import java.time.Instant;
import java.util.List;

final class TestFixtures {

    private TestFixtures() {}

    static NoteDetailResponse note(String id, String title, String content, Instant lastChangedAt) {
        return new NoteDetailResponse(
                id,
                title,
                List.of("java"),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z"),
                "view",
                Instant.parse("2024-01-01T00:00:00Z"),
                "https://hackmd.io/" + id,
                "https://hackmd.io/" + id,
                id,
                content,
                lastChangedAt,
                null,
                "user",
                null,
                "owner",
                "owner");
    }
}
