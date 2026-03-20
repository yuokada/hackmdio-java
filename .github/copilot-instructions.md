# GitHub Copilot Instructions

Follow these repository instructions when working in this project.

## General guidance

- Keep changes focused and consistent with the existing Quarkus + Picocli HackMD CLI architecture.
- Write new or updated repository instructions, comments, and documentation in English.
- Avoid machine-specific paths, local-only assumptions, and committed API tokens.
- Preserve clear boundaries between CLI commands, API client logic, persistence, and utility code.
- Keep user-facing CLI behavior stable unless the task explicitly changes it.

## Project context

- Main code lives under `src/main/java/io/github/yuokada/quarkus/`.
- API and persistence models live under `src/main/java/io/github/yuokada/quarkus/model/`.
- Runtime configuration is defined in `src/main/resources/application.properties`.
- Build and style rules are managed via Maven Wrapper and `pom.xml`.

## Validation

- Prefer `./mvnw test` for behavior changes.
- Prefer `./mvnw package` when packaging or CLI startup behavior may be affected.
- Clearly distinguish between checks you ran and checks you did not run.
