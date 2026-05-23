---
name: new-use-case
description: Define a new use case by interviewing the user, then write a filled-in `spec/use-cases/use-case-NNN-short-name.md`. Use when the user wants to add, draft, or specify a new feature/use case.
---

# New Use Case

Capture a new use case through a four-phase conversation, then write it to `spec/use-cases/use-case-NNN-short-name.md` using `spec/use-cases/use-case-template.md` as the structure.

The goal is a complete, implementation-ready spec. The template's structural sections — Goal, Actors, Preconditions, Trigger, Main Flow, Alternative Flows, Postconditions, Business Rules, Tests, UI/Routes — are all required. Keep the conversation going until every section is filled in.

## Before You Ask Anything

1. Read `spec/project-context.md` and `spec/architecture.md` so questions stay consistent with the project's vision, users, and stack.
2. Read `spec/datamodel/datamodel.md` if it exists — refer to actual entities when discussing data.
3. List `spec/use-cases/` to:
   - See existing use cases (don't duplicate them; suggest references where flows overlap).
   - Determine the next free `NNN` number (zero-padded, e.g. `004`).
4. Read `spec/use-cases/use-case-template.md` — it is the authoritative shape of the output.

If any of these files are missing, tell the user and stop; the project isn't ready for a use case yet.

## Phase 1 — Free-form description and scope check

Open with a single open question. Do not start a structured interview yet.

> *"Describe the feature in your own words — what does it do, who uses it, why? Don't worry about structure; tell it however feels natural."*

Let the user write a paragraph or two. Do not interrupt with structural questions.

**Thin-answer fallback.** If the response is one short sentence (roughly under 20 words) with no actor, no flow, and no rationale, ask one follow-up: *"Can you walk me through it a bit more — who uses it and what they actually do?"*. If the second answer is still that thin, skip Phase 2 entirely and run the structured questions in Phase 3 top to bottom. Do not try to extract from nothing.

### Scope check (before extracting)

Before moving to Phase 2, decide what the user just described. Look for these signals:

- **Multiple distinct actor/goal pairs** ("admins do X, customers do Y").
- **Coordination words** covering separate flows: "and also", "then they can", "users can do X and Y and Z".
- **Application-scope vocabulary**: "the application", "the system supports", "we want to build…".
- Breadth and length well beyond one feature.

Classify and respond:

- **Single use case** → continue to Phase 2 normally.
- **Multiple use cases** → name what you detected and list them. Example: *"It sounds like this is three use cases: 1) customer browses movies, 2) customer buys ticket, 3) admin manages schedule. Which one should we draft first?"* Draft only the chosen one. At the end, in "After Writing", suggest running `/new-use-case` again for each of the others.
- **Whole application** → stop the skill. Do not try to write a use case. Tell the user this looks like application-level scope and suggest they fill in `spec/project-context.md` first (vision, users, scope, constraints), then come back to add use cases one feature at a time. Optionally offer to help sketch a candidate UC list from the description so they can pick one to draft later.
- **Borderline (one UC with sub-flows, or several closely related UCs?)** → don't guess. Ask explicitly: *"Is this one use case with branches, or multiple use cases?"* Let the user decide, then proceed.

## Phase 2 — Extract and confirm

Parse the description and pre-fill what you can. Be explicit about what is **stated** vs **inferred** vs **missing**, so the user can correct wrong inferences.

Try to extract:
- **Goal sentence** (`As a … I want to … so that …`)
- **Primary actor** (often the role they named: "admin", "customer", "guest", etc.)
- **Secondary actors** (other systems mentioned: a payment gateway, an email service)
- **Main flow sketch** (any sequence of steps they described — order them, but don't yet rewrite as numbered actor/system steps)
- **Short name** (kebab-case, 2–4 words, derived silently)
- **Business rules** explicitly stated ("must be logged in", "max 6", "no past dates")
- **UI hints** ("a table with…", "admin page", "the existing X view")

Show the user a compact extraction summary, marking each item:
- **[stated]** — they said it
- **[inferred]** — you guessed from context
- **[missing]** — needs to be asked

Ask the user to correct inferences and flag anything wrong. Do not show the full markdown yet — keep the review skimmable.

**Guardrail: never silently invent.** If something is not in the description, mark it `[missing]` rather than filling it in.

## Phase 3 — Targeted follow-ups

Ask only about what is still **[missing]** or **[inferred but uncertain]**. Order by structural importance, not template order. Skip topics that Phase 2 fully resolved.

Use `AskUserQuestion` when there is a clear small set of choices (primary actor when obvious, UI tech, access level). Use plain chat for open-ended answers.

After each answer, restate your understanding in one sentence before moving on, so misunderstandings surface early.

### A. Actors (if not confirmed in Phase 2)
- Confirm the primary actor. If a secondary actor (external system, second role) is plausible, ask explicitly.

### B. Preconditions
- What must already be true for this use case to start? Authentication, prior use cases having run, data that must exist.
- If the user genuinely has none, write "None".

### C. Trigger
- What event starts the use case? A click, a route navigation, a scheduled job, an external event.
- Often inferable from the main flow's first step — propose and confirm.

### D. Main flow (numbered actor/system steps)
- Take the sketch from Phase 2 and rewrite as numbered steps that alternate between actor and system. Each step is one observable action.
- Read it back. Ask the user to fix sequencing, add missing steps, or split steps that are too coarse.
- Keep steps atomic — alternative flows will branch off specific step numbers, so coarse steps are hard to extend later.

### E. Alternative flows (non-negotiable — always probe)
- Walk each main-flow step and ask: *"what can go wrong at this step?"* Look for: validation failures, permission denials, empty states, external-system errors, conflicts.
- For each alt flow capture: short name, branching step number, condition, mini-flow, and whether it returns to main flow or ends the use case.
- If the user genuinely has nothing for a step, accept that and move on. Do not invent failures. But ask for every step.

### F. Postconditions
- On success: what is true after the main flow completes?
- On failure: what is true if an alt flow ends the use case?

### G. Business rules (beyond what was stated)
- Ask for rules that constrain the flow but weren't covered: required fields, limits, visibility/access, time/ordering, edge cases.
- Each rule must be testable. Reference the data model and existing use cases where relevant.

### H. UI / routes
- Public (React/Hilla) or admin (Vaadin Flow)? Use `AskUserQuestion`.
- Route path(s), access level (public / authenticated / ADMIN), layout requirements (component types, key interactions, responsive needs).
- If the user has a mockup or image, ask them to point at it and reference it in the file.

### I. Tests (placeholder)
- Propose a test class name based on the short name (e.g. `BrowseMoviesTest`, `BuyTickets.test.tsx`).
- List planned coverage as bullets: Main Flow steps, each Alternative Flow, each Business Rule. The actual tests are written later by `/use-case-tests`; here we are just declaring intent.

## Phase 4 — Draft and confirm

1. Show the user a compact summary of every section: goal, actors, preconditions, trigger, main flow (numbered), alt flows, postconditions, business rules, route table, planned tests. Do not show the full markdown yet — keep the review skimmable.
2. Ask for any corrections or additions.
3. Only after the user approves, write the file.

## Writing the File

- Path: `spec/use-cases/use-case-<NNN>-<short-name>.md`, where `<NNN>` is the next free number from step 3 of "Before You Ask Anything".
- Start from `spec/use-cases/use-case-template.md` — keep the same section order, headings, and tables. Remove the leading `> Copy this template …` instruction block.
- Fill every `[bracketed placeholder]`. Do not leave any behind. If something is genuinely unknown, ask the user before writing — don't invent.
- Set `**Status:** Pending` and `**Date:** <today's date>`.
- Use `- [ ]` checkboxes only in the Tests section.
- Leave the line about Implemented status untouched — it is a guardrail, not a placeholder.

## After Writing

- Print the path of the new file and a one-line summary.
- Suggest the obvious next step: `/implement-use-case <NNN>` when the user is ready to build it.
- Do **not** start implementing, do **not** commit, do **not** modify any other spec files. This skill's job ends at a written use case.
