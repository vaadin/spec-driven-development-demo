---
description: "Cross-check all spec files for consistency errors, warnings, and gaps"
allowed-tools: Read, Glob, Edit, Write
argument-hint: "[--fix]"
user-invocable: true
---

# /spec-validate — Spec Consistency Checker

You are a spec validator. Your job is to cross-check all specification files for consistency, completeness, and structural correctness, then report findings as errors, warnings, or info.

## Placeholder Detection Rule

A `[bracketed text]` token is a **placeholder** unless it is immediately followed by `(` — which makes it a markdown link (e.g., `[text](url)`). When scanning for placeholders, ignore markdown links.

## Architecture Guardrail

**NEVER modify `spec/architecture.md`.** Report issues with architecture as warnings for the user to fix manually or via `/spec-architect`.

## Steps

1. **Read all spec files:**
   - `spec/project-context.md`
   - `spec/architecture.md`
   - `spec/datamodel/datamodel.md`
   - `spec/verification.md`
   - All `spec/use-cases/use-case-[0-9]*.md` files (exclude the template)

2. **Run all consistency checks** (details below).

3. **Classify findings:**
   - **Error** — must be fixed; spec is inconsistent or broken
   - **Warning** — should be fixed; likely an oversight
   - **Info** — informational; may be intentional

4. **Print the report** in this format:
   ```
   ## Spec Validation Report

   ### Errors (must fix)
   - [E01] Entity "Order" referenced in use-case-002 but not defined in datamodel.md
   - [E02] Role "Manager" in use-case-003 not listed in project-context.md Section 3

   ### Warnings (should fix)
   - [W01] In Scope item "Reporting dashboard" has no corresponding use case
   - [W02] use-case-001 has Status: Implemented but no verification result in verification.md

   ### Info
   - [I01] 3 placeholders remain in architecture.md (run /spec-architect)
   - [I02] 5 use case files found, 5 verification checklists present

   **Summary:** 2 errors, 2 warnings, 2 info items
   ```

5. **If `$ARGUMENTS` contains `--fix`**, auto-fix clear structural gaps (see Fix Mode below).

6. **Recommend next step** based on findings:
   - If errors exist → list which commands to run to fix them
   - If only warnings → suggest manual review or `--fix`
   - If clean → "All specs are consistent. Ready for implementation."

## Consistency Checks

### Check 1: Data Model vs Use Cases
- Every entity referenced in any use case file (look for capitalized nouns in Main Flow, Business Rules, and Acceptance Criteria that match entity names) should exist in `datamodel/datamodel.md`.
- Every entity in the data model should be referenced by at least one use case (no orphan entities).
- **Error** for missing entities; **Warning** for orphan entities.

### Check 2: Role Consistency
- The "As a [role]" in each use case's user story must match a role defined in `project-context.md` Section 3 (Users table).
- **Error** for undefined roles.

### Check 3: Scope Coverage
- Each In Scope item in `project-context.md` Section 4 should have at least one corresponding use case.
- No use case should implement an Out of Scope item.
- **Warning** for uncovered In Scope items; **Error** for Out of Scope implementations.

### Check 4: Verification Coverage
- Each use case file should have a corresponding checklist in `verification.md`.
- **Warning** for missing checklists.

### Check 5: Placeholder Detection
- Scan ALL spec files for remaining `[bracketed text]` placeholders (using the detection rule).
- Report the count per file.
- **Info** for files with placeholders; **Error** if a use case with Status: Approved or Implemented still has placeholders.

### Check 6: Status Consistency
- Use cases with **Status: Implemented** should have a verification checklist with a result (Pass/Fail/Partial, not a placeholder).
- Use cases with **Status: Approved** should have complete acceptance criteria (no placeholder acceptance criteria).
- **Warning** for status inconsistencies.

### Check 7: Architecture Alignment
- Routes defined in use case UI/Routes sections should be plausible given the architecture (e.g., Vaadin @Route paths).
- If use cases reference technologies not in the architecture, flag it.
- **Warning** for alignment issues.

## Fix Mode (`--fix`)

When `$ARGUMENTS` contains `--fix`, automatically fix **only** clear structural gaps:

1. **Missing verification checklists** — add a checklist to `verification.md` for each use case that lacks one, following the existing template. Set Status as `[Pass / Fail / Partial]` and Verified by as `[Name/Agent]`.

2. **Missing data model entries** — add entities referenced in use cases but missing from `datamodel/datamodel.md`, with fields set to `[NEEDS REVIEW]` and relationships set to `[NEEDS REVIEW]`.

3. **Never auto-fix:**
   - Role mismatches (ambiguous — could be a typo in either file)
   - Scope coverage gaps (requires human decision)
   - Architecture alignment issues
   - Status inconsistencies
   - Anything in `architecture.md` (guardrailed)

After auto-fixing, re-run all checks and report the updated state. Clearly list what was auto-fixed:
```
## Auto-fixes Applied
- Added verification checklist for UC-002, UC-003
- Added entity "Order" to datamodel.md with [NEEDS REVIEW] fields

## Remaining Issues
(re-run report here)
```
