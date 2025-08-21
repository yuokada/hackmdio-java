# GEMINI.md - Project Summary

This document summarizes the work done by the Gemini agent on this project.

## 1. Project Overview

The goal of this project is to create a Java-based command-line interface (CLI) for interacting with the HackMD API. The application is built using the Quarkus framework, making it fast, lightweight, and suitable for native compilation.

## 2. Features Implemented

- **`list` command:** Fetches all notes from the HackMD API and displays them in a formatted table. The table includes the note's ID, Title, and Published Date. The list is sorted by publication date (newest first), and handles notes with no publication date gracefully.
- **`create` command:** Creates a new note on HackMD with a specified title and content.
- **`get` command:** Retrieves a specific note by its ID and displays its title and content.
- **SQLite Database Persistence:** Note metadata (ID, title, publication date, etc.) fetched from the API is automatically saved and updated in a local SQLite database (`hackmd_notes.db`).
- **Code Style Enforcement:** The project is configured with `maven-checkstyle-plugin` to enforce the Google Java Style Guide, ensuring code quality and consistency.

## 3. Architecture & Technology Stack

- **Framework:** Quarkus (`3.25.4`)
- **Language:** Java (`17`)
- **Core Libraries:**
    - `quarkus-picocli`: For building the command-line interface.
    - `quarkus-rest-client-jackson`: For communicating with the HackMD REST API.
    - `quarkus-hibernate-orm-panache`: For database access and persistence.
    - `quarkus-jdbc-sqlite4j`: JDBC driver for SQLite.
- **Database:** SQLite (file: `hackmd_notes.db`)
- **Build Tool:** Maven

## 4. How to Run

### Prerequisites

- JDK 17+
- Maven
- A valid HackMD API Token

### Build

To build the project and create a runnable JAR, run:

```bash
./mvnw package
```

### Execution

1.  **Set the API Token:** Export your HackMD API token as an environment variable.

    ```bash
    export HACKMD_API_TOKEN="your_hackmd_api_token"
    ```

2.  **Run Commands:** Use the generated JAR file to execute commands.

    ```bash
    # List all notes
    java -jar target/hackmd-1.0.0-SNAPSHOT.jar list

    # Create a new note
    java -jar target/hackmd-1.0.0-SNAPSHOT.jar create --title "My New Note" --content "Hello from Gemini!"

    # Get a specific note
    java -jar target/hackmd-1.0.0-SNAPSHOT.jar get <NOTE_ID>
    ```

## 5. Database

The application will automatically create and manage a SQLite database file named `hackmd_notes.db` in the project's root directory. This file stores the metadata of the notes you interact with.
