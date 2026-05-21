# Spec-Driven Development Template

A project template for building applications with AI by writing specifications instead of chat prompts. Specs in `spec/` are the single source of truth — the AI reads them, writes code, verifies the result visually, and writes tests.

## Getting Started

### 1. Know where the project-wide rules live

These files describe the project as a whole. They ship with sensible defaults (Vaadin + Spring Boot stack, a working design system, standard structure), so **you don't have to edit anything to get going** — you can jump straight to writing a use case.

Edit them when you want to deviate from the defaults or add project-specific context.

| File | What goes here |
|------|----------------|
| `spec/project-context.md` | Vision, users, scope, constraints |
| `spec/architecture.md` | Tech stack and application structure |
| `spec/datamodel/datamodel.md` | Entities and relationships |
| `spec/design-system.md` | Theme, components, visual standards |
| `CLAUDE.md` | Instructions that always apply when Claude works in this repo |

If the AI keeps getting something wrong or makes a choice you disagree with, the fix is almost always to add or sharpen a rule in one of these files — not to repeat yourself in chat.

### 2. Define use cases

Features are specified as use cases in `spec/use-cases/`. Each use case is one file describing one capability (e.g. "browse movies", "buy a ticket", "admin manages screenings").

The fastest way is `/new-use-case` — it interviews you for the details and writes a filled-in file in `spec/use-cases/` for you. A fresh project may have no use cases yet; just run the command to add the first one.

If you'd rather write it by hand, copy `spec/use-cases/use-case-template.md` to `use-case-NNN-short-name.md` and fill it in: main flow, business rules, acceptance criteria, routes.

### 3. Implement use cases one at a time

**For most work, one command is all you need:** `/implement-use-case <name-or-number>`. It drives the whole flow — writes code, verifies the UI visually, writes tests, commits.

Two helper skills exist for when you want to run a single step on its own:

| Skill | Purpose |
|-------|---------|
| `/implement-use-case <name-or-number>` | Implements a use case end-to-end: writes code, runs visual verification, writes tests, commits |
| `/visual-verification` | Runs Playwright against the app and checks the UI against the use case |
| `/use-case-tests` | Writes and runs the automated tests for a use case |

`/implement-use-case` calls the other two as part of its flow, so you rarely need to invoke them directly.

## A Typical Run-Through

Say you have three use cases: `use-case-001-browse-movies.md`, `use-case-002-buy-ticket.md`, `use-case-003-admin-screenings.md`.

1. **(Optional) Tweak the defaults.** Skim `spec/project-context.md` and `spec/architecture.md`. Fill in any `[bracketed placeholders]` you care about — or leave them; the defaults work.
2. **Implement the first use case.** Run `/implement-use-case 001`. When it finishes you have a running application with browsing working, screenshots verified, tests passing, and a commit on the branch.
3. **Review and adjust.** Run the app (`./mvnw` — see [DEVELOPMENT.md](DEVELOPMENT.md)), click through it. If something is off, update the use case file (or a project-wide rule) and re-run `/implement-use-case 001`.
4. **Move on to the next use case.** Run `/implement-use-case 002`, then `/implement-use-case 003`. Don't move on until the previous one is fully done — code, visual check, tests, commit.

After all three you have an application that does the three things you specified, with tests covering each, and a spec folder that explains why everything looks the way it does.

## More

- [`spec/README.md`](spec/README.md) — full spec structure and workflow
- [DEVELOPMENT.md](DEVELOPMENT.md) — build, run, and test commands
