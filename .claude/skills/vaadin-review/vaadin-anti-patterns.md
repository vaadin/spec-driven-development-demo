# Vaadin 25 Anti-Pattern Catalog

Each entry describes a known bad practice in Vaadin 25 Flow applications. To add a new entry, copy any existing block and fill in the fields. To remove one, delete the entire `## AP-XXX` block.

---

## AP-001: Inline Styles Instead of CSS Classes

- **Severity:** Warning
- **Detect:** `getStyle().set(` in Java files — ignore occurrences where the value is genuinely dynamic (comes from a variable, calculation, or runtime conditional)
- **Why:** Inline styles bypass the CSS cascade, cannot use media queries or pseudo-selectors (`:hover`, `:focus`), cannot be themed, and scatter presentation across Java code. When inline styles need overriding, it forces `!important` in CSS.
- **Fix:** Add a CSS class name via `addClassName()` or `addClassNames()` and define styles in a CSS file.
  - The application's main CSS entry point is a single file (e.g., `styles.css`) loaded via `@StyleSheet("styles.css")` on the `AppShellConfigurator` class.
  - To split CSS into multiple files, use `@import url(path/to/file.css)` inside `styles.css`. The path is relative to `src/main/resources/META-INF/resources/`.
  - **Reusable styles** (shared across views): define in a dedicated CSS file and `@import url()` it from `styles.css`.
  - **View-specific styles**: add a view-level class name to the root layout (e.g., `addClassName("my-view")`), create a separate CSS file, scope rules under the view class, and `@import url()` it from `styles.css`.
  - If the Lumo theme is active, consider Lumo Utility Classes via `addClassNames(LumoUtility.Padding.MEDIUM)` instead of custom CSS. Note: `LumoUtility` is **not** available with the Aura theme.

## AP-002: String-Based Route Navigation

- **Severity:** Warning
- **Detect:** `UI.getCurrent().navigate("` — a string literal passed to `navigate()` instead of a class reference
- **Why:** String-based navigation is fragile. If a `@Route` value changes, all string references break silently at runtime with no compile-time error.
- **Fix:** Use the class-based overload: `UI.getCurrent().navigate(TargetView.class, parameter)`. For routes with multiple parameters, use `RouteParameters`.

## AP-003: Duplicated Styling Code Across Views

- **Severity:** Warning
- **Detect:** Two or more view files containing near-identical chains of `getStyle().set(...)` calls or `addClassName(...)` calls that produce the same visual element
- **Why:** Duplicated styling violates DRY. When the design changes, every copy must be updated. Missed copies cause visual inconsistency.
- **Fix:** Extract the shared visual pattern into either:
  - A **CSS class** (preferred) — define it once in CSS, apply via `addClassName()` in each view.
  - A **reusable component or factory method** — if the duplication includes both structure and styling, extract a shared Java method or `Composite` component.

## AP-004: `!important` in CSS to Override Inline Styles

- **Severity:** Info
- **Detect:** `!important` in `.css` files — check whether it exists to override an inline style set via `getStyle().set()` in Java
- **Why:** `!important` breaks the normal CSS cascade and makes styles harder to override and maintain. It is often a symptom of AP-001 (inline styles that can only be overridden with `!important`).
- **Fix:** Remove the inline style (fix AP-001 first), then the `!important` becomes unnecessary.

## AP-005: Inline JavaScript in Plain View/Component Classes

- **Severity:** Warning
- **Detect:** `executeJs(` or `getElement().executeJs(` in Java files. Also check for `callJsFunction(` — this is acceptable **only** if the class (or a parent) has a `@JavaScript` or `@JsModule` annotation indicating it is a proper client-side extension.
- **Why:** `executeJs()` in a plain view or simple component class is almost always a hack — embedding raw JS strings in Java to work around missing server-side API. It is fragile, hard to test, and a sign that a proper client-side extension or existing component API should be used instead.
- **Fix:** Check whether the component already has a Java API for what the JS is doing. If not, create a proper client-side extension: a JS/TS module loaded via `@JsModule` with well-defined `callJsFunction` entry points. For one-off DOM tweaks, consider whether CSS or a component configuration can achieve the same result without JS.

## AP-006: Mismatched Theme CSS Variables or Utilities

- **Severity:** Warning
- **Detect:** First, determine the active base theme by inspecting the `AppShellConfigurator` implementation (typically the `@SpringBootApplication` class) for `@StyleSheet` annotations. The base theme is identified by `Aura.STYLESHEET`, `Lumo.STYLESHEET`, or `Base.STYLESHEET`. Then:
  - If **Aura** is the active theme: grep CSS files for `--lumo-` variable references — these will not resolve at runtime.
  - If **Lumo** is the active theme: grep CSS files for `--aura-` variable references — these will not resolve at runtime.
  - In Java files: if Aura is the active theme, check for any use of `LumoUtility` — this class provides Lumo-specific utility class names that do not exist in the Aura theme and will have no effect.
  - Exception: `--lumo-` or `--aura-` references are acceptable if they appear inside a fallback expression like `var(--lumo-size-m, 2rem)` with an explicit default.
- **Why:** Vaadin ships multiple themes (Lumo, Aura, Base) with different sets of CSS custom properties. Using `--lumo-*` variables in an Aura-themed app (or vice versa) produces silent failures — the variables are undefined, so properties fall back to `initial` or are ignored entirely. This causes invisible styling bugs that are hard to diagnose. Similarly, `LumoUtility` class names only exist in the Lumo theme and have no effect under Aura.
- **Fix:** Replace mismatched CSS variables with the correct theme equivalents:
  - **Aura variables:** `--aura-accent-color`, `--aura-accent-text-color`, `--aura-accent-surface`, `--aura-green-text`, etc.
  - **Vaadin base variables** (work across all themes): `--vaadin-background-container`, `--vaadin-background-container-strong`, `--vaadin-border-color`, `--vaadin-text-color-secondary`, `--vaadin-radius-s/m/l`, `--vaadin-padding-xs/s/m/l`, `--vaadin-gap-s/m/l`.
  - Prefer `--vaadin-*` base variables when possible, as they work with any theme. Use `--aura-*` or `--lumo-*` only for theme-specific features (e.g., accent colors).
  - If Aura is active and code uses `LumoUtility`, replace with explicit `addClassName("...")` calls and define the styles in CSS.
