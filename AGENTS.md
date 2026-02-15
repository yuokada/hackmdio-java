# Repository Guidelines

## Overview

A Quarkus + Picocli CLI application for interacting with the HackMD API. It supports listing, creating, and retrieving notes, plus offline full-text search via a local Couchbase Lite database.

## Project Structure

- `src/main/java/io/github/yuokada/hackmd`: CLI commands, API client, service layer, and models.
- `src/main/java/io/github/yuokada/hackmd/model`: API and persistence models (Java records).
- `src/main/resources`: configuration files such as `application.properties`.
- `target/`: Maven build output (generated, do not edit).

## Architecture

**Root package**: `io.github.yuokada.hackmd`

- **Commands** (Picocli): `HackmdCommand` (root) aggregates subcommands — `ListCommand`, `CreateCommand`, `GetCommand`, `IndexCommand`, `SearchCommand`
- **API layer**: `HackMdApi` (MicroProfile REST Client interface) → `HackMdService` (business logic) → `AuthorizationFilter` (injects Bearer token)
- **Local storage**: `CouchbaseLiteService` (@Startup, ApplicationScoped) manages Couchbase Lite for offline indexing and FTS search
- **Models**: Java records in `model/` package — `Note`, `NoteDetailResponse`, `IndexedNote`, `CreateNoteRequest`, `UpdateNoteRequest`, etc.

REST client base URL configured in `application.properties` as `quarkus.rest-client.hackmd-api.url`.

## Build, Test, and Development Commands

```bash
# Build
./mvnw clean compile
./mvnw package                    # Build über-jar to target/

# Run in dev mode
./mvnw quarkus:dev
./mvnw quarkus:dev -Dquarkus.args='list --json'   # Pass CLI args

# Tests
./mvnw test                       # Unit tests only
./mvnw verify -DskipITs=false     # Include integration tests
# Run a single test class:
./mvnw test -Dtest=AuthorizationFilterTest
# Run a single test method:
./mvnw test -Dtest=AuthorizationFilterTest#testFilterAddsAuthorizationHeader

# Linting
./mvnw checkstyle:check           # Google Java Style (runs automatically during validate phase)
```

**Java 17** is required. The Maven wrapper (`./mvnw`) is included.

## Coding Style & Naming Conventions

- Google Java Style enforced by Checkstyle (4-space indentation)
- PascalCase for classes, lowerCamelCase for methods/variables
- Models use Java records
- Test classes follow `*Test.java` naming convention

## Testing Guidelines

- JUnit 5 with `quarkus-junit5` and `quarkus-junit5-mockito`
- WireMock (`quarkus-wiremock-test`) for HTTP stubbing
- Place tests in `src/test/java` mirroring main package structure
- Run unit tests with `./mvnw test`
- For integration tests, use `./mvnw verify -DskipITs=false`

## Configuration & Security

- Set `HACKMD_API_TOKEN` environment variable (see `.env.sample`).
- The Couchbase Lite database (`hackmd_notes.cblite2`) is stored at `${user.home}/.config/hackmd-sync/couchbase` by default, configurable via `couchbase.lite.database.path` property.

## CI

GitHub Actions runs `./mvnw -B test` on pushes to master and all PRs (Temurin JDK 17).

## Commit & Pull Request Guidelines

- Commit messages are short, imperative English (example: "Add --json to list command").
- PRs should include a summary, verification steps, and scope impact (CLI output, config changes).
- Link related issues and include brief usage examples for new features.
