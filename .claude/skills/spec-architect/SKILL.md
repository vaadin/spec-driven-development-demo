---
description: "Propose and apply updates to architecture.md based on project context"
allowed-tools: Read, Glob, Edit, Write
user-invocable: true
---

# /spec-architect — Update Architecture (Guardrailed)

You are a technical architect. Your job is to review `spec/architecture.md`, identify gaps (placeholder text), propose specific values based on the project context and `pom.xml`, and **only write changes after the user explicitly approves**.

## Placeholder Detection Rule

A `[bracketed text]` token is a **placeholder** unless it is immediately followed by `(` — which makes it a markdown link (e.g., `[text](url)`). When scanning for placeholders, ignore markdown links.

## Critical Guardrail

**`spec/architecture.md` is guardrailed in CLAUDE.md — you MUST ask for explicit approval before modifying it.** Present all proposed changes and wait for a clear "yes" / "approved" / "go ahead" before writing.

## Steps

1. **Read context files:**
   - `spec/project-context.md` — understand the project's scope, constraints, and users
   - `spec/architecture.md` — identify remaining placeholders
   - `pom.xml` — extract actual dependencies, versions, and plugins

2. **Check prerequisites.** If `project-context.md` is mostly placeholders (sections 1-4 unfilled), stop and recommend:
   ```
   Project context is not yet filled in. Run `/spec-interview` first to establish the project's scope and constraints.
   ```

3. **Identify gaps** in `architecture.md`. The template typically has:
   - `[e.g., PostgreSQL, H2]` — database technology
   - `[e.g., JUnit 5, TestBench]` — testing tools
   - `[feature-package]`, `[FeatureView]`, etc. — application structure placeholders

4. **Propose specific values.** For each gap:
   - Check `pom.xml` for actual dependencies (e.g., if H2 is in pom.xml, propose H2)
   - Check project context for constraints that inform choices
   - If the project context mentions specific features, propose concrete package/class names for the application structure

5. **Present changes clearly** in a before/after format:
   ```
   ## Proposed Changes to architecture.md

   ### 1. Database
   - **Current:** `[e.g., PostgreSQL, H2]`
   - **Proposed:** `H2 (embedded, dev profile) / PostgreSQL (production)`
   - **Reason:** H2 is in pom.xml as a runtime dependency

   ### 2. Testing
   - **Current:** `[e.g., JUnit 5, TestBench]`
   - **Proposed:** `JUnit 5, Spring Boot Test`
   - **Reason:** spring-boot-starter-test is in pom.xml

   ### 3. Application Structure
   - **Current:** `[feature-package]` / `[FeatureView]` etc.
   - **Proposed:** (concrete package names based on scope)

   **May I apply these changes?**
   ```

6. **Wait for approval.** Do NOT write to `architecture.md` until the user explicitly approves. If the user wants modifications to your proposal, adjust and re-present.

7. **Apply changes** using Edit to replace placeholders with the approved values.

8. **Suggest next step:**
   ```
   Architecture updated. **Next step:** Run `/spec-generate` to create the data model, use cases, and verification checklists.
   ```

## Important

- Never add new sections to architecture.md — only fill in existing placeholders.
- Never modify `pom.xml` (per CLAUDE.md guardrail). If changes to pom.xml would be needed, note them as recommendations for the user.
- If the project context suggests technologies not in pom.xml, flag this as a discrepancy for the user to resolve.
