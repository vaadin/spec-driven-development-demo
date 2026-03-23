# Design System

> Theme, component usage, and visual standards. Reference this when building or reviewing UI.

---

## 1. Theme

- **Base theme:** Vaadin Aura
- **Custom CSS:** `src/main/resources/META-INF/resources/styles.css`

---

## 2. Color Palette and Dark Theme

| Token | Value | Usage |
|-------|-------|-------|
| [e.g., `--primary`] | [e.g., `#1676F3`] | [Primary actions, links] |
| [e.g., `--error`] | [e.g., `#E53935`] | [Error states, destructive actions] |

When customizing Aura theme colors, always define both the `-light` and `-dark` variants of any color property. Aura uses the CSS `color-scheme` property to switch between modes.

Start color customization with these primary properties:
- `--aura-accent-color-light` and `--aura-accent-color-dark`
- `--aura-background-color-light` and `--aura-background-color-dark`

Best practice is to use Aura's existing color variables: `--aura-red`, `--aura-orange`, `--aura-yellow`, `--aura-green`, `--aura-blue`, or `--aura-purple`. These color values can be tweaked or new custom colors can be created. Never modify variables prefixed with --_ as these are internal.

Aura customizations should be applied with `:where(:root), :where(:host)` CSS selector.

---

## 3. Typography

| Element | Font / Size | Notes |
|---------|-------------|-------|
| [Headings] | [e.g., Aura defaults] | [Usage guidance] |
| [Body text] | [e.g., Aura defaults] | [Usage guidance] |

---

## 4. Spacing & Layout

- [Grid or layout system — e.g., Vaadin VerticalLayout / HorizontalLayout defaults]
- [Standard spacing units — e.g., Lumo spacing tokens]
- [Max content width, if any]

---

## 5. Component Standards

> Preferred Vaadin components and usage patterns. List components actually used or planned.

| Component | When to Use | Notes |
|-----------|-------------|-------|
| [e.g., `Button`] | [Primary and secondary actions] | [Use `ButtonVariant.LUMO_PRIMARY` for main CTA] |
| [e.g., `Grid`] | [Tabular data display] | [Always enable column sorting] |
| [e.g., `Notification`] | [User feedback] | [Use appropriate position and duration] |

---

## 6. Responsive Behavior

- **Mobile** (< 640px): Single column, stacked layouts, full-width cards
- **Tablet** (640–1024px): Two-column grid, side-by-side content
- **Desktop** (> 1024px): Multi-column grid, admin grid+form side by side

