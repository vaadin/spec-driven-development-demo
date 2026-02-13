# Non-Functional Requirements & Architecture

> This document defines the quality attributes (NFRs) the system must meet and outlines the architecture that supports them. Part A captures measurable targets for performance, security, and other qualities. Part B describes the technology stack, application structure, and key decisions.
>
> **How to use this template:**
> 1. Fill in Part A first — NFRs drive architecture choices.
> 2. Replace all `[bracketed text]` with your content.
> 3. Remove any optional sections that don't apply.
> 4. Reference this document from use cases when a feature has specific NFR implications.

---

**Project:** [Project Name]
**Version:** [X.Y]
**Date:** [YYYY-MM-DD]
**Status:** [Draft | Approved | Active]

---

# Part A — Non-Functional Requirements

## 1. Performance

| Metric | Target | Measurement Method |
|--------|--------|--------------------|
| [Page load time] | [e.g., < 2 seconds] | [e.g., Lighthouse, browser dev tools] |
| [API response time] | [e.g., < 500ms p95] | [e.g., Server logs, APM] |
| [Concurrent users] | [e.g., 50 simultaneous] | [e.g., Load testing] |

---

## 2. Security

- [Authentication method — e.g., Spring Security with form login]
- [Authorisation model — e.g., role-based access control]
- [Data protection — e.g., HTTPS only, sensitive data encrypted at rest]
- [Input validation — e.g., server-side validation on all user inputs]

---

## 3. Accessibility

- [Target standard — e.g., WCAG 2.1 AA]
- [Keyboard navigation required for all interactive elements]
- [Screen reader support — e.g., ARIA labels on custom components]
- [Colour contrast — e.g., minimum 4.5:1 ratio for normal text]

---

## 4. Scalability *(optional)*

- [Scaling strategy — e.g., vertical scaling, horizontal with sticky sessions]
- [Data volume expectations — e.g., up to 10,000 records]
- [Caching strategy — e.g., none required for MVP]

---

## 5. Reliability *(optional)*

- [Uptime target — e.g., 99% during business hours]
- [Backup strategy — e.g., daily database backups]
- [Recovery time objective — e.g., < 1 hour]

---

## 6. Maintainability

- [Code style — e.g., standard Java conventions, Checkstyle]
- [Test coverage target — e.g., > 80% line coverage on business logic]
- [Documentation — e.g., Javadoc on public APIs]

---

## 7. Other NFRs *(optional)*

- [Internationalisation, browser support, regulatory compliance, etc.]

---

# Part B — Architecture

## 8. Technology Stack

| Layer | Technology | Version | Notes |
|-------|-----------|---------|-------|
| **UI Framework** | Vaadin Flow (Aura theme) | 25.1 | Server-side Java UI |
| **Backend Framework** | Spring Boot | 4.0 | Auto-configuration, embedded Tomcat |
| **Language** | Java | 21 | LTS release |
| **Build Tool** | Maven | [version] | Maven wrapper included |
| **Database** | [e.g., PostgreSQL, H2] | [version] | [Production vs. dev profiles] |
| **Testing** | [e.g., JUnit 5, TestBench] | [version] | [Notes] |

---

## 9. Application Structure

[Describe the package layout and module organisation.]

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

## 10. Data Model *(optional)*

[Entity-relationship overview or key domain objects.]

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| [Entity 1] | [field1, field2] | [Has many Entity2] |
| [Entity 2] | [field1, field2] | [Belongs to Entity1] |

---

## 11. Integration Points *(optional)*

| System | Protocol | Purpose | Auth |
|--------|----------|---------|------|
| [External API] | [REST/GraphQL] | [What it provides] | [API key/OAuth] |

---

## 12. Deployment Architecture

- **Development:** `./mvnw` runs embedded Tomcat on port 8080
- **Production:** [e.g., JAR via `./mvnw clean package`, deployed to cloud/container]
- **Environment config:** [e.g., `application.properties`, environment variables]

---

## 13. Key Architecture Decisions *(optional)*

[Record significant decisions and their rationale. Useful for onboarding and future reference.]

| Decision | Rationale | Alternatives Considered |
|----------|-----------|------------------------|
| [e.g., Server-side UI with Vaadin Flow] | [e.g., Simpler development model, full Java stack] | [e.g., React + REST API] |
| [Decision 2] | [Rationale] | [Alternatives] |
