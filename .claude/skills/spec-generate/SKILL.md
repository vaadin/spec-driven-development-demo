---
description: "Generate data model, use cases, and verification checklists from project context"
allowed-tools: Read, Glob, Write, Edit, Bash
argument-hint: "[datamodel | use-cases | verification | all]"
user-invocable: true
---

# /spec-generate — Generate Downstream Specs

You are a spec writer. Your job is to generate downstream specification documents (data model, use cases, verification checklists) based on the filled-in project context and architecture.

## Placeholder Detection Rule

A `[bracketed text]` token is a **placeholder** unless it is immediately followed by `(` — which makes it a markdown link (e.g., `[text](url)`). When scanning for placeholders, ignore markdown links.

## Architecture Guardrail

**NEVER modify `spec/architecture.md`.** If changes to the architecture are needed, tell the user to run `/spec-architect`.

## Steps

1. **Read prerequisite files:**
   - `spec/project-context.md` — check that sections 1-4 (Problem, Vision, Users, Scope) are filled in (no placeholders). If they are not, stop immediately:
     ```
     Project context sections 1-4 are not yet complete. Run `/spec-interview` first.
     ```
   - `spec/architecture.md` — for technology stack and structure
   - `spec/datamodel/datamodel.md` — current state
   - `spec/verification.md` — current state
   - `spec/use-cases/use-case-template.md` — the template to follow
   - Any existing `spec/use-cases/use-case-[0-9]*.md` files

2. **Determine scope** from `$ARGUMENTS`:
   - `datamodel` — only generate/update the data model
   - `use-cases` — only generate use case files
   - `verification` — only generate verification checklists
   - `all` or empty/missing — generate all three in order: datamodel, use-cases, verification

3. **Get today's date** by running: `date +%Y-%m-%d` (use Bash). Use this date in all generated files.

### Data Model Generation

4. **Analyze scope and use cases** to identify entities. Look at:
   - In Scope capabilities from Section 4
   - User roles from Section 3
   - Any existing use case files for referenced entities

5. **Write `spec/datamodel/datamodel.md`** with:
   - One row per entity in the table
   - Key fields for each entity (informed by scope and use cases)
   - Relationships between entities
   - Keep the existing header and description blockquote

### Use Case Generation

6. **Identify capabilities** from `spec/project-context.md` Section 4 (In Scope). Each capability should map to one or more use cases.

7. **Find the next number.** Check existing `spec/use-cases/use-case-*.md` files (excluding template). Find the highest NNN and start from max + 1. If none exist, start at 001.

8. **For each capability**, create a use case file at `spec/use-cases/use-case-NNN-short-name.md` following the template exactly:
   - **User story:** Use a role from Section 3 (Users) as the actor
   - **Status:** Draft
   - **Date:** today's date
   - **Main Flow:** Write concrete steps from the user's perspective
   - **Business Rules:** At least 1-2 rules per use case
   - **Acceptance Criteria:** At least 3 testable criteria
   - **UI / Routes:** Propose a route path and access level

9. **Never overwrite existing use case files.** If a file already exists for a capability, skip it and note:
   ```
   Skipped: use-case-001-feature-name.md (already exists)
   ```

### Verification Generation

10. **Update `spec/verification.md`** by adding a per-use-case checklist for each use case file (new and existing) that doesn't already have a checklist in the verification file.

11. **Follow the checklist template** from the existing verification.md Section 2. For each use case:
    - Reference the use case file path
    - List the BR-IDs from that use case
    - Set Verified by and Date as placeholders
    - Set Status as `[Pass / Fail / Partial]`

12. **Preserve the existing verification process section** (Section 1) — only add/update Section 2 checklists.

### Summary

13. **Print a summary** of what was generated:
    ```
    ## Generation Summary

    ### Data Model
    - Entities defined: [count] ([list])

    ### Use Cases
    - Created: [list of files]
    - Skipped: [list of existing files]

    ### Verification
    - Checklists added: [count]

    **Next step:** Run `/spec-validate` to check consistency across all specs.
    ```

## Important

- Follow the use case template structure exactly — do not add or remove sections.
- Use short, descriptive kebab-case names for use case files (e.g., `use-case-001-manage-inventory.md`).
- Number use cases with zero-padded three-digit numbers (001, 002, ...).
- Each In Scope item should map to at least one use case. Multiple related In Scope items can be combined into one use case if they form a single workflow.
