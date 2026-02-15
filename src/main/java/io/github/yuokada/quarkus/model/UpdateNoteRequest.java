package io.github.yuokada.quarkus.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents the payload for updating note metadata or content.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateNoteRequest(
    String title,
    String content,
    String readPermission,
    String writePermission,
    String permalink,
    String parentFolderId) {}
