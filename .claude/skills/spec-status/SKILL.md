---
description: "Check the completeness of all spec files and recommend the next step"
allowed-tools: Read, Glob
user-invocable: true
---

# /spec-status — Spec Completeness Diagnostic

You are a spec-status checker. Your job is to read all spec files in the `spec/` directory, assess their completeness, and recommend the next command to run.

## Placeholder Detection Rule

A `[bracketed text]` token is a **placeholder** unless it is immediately followed by `(` — which makes it a markdown link (e.g., `[text](url)`). When scanning for placeholders, ignore markdown links.

## Steps

1. **Read all spec files.** Use Glob to find every `.md` file under `spec/`. Read each file.

2. **Assess each file.** For every spec file, count the number of placeholders (using the rule above). Classify each file:
   - **Empty** — the file is entirely template; every substantive section contains placeholders
   - **Partial** — some placeholders remain but some sections have real content
   - **Complete** — no placeholders remain (excluding markdown links)

3. **Count use case files.** Look for files matching `spec/use-cases/use-case-[0-9]*.md` (exclude the template). Report how many exist.

4. **Print a status table** in this format:

   ```
   ## Spec Status

   | File | Status | Placeholders |
   |------|--------|-------------|
   | project-context.md | Empty | 23 |
   | architecture.md | Partial | 2 |
   | datamodel/datamodel.md | Empty | 3 |
   | verification.md | Complete | 0 |

   **Use case files:** 0 (excluding template)
   ```

5. **Recommend next step.** Based on the workflow order, suggest what to run next:
   - If `project-context.md` is Empty or Partial → recommend `/spec-interview`
   - Else if `architecture.md` has remaining placeholders → recommend `/spec-architect`
   - Else if no use case files exist (or datamodel is empty) → recommend `/spec-generate`
   - Else → recommend `/spec-validate`

   Print the recommendation clearly:
   ```
   **Next step:** Run `/spec-interview` to fill in the project context.
   ```

## Important

- This command is **read-only**. Never modify any files.
- Always show the full table, even if everything is complete.
- For `spec/README.md` and `spec/use-cases/use-case-template.md`, list them but mark as "Template (skip)" — they are not meant to be filled in.
