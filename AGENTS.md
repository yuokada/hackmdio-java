# Repository Guidelines

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

A Quarkus + Picocli CLI application for interacting with the HackMD API. It supports listing, creating, retrieving, and opening notes, plus offline full-text search via a local Couchbase Lite database. Packaged as an uber-jar.

## Build, Test, and Lint Commands

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
./mvnw test -Dtest=AuthorizationFilterTest                                  # Single test class
./mvnw test -Dtest=AuthorizationFilterTest#testFilterAddsAuthorizationHeader # Single test method

# Linting
./mvnw checkstyle:check           # Google Java Style (also runs during validate phase)
```

**Java 17** is required (`maven.compiler.release=17`). The Maven wrapper (`./mvnw`) is included.

## Architecture

**Root package**: `io.github.yuokada.hackmd`

- **Commands** (Picocli): `HackmdCommand` (root, `@TopCommand`) aggregates subcommands — `ListCommand`, `CreateCommand`, `GetCommand`, `OpenCommand`, `IndexCommand`, `SearchCommand`
- **API layer**: `HackMdApi` (MicroProfile REST Client interface; list/create/get note endpoints, base URL in `application.properties`) → `HackMdService` (business logic, `@ApplicationScoped`). `client/HackmdRestClient` mirrors the Swagger endpoints (teams/history/update/delete, etc.) and is currently not wired to commands. `AuthorizationFilter` (`ClientRequestFilter`, injects Bearer token from `hackmd.api.token` config property)
- **Local storage**: `CouchbaseLiteService` (`@Startup`, `@ApplicationScoped`) manages Couchbase Lite for offline indexing and FTS search. Document operations use `Collection` (v4 API).
- **Models**: Java records (plus helpers) in `model/` package — `Note`, `NoteDetailResponse`, `IndexedNote`, `CreateNoteRequest`, `UpdateNoteRequest`, `Team`, `TeamVisibility`, `UserProfile`, `EpochMillisInstantDeserializer`
- **Utilities**: `SnippetUtil` generates search snippets for `SearchCommand`

## Coding Style

- Google Java Style enforced by Checkstyle (4-space indentation)
- Models use Java records
- Test classes follow `*Test.java` naming

## Testing

- JUnit 5 with `quarkus-junit5` and `quarkus-junit5-mockito`
- WireMock (`quarkus-wiremock-test`) for HTTP stubbing
- Tests capture `System.out`/`System.err` via `ByteArrayOutputStream` in `@BeforeEach`/`@AfterEach`

## Configuration

- `HACKMD_API_TOKEN` environment variable for API authentication (see `.env.sample`)
- Couchbase Lite database path: `${user.home}/.config/hackmd-sync/couchbase` (configurable via `couchbase.lite.database.path`)
- REST client base URL: `quarkus.rest-client.hackmd-api.url=https://api.hackmd.io`

## CI

GitHub Actions runs `./mvnw -B test` on pushes to `master` and all PRs (Temurin JDK 17). Dependabot opens monthly PRs for Maven dependencies and GitHub Actions. PRs are auto-labeled by `actions/labeler` based on changed file paths.

## Commit & PR Guidelines

- Commit messages: short, imperative English (e.g., "Add --json to list command")
- PRs: include summary, verification steps, and scope impact
