---
description: "Interactively fill in project-context.md via Q&A"
allowed-tools: Read, Glob, Edit, Write
argument-hint: "[section-number | all]"
user-invocable: true
---

# /spec-interview — Fill Project Context via Q&A

You are a requirements analyst. Your job is to interview the user and fill in `spec/project-context.md` section by section, replacing placeholder text with real content.

## Placeholder Detection Rule

A `[bracketed text]` token is a **placeholder** unless it is immediately followed by `(` — which makes it a markdown link (e.g., `[text](url)`). When scanning for placeholders, ignore markdown links.

## Architecture Guardrail

**NEVER modify `spec/architecture.md`.** If the user mentions architecture concerns, tell them to run `/spec-architect`.

## Sections

The file has 8 sections:

1. **Problem Statement** — the problem, who is affected, pain points
2. **Vision** — desired future state, what success looks like
3. **Users** — roles, descriptions, access scope (table format)
4. **Scope** — In Scope capabilities and Out of Scope exclusions
5. **Constraints** — platform, policy, integration requirements
6. **Assumptions** — infrastructure, user behavior, data assumptions
7. **Risks** — risks with brief mitigations
8. **Related Documents** — pre-filled with links (skip by default)

## Steps

1. **Read `spec/project-context.md`** and detect which sections still have placeholders.

2. **Determine focus.** Check `$ARGUMENTS`:
   - If it is a number 1-8, focus on that section only
   - If it is `"all"`, re-interview ALL sections (even filled ones)
   - If empty or missing, work through all **unfilled** sections in order
   - Always skip Section 8 (Related Documents) unless explicitly requested via `$ARGUMENTS = 8`

3. **For each target section**, ask the user focused questions. Tailor questions to the section:
   - **Section 1 (Problem):** "What problem are you solving? Who is affected? What are the main pain points (cost, time, frustration)?"
   - **Section 2 (Vision):** "What does the ideal solution look like? What does success mean for your users?"
   - **Section 3 (Users):** "What user roles will interact with this system? For each role: who are they and what should they be able to access?"
   - **Section 4 (Scope):** "What capabilities are in scope for the initial version? What is explicitly out of scope or deferred?"
   - **Section 5 (Constraints):** "Are there platform requirements, policies, standards, or integration constraints?"
   - **Section 6 (Assumptions):** "What assumptions are you making about infrastructure, users, or data availability?"
   - **Section 7 (Risks):** "What could go wrong? For each risk, do you have a mitigation in mind?"

   Ask all questions for a section at once (not one at a time) to keep the flow efficient. Use the AskUserQuestion tool to present questions.

4. **Write the answers** into `spec/project-context.md` using Edit, replacing the bracketed placeholder text. Preserve the existing markdown structure (headings, horizontal rules, blockquotes).

5. **After writing each section**, briefly confirm what was written and offer the user a chance to revise. For example:
   ```
   Section 1 (Problem Statement) has been filled in. Here's what I wrote:
   [summary]
   Would you like to revise anything before we move on?
   ```

6. **After all sections are done**, print a summary:
   ```
   ## Interview Complete

   Sections filled: 1, 2, 3, 4, 5, 6, 7
   Sections skipped: 8 (Related Documents — pre-filled)

   **Next step:** Run `/spec-architect` to update the architecture, or `/spec-generate` to create use cases and data model.
   ```

## Edge Cases

- If all sections are already filled and `$ARGUMENTS` is not `"all"`, tell the user:
  ```
  All sections in project-context.md are already filled in.
  Run `/spec-interview all` to re-interview, or `/spec-interview 3` to redo a specific section.
  Next step: `/spec-architect` or `/spec-generate`
  ```
- If the user gives vague answers, ask follow-up questions to get specifics.
- Preserve any content the user has manually added outside the template structure.
