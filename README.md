# hackmdio-java

A Java CLI tool for interacting with the [HackMD](https://hackmd.io/) API. It supports listing, creating, and retrieving notes, as well as offline full-text search powered by a local Couchbase Lite database.

Built with Quarkus + Picocli and packaged as a standalone uber-jar.

## Requirements

- Java 17 or later
- A HackMD account and API token

See [How to issue an API token](https://hackmd.io/@hackmd-api/developer-portal/https%3A%2F%2Fhackmd.io%2F%40hackmd-api%2Fhow-to-issue-an-api-token?utm_source=settings-api&utm_medium=inline-cta) for token creation.

- [Getting Started \- HackMD](https://hackmd.io/@hackmd-api/developer-portal/https%3A%2F%2Fhackmd.io%2F%40hackmd-api%2FrkoVeBXkq?utm_source=settings-api&utm_medium=inline-cta)
- [Swagger UI](https://api.hackmd.io/v1/docs)

## Setup

```bash
# Set your API token
export HACKMD_API_TOKEN="your-api-token"

# Build the uber-jar
./mvnw package
```

## CLI Commands

| Command  | Description |
|----------|-------------|
| `list`   | List notes from the HackMD API |
| `create` | Create a new note |
| `get`    | Retrieve a specific note by ID |
| `index`  | Index notes to a local database for offline search |
| `search` | Full-text search on locally indexed notes |

### Usage Examples

```bash
JAR=target/hackmd-1.0.0-SNAPSHOT.jar

# List all notes
java -jar $JAR list

# List notes in JSON format
java -jar $JAR list --json

# Create a new note
java -jar $JAR create --title "My Note" --content "Hello World"

# Get a specific note
java -jar $JAR get <note-id>

# Index all notes to the local database
java -jar $JAR index

# Search the local database
java -jar $JAR search "search term"

# Search with JSON output
java -jar $JAR search --json "search term"
```

### Index and Search Features

The `index` command synchronizes your HackMD notes to a local Couchbase Lite database, enabling offline search:

- **Smart Sync**: Only downloads new or updated notes based on timestamps
- **Progress Tracking**: Shows real-time progress during indexing
- **Summary Report**: Displays statistics about new, updated, and skipped notes

The `search` command performs full-text search on locally indexed notes:

- **Fast Search**: Uses an FTS (Full-Text Search) index for quick results
- **Offline**: Works without an internet connection after indexing
- **Content Search**: Searches both title and content fields

## Development

```bash
# Run in dev mode
./mvnw quarkus:dev
./mvnw quarkus:dev -Dquarkus.args='list --json'

# Run tests
./mvnw test

# Checkstyle (Google Java Style)
./mvnw checkstyle:check
```

## CI

Every push and pull request targeting `master` triggers the GitHub Actions workflow defined in `.github/workflows/ci.yml`, which sets up Temurin JDK 17 and runs `./mvnw -B test` to ensure the CLI continues to build and pass tests.

Dependency updates are monitored by Dependabot (`.github/dependabot.yml`), which opens weekly pull requests for Maven dependencies and GitHub Actions.
