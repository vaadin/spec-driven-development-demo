# Architecture

> Technology stack and application structure. `pom.xml` is the source of truth for versions. Do not modify `pom.xml` without asking.

---

## 1. Technology Stack

- Vaadin Flow (Aura theme) — server-side Java UI for admin views
- Hilla (React + `@BrowserCallable`) — client-side React views for public pages
- Spring Boot — auto-configuration, embedded Tomcat
- Java
- Maven (wrapper included)
- Database: H2
- Testing: JUnit 5, Vaadin TestBench (UI unit tests)

---

## 2. Application Structure

This is a **hybrid Flow + Hilla** application:
- **Public views** (browse movies, purchase tickets) are React (Hilla) views in `src/main/frontend/views/`, backed by `@BrowserCallable` + `@AnonymousAllowed` services
- **Admin views** (manage movies, manage shows, view bookings) are server-side Vaadin Flow views in Java

```
src/main/frontend/views/
  @index.tsx                    — Browse today's movies (Hilla React view)
  movie/{movieId}.tsx           — Movie details + ticket purchase (Hilla React view)

com.example.specdriven/
  Application.java              — Spring Boot entry point
  [feature-package]/
    [FeatureView].java          — Vaadin @Route view (admin views)
    [FeatureService].java       — @BrowserCallable or @Service (business logic)
    [FeatureRepository].java    — Data access (Spring Data)
```

- Application CSS: `src/main/resources/META-INF/resources/styles.css`
