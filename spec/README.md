# Specification Folder

> This folder contains all project specifications for a spec-driven AI development process.
> Specs are written first, then used as input for implementation and verification.
> Keep specs up to date as the project evolves — they are the single source of truth.

---

**Project:** [Project Name]
**Version:** [X.Y]
**Date:** [YYYY-MM-DD]
**Status:** [Draft | Approved | Active]

---

## File Overview

| File | Purpose | When to Read |
|------|---------|--------------|
| `context/project-context.md` | Vision, problem, stakeholders, scope, risks | First — before anything else |
| `architecture/nfr-and-architecture.md` | Non-functional requirements and architecture outline | After project context is filled in |
| `use-cases/use-case-template.md` | Template for individual feature specs | Copy per feature, after architecture is outlined |
| `qa/qa-and-verification.md` | Test strategy, visual verification checklists | During and after implementation |

---

## Workflow

1. **Define context** — Fill in `context/project-context.md` with problem, vision, scope, and constraints.
2. **Outline architecture** — Fill in `architecture/nfr-and-architecture.md` with NFRs and tech decisions.
3. **Specify features** — Copy `use-cases/use-case-template.md` once per feature (see naming below).
4. **Implement** — Build each use case, referencing its spec for acceptance criteria.
5. **Verify** — Follow `qa/qa-and-verification.md` checklists for each implemented use case.

---

## Naming Conventions

Copy the use-case template for each feature using this pattern:

```
use-cases/use-case-NNN-short-name.md
```

Examples:
- `use-case-001-login.md`
- `use-case-002-seat-selection.md`
- `use-case-003-admin-dashboard.md`

Use zero-padded three-digit numbers. The short name should be lowercase, hyphen-separated.

---

## Status Lifecycle

Each document progresses through these statuses:

**Draft** — Initial content, still being written
**Review** — Content complete, awaiting stakeholder feedback
**Approved** — Reviewed and accepted, ready for implementation
**Implemented** — Feature built and verified against the spec

---

## For AI Agents

When consuming these specs as an AI coding agent:

1. **Read order:** `README.md` → `context/project-context.md` → `architecture/nfr-and-architecture.md` → relevant `use-case-NNN-*.md` → `qa/qa-and-verification.md`
2. **Project context** gives you the "why" and constraints. Read it before writing any code.
3. **Architecture** tells you the tech stack, package layout, and NFRs to respect.
4. **Use cases** are your implementation specs — follow the main flow, business rules, and acceptance criteria exactly.
5. **QA** defines how to verify your work. Run through the checklist after implementing each use case.
6. Sections marked `*(optional)*` may be absent in filled-in specs — do not treat missing optional sections as errors.
7. `[Bracketed text]` indicates placeholders that should be replaced with real content.
