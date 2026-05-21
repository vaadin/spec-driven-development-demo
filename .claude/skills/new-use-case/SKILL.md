---
name: new-use-case
description: Define a new use case by interviewing the user, then write a filled-in `spec/use-cases/use-case-NNN-short-name.md`. Use when the user wants to add, draft, or specify a new feature/use case.
---

# New Use Case

Interview the user to capture a new use case, then write it to `spec/use-cases/use-case-NNN-short-name.md` using `spec/use-cases/use-case-template.md` as the structure.

The goal is a complete, implementation-ready spec. A use case that lacks a main flow, business rules, acceptance criteria, or routes is not done — keep interviewing until every section is filled in.

## Before You Ask Anything

1. Read `spec/project-context.md` and `spec/architecture.md` so questions stay consistent with the project's vision, users, and stack.
2. Read `spec/datamodel/datamodel.md` if it exists — refer to actual entities when discussing data.
3. List `spec/use-cases/` to:
   - See existing use cases (don't duplicate them; suggest references where flows overlap).
   - Determine the next free `NNN` number (zero-padded, e.g. `004`).
4. Read `spec/use-cases/use-case-template.md` — it is the authoritative shape of the output.

If any of these files are missing, tell the user and stop; the project isn't ready for a use case yet.

## Interview

Drive a focused conversation. Ask one topic at a time, in the order below. Keep questions short. Use `AskUserQuestion` when there is a clear small set of choices (role, access level, UI tech, status). Use plain chat for open-ended answers (main flow, business rules). Never ask the user to "fill in the template" themselves — you are doing that for them.

After each answer, restate your understanding in one sentence before moving on, so misunderstandings surface early.

### 1. Capability and value (one user-story sentence)
- What capability is this use case about? Who is the actor? What value do they get?
- Produce the `As a … I want to … so that …` sentence and confirm it.

### 2. Short name (derive silently)
- Derive a kebab-case short name (2–4 words) from the capability sentence. Use it as the filename suffix. Do not ask the user — just pick a sensible one (e.g. "browse movies" → `browse-movies`, "user buys ticket" → `buy-ticket`). It will show up in the final summary; the user can correct it there if they care.

### 3. Main flow (happy path)
- Ask the user to walk through what happens, step by step, from their point of view.
- Write the steps in first person (`I open …`, `I see …`, `I do …`, `The system responds with …`).
- If the user is vague, ask targeted follow-ups: entry point, what they see, what they click/type, what the system does, when the flow ends.

### 4. Business rules
- Ask what rules constrain the flow: required fields, limits, visibility/access, validation, edge cases, time/ordering rules.
- Encourage at least two or three. Each rule should be testable. Reference the data model and existing use cases when relevant.

### 5. Acceptance criteria
- Derive testable statements from the main flow + business rules. Read them back and ask the user to confirm or extend.
- Each criterion must be something a test can verify ("user sees X", "system rejects Y").

### 6. UI / routes
- Ask whether this is a public (React/Hilla) or admin (Vaadin Flow) view — this drives routing, access annotations, and test style. Use `AskUserQuestion` for this.
- Capture the route path(s), access level (public / authenticated / ADMIN), and any layout requirements (component types, key interactions, responsive needs).
- If the user has a mockup or image, ask them to point at it and reference it in the file.

### 7. Tests (placeholder)
- Suggest a test class name based on the short name (e.g. `BrowseMoviesTest`, `BuyTickets.test.tsx`) and a bullet list of what each test will cover. The actual tests are written later by `/use-case-tests`; here we are just declaring intent.

## Draft and Confirm

Once all sections are gathered:

1. Show the user a compact summary of every section (story sentence, main flow bullets, business rules, acceptance criteria, route table, planned tests). Do not show the full markdown yet — keep the review skimmable.
2. Ask for any corrections or additions.
3. Only after the user approves, write the file.

## Writing the File

- Path: `spec/use-cases/use-case-<NNN>-<short-name>.md`, where `<NNN>` is the next free number from step 3 of "Before You Ask Anything".
- Start from `spec/use-cases/use-case-template.md` — keep the same section order, headings, and tables. Remove the leading `> Copy this template …` instruction block.
- Fill every `[bracketed placeholder]`. Do not leave any behind. If something is genuinely unknown, ask the user before writing — don't invent.
- Set `**Status:** Pending` and `**Date:** <today's date>`.
- Use `- [ ]` checkboxes for acceptance criteria and tests, as the template does.
- Leave the line about Implemented status untouched — it is a guardrail, not a placeholder.

## After Writing

- Print the path of the new file and a one-line summary.
- Suggest the obvious next step: `/implement-use-case <NNN>` when the user is ready to build it.
- Do **not** start implementing, do **not** commit, do **not** modify any other spec files. This skill's job ends at a written use case.
