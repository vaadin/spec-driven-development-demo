# QA & Verification

> This document defines the test strategy and verification process for the project. It covers test levels, naming conventions, and a per-use-case verification checklist that should be copied and completed for each implemented feature.
>
> **How to use this template:**
> 1. Fill in the strategy sections (1-4) once for the project.
> 2. Copy the per-use-case checklist (section 5) for each implemented use case.
> 3. Replace all `[bracketed text]` with your content.
> 4. Remove any optional sections that don't apply.

---

**Project:** [Project Name]
**Version:** [X.Y]
**Date:** [YYYY-MM-DD]
**Status:** [Draft | Approved | Active]

---

## 1. Test Strategy Overview

### Test Levels

| Level | Scope | Tools | Coverage Target |
|-------|-------|-------|-----------------|
| **Unit** | Individual classes and methods | [e.g., JUnit 5, Mockito] | [e.g., > 80% on business logic] |
| **UI Unit** | Individual Vaadin components in isolation | [e.g., Vaadin TestBench, Karibu Testing] | [e.g., All views have at least one test] |
| **Integration** | Component interactions, Spring context | [e.g., Spring Boot Test, @WebMvcTest] | [e.g., All service-to-repository paths] |
| **End-to-End** | Full user workflows through the browser | [e.g., Playwright MCP, TestBench] | [e.g., All main flows from use cases] |

### Naming Conventions

- Test classes: `[ClassName]Test.java` (unit), `[ClassName]IT.java` (integration)
- Test methods: `should_[expectedResult]_when_[condition]`
- Test data: use descriptive variable names, not magic numbers

### When to Write Tests

- **Before implementation:** write acceptance-criteria tests (red-green-refactor)
- **During implementation:** add unit tests for business logic
- **After implementation:** run visual verification and accessibility checks

---

## 2. Test Environment

| Environment | Purpose | Configuration |
|-------------|---------|---------------|
| **Local dev** | Developer testing | [e.g., `./mvnw test`, H2 in-memory DB] |
| **CI** | Automated checks on push/PR | [e.g., GitHub Actions, same Maven goals] |
| **Staging** | Pre-production validation | [e.g., Production-like config, test data] |

---

## 3. Visual Verification Process

Use the Playwright MCP server to perform visual verification after implementing each use case.

### Steps

1. **Start the application** — run `./mvnw` and wait for startup
2. **Navigate to the route** — open the page defined in the use case's UI/Routes section
3. **Walk through the main flow** — perform each step from the use case's Main Flow
4. **Take screenshots** — capture the page state at key interaction points
5. **Check visual appearance:**
   - Layout matches expectations (spacing, alignment, sizing)
   - Typography is readable and consistent
   - Interactive elements are clearly identifiable
   - Responsive behaviour works at common breakpoints (mobile, tablet, desktop)
6. **Walk through alternative/exception flows** — verify error states and edge cases display correctly
7. **Record results** — note any visual issues in the per-use-case checklist below

---

## 4. Accessibility Spot-Check

Run through this checklist for each implemented view:

- [ ] **Keyboard navigation:** all interactive elements are reachable and operable via keyboard
- [ ] **Focus indicators:** focused elements have a visible outline or highlight
- [ ] **Colour contrast:** text meets minimum contrast ratio (4.5:1 normal, 3:1 large)
- [ ] **Labels:** all form fields have associated labels (visible or via `aria-label`)
- [ ] **Headings:** page has a logical heading hierarchy (h1 → h2 → h3)
- [ ] **Error messages:** validation errors are announced to screen readers
- [ ] **Images:** decorative images have empty alt, informative images have descriptive alt

---

## 5. Per-Use-Case Verification Checklist

> Copy this section for each use case. Name it: **UC-[NNN]: [Feature Title]**

### UC-[NNN]: [Feature Title]

**Use case spec:** [`use-case-NNN-name.md`](../use-cases/use-case-NNN-name.md)
**Verified by:** [Name]
**Date:** [YYYY-MM-DD]

#### Functional Verification

- [ ] Main flow works end-to-end as described in the spec
- [ ] All business rules are enforced (list BR-IDs: [BR-01, BR-02, ...])
- [ ] All acceptance criteria pass (list AC items)
- [ ] Alternative flows work correctly *(if applicable)*
- [ ] Exception flows display appropriate error handling *(if applicable)*

#### Visual Verification

- [ ] Page layout matches expectations
- [ ] Interactive elements respond correctly (hover, focus, click)
- [ ] Loading states and transitions are smooth
- [ ] Responsive at mobile, tablet, and desktop widths

#### Code Quality

- [ ] Unit tests pass (`./mvnw test`)
- [ ] No compiler warnings or lint errors
- [ ] No hardcoded values that should be configurable
- [ ] Code follows project conventions (package structure, naming)

#### Accessibility

- [ ] Keyboard-navigable
- [ ] Screen reader-friendly (labels, ARIA attributes)
- [ ] Sufficient colour contrast

#### Result

- **Status:** [Pass / Fail / Partial]
- **Notes:** [Any issues found, workarounds, or follow-up items]

---

## 6. Defect Reporting *(optional)*

| ID | Use Case | Description | Severity | Status |
|----|----------|-------------|----------|--------|
| D-001 | UC-[NNN] | [What's wrong] | [Critical/Major/Minor] | [Open/Fixed/Won't Fix] |
