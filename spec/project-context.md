# Project Context

> High-level context for the project: the problem being solved, who it's for, what's in scope, and what constraints apply.

---

## 1. Problem Statement

Purchasing tickets to movies or shows takes too much effort
Users don't want to create accounts
Customers navigate away before they get through long wizards for seat selections, extra purhases etc. 

---

## 2. Vision

A fast or even "Quick" Ticket purchasing system that can be used on mobile as well as desktop. The main idea is to reduce the amount of clicks the end user has to do to buy a ticket. In simple terms we want 1. A user friendly list of avaialble movies/shows, when they run and a cover picture. 2. Selecting one navigates to a description page with the cover picture, a bit more description and a ticket selector for how many tickets tehy want 3. A summary view of total cost and a Credit Card form that has only the needed fields for a mainstream CC. 4. A view showing the ticket with a UUID or similar identifier that the user can show later at the venue. 

This is a first proof of concept and we focus only on the end-user purchasing side. The administation of shows, purchases etc. is out of scope. 

---

## 3. Users

| Role | Description | Access Scope |
|------|-------------|--------------|
| Customer | person that buys one or more tickets | the ticket purchasing wizard/flow |

---

## 4. Scope

> High-level capabilities, not detailed features (those go in use cases).

### In Scope
Customer ticket purchase workflow

### Out of Scope
Administrative/maintenance side of the app

---

## 5. Constraints

- Proof of concept — no specific platform, policy, or integration constraints beyond the existing tech stack
- Payment processing is simulated (no real payment gateway integration)

> For technology stack and application structure details, see [`architecture.md`](architecture.md).

---

## 6. Assumptions

1. H2 in-memory database — data resets on restart; acceptable for POC
2. Payment is simulated — any credit card input is accepted without real validation
3. Users have modern desktop or mobile browsers
4. Deployed on a self-hosted server capable of running Java

---

## 7. Risks

- Data loss on restart — H2 in-memory DB resets when the app stops; acceptable for POC, migrate to persistent DB for production
- No real payment validation — simulated payments may not reflect real-world UX friction; plan a payment gateway integration phase before go-live
- Scope creep into admin features — keep admin functionality strictly out of scope; track it separately for a future phase

---

## 8. Related Documents

- [Spec README](README.md) — process overview and workflow
- [Architecture](architecture.md) — technology stack and application structure
- [Use Case Template](use-cases/use-case-template.md) — template for feature specifications
- [Verification](verification.md) — visual verification checklists
