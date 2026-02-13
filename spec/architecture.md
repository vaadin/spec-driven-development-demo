# Architecture

> Technology stack and application structure. This drives how AI agents scaffold code.
>
> **How to use:** Fill in the tables below. Replace all `[bracketed text]` with your content.

---

## 1. Technology Stack

| Layer | Technology | Version | Notes |
|-------|-----------|---------|-------|
| **UI Framework** | Vaadin Flow (Aura theme) | 25.1 | Server-side Java UI |
| **Backend Framework** | Spring Boot | 4.0 | Auto-configuration, embedded Tomcat |
| **Language** | Java | 21 | LTS release |
| **Build Tool** | Maven | — | Maven wrapper included |
| **Database** | [e.g., PostgreSQL, H2] | [version] | [Production vs. dev profiles] |
| **Testing** | [e.g., JUnit 5, TestBench] | [version] | [Notes] |

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

- Views go in feature-specific subpackages under `com.example.specdriven`
- Routes are declared via `@Route("path")` and page titles via `@PageTitle`
- Custom CSS: `src/main/resources/META-INF/resources/styles.css`
- Auto-generated frontend: `src/main/frontend/generated/` (do not edit)

---

## 3. Data Model *(optional)*

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| [Entity 1] | [field1, field2] | [Has many Entity2] |
| [Entity 2] | [field1, field2] | [Belongs to Entity1] |
