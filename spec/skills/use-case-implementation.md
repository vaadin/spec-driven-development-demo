# Use Case Implementation

This file describes how to implement a use case

---

## Input

The input for implementing a use case is:

1. The use case document itself
2. Related use cases
3. Generic specification files in /spec/
4. Any potential images or other resources provided for the use case

## Implementation

The following steps are mandatory and sequential. **Do not skip or reorder them.** Each step must be completed before proceeding to the next.

### Step 1: Write the code
- Whenever you are even slightly unsure about Vaadin API usage, component behavior, theme variables, styling, or best practices — use the Vaadin MCP server to look it up before guessing. Do not rely on memory for Vaadin specifics.

### Step 2: Visually verify with Playwright MCP
- **This step is mandatory.** Do not skip it, do not defer it.
- Follow every check in `visual-verification.md`: start the app, navigate every route, walk through the main flow, take screenshots, and validate the visual appearance.
- Fix any issues found before moving on.

### Step 3: Write and run automated tests
- Follow `use-case-tests.md` and ensure all tests pass.

### Step 4: Iterate
- Keep iterating until everything looks and works great. Prefer great results over finishing quickly.

**All steps must be completed before a use case is considered implemented. Do not commit until all steps are done.**
