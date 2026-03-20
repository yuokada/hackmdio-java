---
applyTo: "src/main/java/**/*.java,src/test/java/**/*.java,pom.xml,src/main/resources/application.properties"
---

When editing Java or Maven files in this repository:

- Keep command parsing and CLI flow in command classes, not in low-level service or model classes.
- Keep HackMD API access, authorization handling, Couchbase Lite persistence, and formatting utilities separated by responsibility.
- Do not introduce real tokens, credentials, or writable local paths into source, tests, or configuration examples.
- Keep tests close to the affected command, API, or utility behavior.
