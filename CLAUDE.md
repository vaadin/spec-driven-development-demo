# CLAUDE.md

## Build & Run Commands

```bash
./mvnw                            # Run in dev mode (default goal: spring-boot:run)
./mvnw clean package              # Production build (JAR in target/)
./mvnw test                       # Run all tests
./mvnw test -Dtest=ClassName      # Run a single test class
```

The app runs on port 8080 (configurable via `PORT` env var).

## Specifications

Project specs live in `spec/`. Read [`spec/README.md`](spec/README.md) first.

## Implementation Workflow

When implementing a use case:

1. Read the use case spec and referenced specs (architecture, data model)
2. Implement the feature (backend + view)
3. Run `./mvnw test` to verify compilation and existing tests pass
4. Start the dev server (`./mvnw`) and use Playwright MCP to verify the implementation visually — walk through the main flow and acceptance criteria
5. Write UI Unit tests (Vaadin TestBench) covering the acceptance criteria
6. Run `./mvnw test` to verify all tests pass
7. Commit

## Guardrails

- Do not modify `pom.xml` without asking
- Do not modify `spec/architecture.md` without asking
