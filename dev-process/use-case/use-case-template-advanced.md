# Use Case: [Use Case Title]

**ID:** UC-[NNN]  
**Version:** [X.Y]  
**Date:** [YYYY-MM-DD]  
**Status:** [Draft | Review | Approved | Implemented]

---

## 1. Brief Description

[1-3 sentences: what this use case accomplishes and why it matters. Focus on the user outcome.]

---

## 2. Actors

| Actor | Description |
|-------|-----------|
| **[Actor Name]** (primary) | [Role and responsibility in this use case] |
| **[Actor Name]** (secondary) | [Role and responsibility in this use case] |

---

## 3. Preconditions

[Conditions that must be true before this use case can begin. Be specific and testable.]

1. [Precondition 1 - state what must exist or be true]
2. [Precondition 2 - authentication, permissions, data state]
3. [Precondition 3 - system state, availability]

---

## 4. Postconditions

[What must be true when the use case completes successfully.]

1. [Data created, updated, or deleted]
2. [State changes]
3. [Notifications, audit trails]

---

## 5. Basic Flow

| Step | Actor | System |
|------|-------|--------|
| 1 | [Actor action] | [System response] |
| 2 | [Actor action] | [System response] |
| 3 | [Actor action] | [System response] |
| ... | ... | ... |
| N | [Final actor action] | [Final system response and confirmation] |

---

## 6. Alternative Flows

### 6a. [Alternative Flow Name] (at step N)

| Step | Actor | System |
|------|-------|--------|
| 6a.1 | [What triggers this alternative] | — |
| 6a.2 | [Actor action] | [System response] |
| 6a.3 | [Resolution] | [System state] |
| 6a.4 | Return to step [N] of basic flow | — |

### 6b. [Another Alternative Flow] (at step M)

| Step | Actor | System |
|------|-------|--------|
| 6b.1 | [Condition or trigger] | [System detection/response] |
| 6b.2 | [Actor action] | [System response] |
| 6b.3 | [Resolution] | [Return point or termination] |

---

## 7. Exception Flows

### 7a. [Exception Name] (at step/any step)

| Step | Actor | System |
|------|-------|--------|
| 7a.1 | [Actor action or event] | [Error or exception occurs] |
| 7a.2 | — | [System error handling] |
| 7a.3 | [Actor recovery action] | [System recovery response] |
| 7a.4 | [Resolution: retry, abort, alternative path] | [Final state] |

---

## 8. Business Rules

| ID | Rule |
|----|------|
| BR-01 | [Business rule statement - constraints, validations, policies] |
| BR-02 | [Business rule statement - timing, permissions, thresholds] |
| BR-03 | [Business rule statement - data integrity, calculations] |

---

## 9. User Interface Requirements *(optional)*

[UI/UX considerations specific to this use case. Delete this section if not needed.]

- [Interaction or layout requirement]
- [Accessibility or device-specific requirement]

---

## 10. Frequency

[How often is this use case performed? By how many users? This helps prioritise performance and UX effort.]

- [e.g., "2-3 times per day per apparatus" or "Monthly by admin users"]

---

## 11. Assumptions and Open Issues

### Assumptions
1. [Assumption about users, environment, or data]
2. [Assumption about system capabilities or integrations]

### Open Issues
| ID | Issue/Question | Status |
|----|----------------|--------|
| OI-01 | [Unresolved decision or question] | [Open/Resolved] |

### Dependencies
- **Related Use Cases:** [UC-AAA, UC-BBB]

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| [X.Y] | [YYYY-MM-DD] | [Name] | [Summary of changes] |
