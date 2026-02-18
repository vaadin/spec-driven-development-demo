# Architecture

> Technology stack and application structure. `pom.xml` is the source of truth for versions. Do not modify `pom.xml` without asking.

---

## 1. Technology Stack

- Vaadin Flow (Aura theme) — server-side Java UI
- Spring Boot — auto-configuration, embedded Tomcat
- Java
- Maven (wrapper included)
- Database: H2 (in-memory) — data resets on restart; acceptable for POC
- Testing: JUnit 5, Spring Boot Test

---

## 2. Application Structure

```
com.example.specdriven/
  Application.java              — Spring Boot entry point
  show/
    ShowListView.java           — Vaadin @Route: browse available shows
    ShowDetailView.java         — Vaadin @Route: show description + ticket selector
    ShowService.java            — Show business logic (Spring @Service)
    ShowRepository.java         — Show data access (Spring Data)
  purchase/
    CheckoutView.java           — Vaadin @Route: order summary + credit card form
    TicketConfirmationView.java — Vaadin @Route: displays ticket with UUID
    PurchaseService.java        — Purchase/payment logic (Spring @Service)
    PurchaseRepository.java     — Purchase data access (Spring Data)
```

- Application CSS: `src/main/resources/META-INF/resources/styles.css`
