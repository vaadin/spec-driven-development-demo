---
name: use-case-tests
description: Write and run tests for a use case. Use when writing tests for an implemented use case.
---

# Writing Tests for a Use Case

Each use case must be covered by the following tests:

## UI tests, one of
- **Browserless Tests**: Vaadin Browserless Testing (`SpringBrowserlessTest`)
  - If the view is implemented using Vaadin Flow
  - Tests live in `src/test/java/`, mirroring the main package structure
  - Extend `SpringBrowserlessTest`, annotate with `@SpringBootTest`
  - Use `@WithMockUser(roles = "ADMIN")` for admin views
  - Use `@WithAnonymousUser` for access control tests
  - Use `navigate(ViewClass.class)` to render views
  - Use `$(ComponentClass.class)` to query components, `test(component)` to interact
- **Vitest with React Testing Library**
  - If the view is implemented using React
  - Tests live in `src/test/frontend/`, mirroring the view structure
  - Mock `@BrowserCallable` endpoint calls
  - Test component rendering, user interactions, and navigation
  - Run via `npx vitest run`

## Backend / services tests
  - JUnit tests for Spring `@Service` classes
  - Tests live in `src/test/java/`, same as browserless tests
  - Annotate with `@SpringBootTest`, autowire the service
  - Test business rules, validation, and data access
  - Endpoints (`@BrowserCallable`) typically delegate to services -- test the service, not the endpoint

## Coverage Requirements

The use case's flows and business rules *are* the acceptance criteria. There is no separate acceptance-criteria list. Cover:

- **Main Flow** — at least one test exercises the happy path end to end.
- **Alternative Flows** — each `AF-N` has a dedicated test that triggers its condition and asserts its branch.
- **Business Rules** — each `BR-N` has a dedicated test, especially edge cases like limits, validation, and error handling.
- **Postconditions** — assert the success and failure postconditions in the relevant tests.

### Naming Conventions

- **Test class**: `[FeatureName]Test.java` or `[FeatureName].test.tsx` (e.g., `BrowseMoviesTest`, `BuyTickets.test.tsx`)
- **Test methods**: descriptive names that map to a flow, an alternative flow, or a business rule (e.g., `onlyItemsWithFutureEventsAreDisplayed`, `maximumSixItemsPerTransaction`, `rejectsCheckoutWhenCartEmpty`)
- **Structure**: one test class per use case, with individual test methods for the main flow, each alternative flow, and each business rule edge case
