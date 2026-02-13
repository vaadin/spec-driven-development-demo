# Project Context

> This document captures the high-level context for a project: the problem being solved, who it's for, what's in scope, and what constraints apply. It is the single source of truth that all other project documents build on.
>
> **How to use this template:**
> 1. Fill in each section in order — earlier sections inform later ones.
> 2. Replace all `[bracketed text]` with your content.
> 3. Remove any optional sections that don't apply.
> 4. Keep it concise — details belong in use cases and architecture docs.
> 5. Review with stakeholders and update the status to **Approved** before starting development.

---

**Project:** [Project Name]  
**Version:** [X.Y]  
**Date:** [YYYY-MM-DD]  
**Status:** [Draft | Approved | Active]

---

## 1. Problem Statement

[Describe the problem in concrete terms. Who is affected and what happens today? Focus on impact, not solutions.]

- [Pain point 1 — what goes wrong and what it costs]
- [Pain point 2]
- [Pain point 3]

---

## 2. Vision

[One to two paragraphs describing the desired future state. What does success look like for the users? Focus on outcomes, not technology or implementation.]

---

## 3. Stakeholders and Users

[List the people and groups who have a stake in this project — both those who sponsor/oversee it and those who use the system daily.]

| Stakeholder | Role | Key Concerns |
|-------------|------|--------------|
| **[Name/Group]** | [Primary/Secondary] | [What they care about] |
| **[Name/Group]** | [Primary/Secondary] | [What they care about] |

### User Roles

| Role | Description | Access Scope |
|------|-------------|--------------|
| **[Role 1]** | [Who they are] | [What they can access] |
| **[Role 2]** | [Who they are] | [What they can access] |

### Device Usage *(optional)*

| Device | Primary Users | Typical Use |
|--------|---------------|-------------|
| **Mobile** | [Users] | [Use cases] |
| **Desktop** | [Users] | [Use cases] |

---

## 4. Scope

### In Scope
- [Feature/capability 1]
- [Feature/capability 2]
- [User group or workflow 3]

### Out of Scope
- [Excluded feature 1]
- [Excluded integration 2]
- [Deferred capability 3]

---

## 5. Constraints

### Technical
- **Technology stack:** [e.g., Java 21, Spring Boot, PostgreSQL — list mandated technologies]
- [Platform, integration, or performance requirement]
- [Security or compliance requirement]

### Operational
- [How/where system is used]
- [User skill levels or training needs]

### Organisational
- [Budget, timeline, or resource limits]
- [Policies or standards]

---

## 6. Success Criteria

[Define measurable outcomes that indicate the project has achieved its goals. Each criterion should have a clear target and a way to verify it.]

1. **[Metric 1]:** [Measurable target, e.g., "Average task completion time under 5 minutes"]
2. **[Metric 2]:** [Measurable target]
3. **[Metric 3]:** [Measurable target]

---

## 7. Assumptions

[List things you are taking for granted. If any assumption turns out to be wrong, it may significantly affect the project.]

1. [Infrastructure or environment assumption]
2. [User behaviour or capability assumption]
3. [Data or dependency assumption]

---

## 8. Risks

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| [Risk 1] | [H/M/L] | [H/M/L] | [How to prevent/reduce] |
| [Risk 2] | [H/M/L] | [H/M/L] | [How to prevent/reduce] |

---

## 9. Related Documents *(optional)*

- [Use Cases]()
- [Architecture]()
