# Architecture

> Technology stack and application structure. `pom.xml` is the source of truth for versions. Do not modify `pom.xml`, `vite.config.ts`, or `architecture.md` without asking.

---

## 1. Technology Stack

- Vaadin — server-side Java UI for admin views, client-side React for public views
- Spring Boot — auto-configuration, embedded Tomcat
- Java
- Maven (wrapper included)
- Database: [e.g., PostgreSQL, H2]
- Routing: Vaadin Flow views use `@Route`. Hilla React views use file-based routing (`src/main/frontend/views/`), not `src/main/frontend/routes.tsx`.
- Testing: JUnit 5, Vaadin Browserless Tests (`browserless-test-junit6`), Vitest for React views

---

## 2. Application Structure

```
com.example.specdriven/
  Application.java              — Spring Boot entry point
  [feature-package]/
    [FeatureView].java          — Vaadin @Route view
    [FeatureService].java       — Business logic (Spring @Service)
    [FeatureRepository].java    — Data access (Spring Data)
```

---

## 3. UIState Management

- **Signals** are the primary mechanism for managing UI state
- **Non-shared signals** for standard per-user UI state (e.g., form values, selection state, view-local data)
- **Shared signals** when state must be visible across multiple users/sessions (collaborative or real-time features) — requires **server push** to be enabled
- When using shared signals, enable push on the view/UI (e.g., `@Push` annotation)

---

## 4. Security & Admin

- **Spring Security** with `VaadinSecurityConfigurer`
- Public views: `@AnonymousAllowed` (React Hilla endpoints, public routes)
- Admin views: `@RolesAllowed("ADMIN")` (Vaadin Flow views)
- Login: Vaadin `LoginOverlay` at `/login`
- A "Forgot password" button on the login screen reveals the available demo usernames and passwords inline on the login view itself (not as a notification or popup — this is a demo app, not a real password-reset flow). The button must be labeled "Forgot password" (not "Show demo credentials" or similar).
- A "Log out" link is shown in the top navbar for authenticated users. Clicking it ends the session and redirects to the login page.
