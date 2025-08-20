package io.github.yuokada.quarkus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Represents a note entity for persistence in the database.
 */
@Entity
@Table(name = "notes")
public class NoteEntity {

  @Id
  public String id;

  @Column(unique = true)
  public String shortId;

  public String title;

  public Instant publishedAt;

  // Default constructor is required by JPA
  public NoteEntity() {
  }

  /**
   * Creates a new NoteEntity from a Note record.
   *
   * @param note The note record from the API.
   * @return A new NoteEntity.
   */
  public static NoteEntity from(Note note) {
    NoteEntity entity = new NoteEntity();
    entity.id = note.id();
    entity.shortId = note.shortId();
    entity.title = note.title();
    entity.publishedAt = note.publishedAt();
    return entity;
  }
}