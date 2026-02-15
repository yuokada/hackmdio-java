package io.github.yuokada.hackmd.model;

/**
 * Represents the request body for creating a new HackMD note.
 *
 * @param title           The title of the note.
 * @param content         The content of the note.
 * @param readPermission  The read permission for the note.
 * @param writePermission The write permission for the note.
 */
public record CreateNoteRequest(
    String title, String content, String readPermission, String writePermission) {}
