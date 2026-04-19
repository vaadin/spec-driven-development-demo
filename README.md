# Spec-Driven Development Template

A project template for spec-driven development with AI. Instead of describing features in chat prompts, you write specifications in structured markdown files. The AI reads the specs and implements them, including visual verification and automated tests.

## How It Works

1. **Define specs** in `spec/` - project context, architecture, data model, and individual use cases
2. **Ask the AI to implement** a use case - it reads the spec, writes code, visually verifies with Playwright, and writes tests
3. **Iterate** - update specs as the project evolves; they remain the single source of truth

See [`spec/README.md`](spec/README.md) for the full spec structure and workflow.

## Development

See [DEVELOPMENT.md](DEVELOPMENT.md) for build, run, and test commands.
