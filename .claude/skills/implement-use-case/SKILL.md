---
name: implement-use-case
description: Implement a use case from the spec/ folder. Use when asked to implement, build, or work on a use case.
argument-hint: "[use-case-name or number]"
---

# Use Case Implementation

Implement the use case specified by $ARGUMENTS.

## Input

1. The use case document itself (in `spec/use-cases/`)
2. Related use cases
3. Generic specification files in `spec/`
4. Any potential images or other resources provided for the use case

## Implementation

The following steps are mandatory and sequential. **Do not skip or reorder them.** Each step must be completed before proceeding to the next.

### Step 1: Write the code
- Whenever you are even slightly unsure about Vaadin API usage, component behavior, theme variables, styling, or best practices -- use the Vaadin MCP server to look it up before guessing. Do not rely on memory for Vaadin specifics.

### Step 2: Visually verify with Playwright MCP
- **This step is mandatory.** Do not skip it, do not defer it.
- Run `/visual-verification` and follow every check: start the app, navigate every route, walk through the main flow, take screenshots, and validate the visual appearance.
- Fix any issues found before moving on.

### Step 3: Write and run automated tests
- Run `/use-case-tests` and ensure all tests pass.

### Step 4: Iterate
- Keep iterating until everything looks and works great. Prefer great results over finishing quickly.

### Step 5: Commit
- Once all steps are complete and everything works, commit the changes using `/commit`.

**All steps must be completed before a use case is considered implemented.**
