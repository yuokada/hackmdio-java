package io.github.yuokada.quarkus;

import io.github.yuokada.quarkus.model.NoteEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * A repository for NoteEntity, using the Panache repository pattern.
 */
@ApplicationScoped
public class NoteRepository implements PanacheRepositoryBase<NoteEntity, String> {
    // Panache will implement the basic CRUD methods for us.
    // We can add custom query methods here if needed.
}