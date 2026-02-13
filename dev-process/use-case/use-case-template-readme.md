# Use Case Templates

> **Purpose:** Document feature requirements in a structured format for product teams, developers, and AI agents.
>
> **Format:** Markdown files stored in the project repository.

---

## Quick Start

1. **Pick a template** using the guide below
2. **Copy** [use-case-template-simple.md](use-case-template-simple.md) or [use-case-template-advanced.md](use-case-template-advanced.md)
3. **Fill it out** — replace all placeholders with your content
4. **Save** in your project's `/docs/use-cases/` folder

---

## Which Template?

**Use the Simple template** when the feature is straightforward: standard workflows, CRUD operations, clear scope. Think "one screen, one job." This matches the style of the movie-browsing and product-management examples.

**Use the Advanced template** when the feature has multiple actors, many alternative/exception flows, or needs formal preconditions and business rules. Think "multi-step process with real-world edge cases" — like an inventory check or equipment audit.

---

## Template Comparison

| Aspect | Simple | Advanced |
|--------|--------|----------|
| **Sections** | ~5 | ~11 |
| **Page length** | 1 page | 2-4 pages |
| **Time to complete** | 15-30 minutes | 1-2 hours |
| **Flow style** | First-person narrative | Actor/System table |
| **Alternative flows** | Inline sections | Dedicated section with tables |
| **Business rules** | Inline list | Numbered table |
| **Best for** | Single-actor, single-screen features | Multi-actor, multi-step workflows |

---

## Writing Tips

These guidelines help both humans and AI agents work from your use cases:

1. **Be specific** — state exactly what should happen; avoid ambiguity
2. **Be consistent** — use the same term for the same concept throughout
3. **Write from the user's perspective** — "I see...", "I click..." keeps it grounded
4. **Include examples** — concrete data values, error messages, edge cases
5. **Separate what from how** — use cases define requirements, not implementation

### Checklist Before Handing Off

- [ ] Actor roles and permissions are clear
- [ ] Happy path is complete with expected inputs and outputs
- [ ] Alternative and error scenarios are covered
- [ ] Validation rules are explicit
- [ ] UI layout is described or linked to a mockup

---
