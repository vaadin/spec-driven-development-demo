# UC-[NNN]: [Feature Title]

> This is the use-case template. Copy it for each feature as `use-case-NNN-short-name.md`.
> **Simple format:** fill in the required sections only — delete all sections marked *(optional)*.
> **Advanced format:** keep and fill in the optional sections for complex features.
> Replace all `[bracketed text]` with your content.

---

**As a** [role/actor], **I want to** [capability] **so that** [business value/benefit].

**Status:** [Draft | Review | Approved | Implemented]
**Date:** [YYYY-MM-DD]

---

## Description *(optional)*

[1-3 sentences: what this use case accomplishes and why it matters. Focus on the user outcome.]

---

## Actors *(optional)*

| Actor | Description |
|-------|-------------|
| **[Actor Name]** (primary) | [Role and responsibility in this use case] |
| **[Actor Name]** (secondary) | [Role and responsibility in this use case] |

---

## Preconditions *(optional)*

[Conditions that must be true before this use case can begin. Be specific and testable.]

1. [Precondition 1 — state what must exist or be true]
2. [Precondition 2 — authentication, permissions, data state]

---

## Main Flow

[Describe the happy path from the user's perspective. Write in first person as if you are the user.]

- [I open / I navigate to...]
- [I see...]
- [I do X...]
- [The system responds with Y...]
- [Continue until completion]

### Actor/System Detail *(optional)*

| Step | Actor | System |
|------|-------|--------|
| 1 | [Actor action] | [System response] |
| 2 | [Actor action] | [System response] |
| 3 | [Actor action] | [System response] |
| N | [Final actor action] | [Final system response and confirmation] |

---

## Alternative Flows *(optional)*

### [Alternative Flow Name] (at step [N])

| Step | Actor | System |
|------|-------|--------|
| 1 | [What triggers this alternative] | — |
| 2 | [Actor action] | [System response] |
| 3 | [Resolution] | [Return to step N or end] |

---

## Exception Flows *(optional)*

### [Exception Name] (at step [N] / any step)

| Step | Actor | System |
|------|-------|--------|
| 1 | [Actor action or event] | [Error or exception occurs] |
| 2 | — | [System error handling] |
| 3 | [Actor recovery action] | [System recovery response] |

---

## Postconditions *(optional)*

[What must be true when the use case completes successfully.]

1. [Data created, updated, or deleted]
2. [State changes, notifications, audit trails]

---

## Business Rules

[Constraints, validations, and policies that apply to this feature.]

| ID | Rule |
|----|------|
| BR-01 | [Business rule — e.g., "All fields are mandatory"] |
| BR-02 | [Business rule — e.g., "Sold-out items are visible but cannot be selected"] |
| BR-03 | [Business rule — e.g., "Maximum 6 items per transaction"] |

---

## Acceptance Criteria

- [ ] [Criterion 1 — testable statement of expected behaviour]
- [ ] [Criterion 2 — edge case or validation check]
- [ ] [Criterion 3 — non-functional requirement, e.g., "Page loads in under 2 seconds"]

---

## UI / Routes *(optional)*

[Describe layout or interaction requirements. Reference a mockup if available.]

- [Layout or component description]
- [Device-specific consideration]

| Route | Access | Notes |
|-------|--------|-------|
| `[/path]` | [public/authenticated] | [Technology, e.g., Vaadin @Route] |

---

## Frequency *(optional)*

[How often is this use case performed? By how many users? Helps prioritise performance and UX effort.]

- [e.g., "2-3 times per day per user" or "Monthly by admin users"]

---

## Open Issues *(optional)*

| ID | Issue / Question | Status |
|----|------------------|--------|
| OI-01 | [Unresolved decision or question] | [Open / Resolved] |

---

## Document History *(optional)*

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| [X.Y] | [YYYY-MM-DD] | [Name] | [Summary of changes] |

---

**Related:** [Link to related use cases, design docs, or spec files]
