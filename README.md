# hackmd

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/hackmd-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Picocli ([guide](https://quarkus.io/guides/picocli)): Develop command line applications with Picocli

## Provided Code

### Picocli Example

Hello and goodbye are civilization fundamentals. Let's not forget it with this example picocli application by changing the <code>command</code> and <code>parameters</code>.

[Related guide section...](https://quarkus.io/guides/picocli#command-line-application-with-multiple-commands)

Also for picocli applications the dev mode is supported. When running dev mode, the picocli application is executed and on press of the Enter key, is restarted.

As picocli applications will often require arguments to be passed on the commandline, this is also possible in dev mode via:

```shell script
./mvnw quarkus:dev -Dquarkus.args='Quarky'
```

## Requirements

You need a HackMD account at https://hackmd.io/ and must set the HackMD API token in the `HACKMD_API_TOKEN` environment variable before running the CLI.
For API token creation, see [How to issue an API token](https://hackmd.io/@hackmd-api/developer-portal/https%3A%2F%2Fhackmd.io%2F%40hackmd-api%2Fhow-to-issue-an-api-token?utm_source=settings-api&utm_medium=inline-cta)

- [Getting Started \- HackMD](https://hackmd.io/@hackmd-api/developer-portal/https%3A%2F%2Fhackmd.io%2F%40hackmd-api%2FrkoVeBXkq?utm_source=settings-api&utm_medium=inline-cta)
- [Swagger UI](https://api.hackmd.io/v1/docs?_gl=1*1vheg5h*_ga*MTkxOTM2NDA5MC4xNzQyODU4OTg5*_ga_NGVZMM6DR6*czE3NjgxNDEyODgkbzkzJGcxJHQxNzY4MTQxOTMwJGoxMyRsMSRoMTQ1Njc0MTM5NA..)

## CI

Every push and pull request targeting `master` triggers the GitHub Actions workflow defined in `.github/workflows/ci.yml`, which sets up Temurin JDK 17 and runs `./mvnw -B test` to ensure the CLI continues to build and pass tests.

Dependency updates are monitored by Dependabot (`.github/dependabot.yml`), which opens weekly pull requests for Maven dependencies and GitHub Actions.

## CLI Commands

The application exposes the following subcommands:

- `list`: List notes from HackMD API
- `create`: Create a new note on HackMD
- `get`: Get a specific note by ID from HackMD API
- `index`: Index notes from HackMD to local Couchbase Lite database for offline search
- `search`: Search notes in local database using full-text search

### Usage Examples

```bash
# List all notes from HackMD
java -jar target/hackmd-1.0.0-SNAPSHOT.jar list

# Create a new note
java -jar target/hackmd-1.0.0-SNAPSHOT.jar create --title "My Note" --content "Hello World"

# Get a specific note
java -jar target/hackmd-1.0.0-SNAPSHOT.jar get <note-id>

# Index all notes to local database
java -jar target/hackmd-1.0.0-SNAPSHOT.jar index

# Search notes in local database
java -jar target/hackmd-1.0.0-SNAPSHOT.jar search "search term"
```

### Index and Search Features

The `index` command synchronizes your HackMD notes to a local Couchbase Lite database, enabling fast offline search capabilities:

- **Smart Sync**: Only downloads new or updated notes based on timestamps
- **Progress Tracking**: Shows real-time progress during indexing
- **Summary Report**: Displays statistics about new, updated, and skipped notes

The `search` command performs full-text search on locally indexed notes:

- **Fast Search**: Uses FTS (Full-Text Search) index for quick results
- **Offline**: Works without internet connection after indexing
- **Content Search**: Searches both title and content fields

Indexed data is stored under `$HOME/.hackmdio/` by default. You can override the location by setting the `couchbase.lite.database.path` configuration property if you want to use a different directory.
