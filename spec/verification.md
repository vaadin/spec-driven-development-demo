# Verification

> Visual verification process using Playwright MCP, plus a per-use-case checklist.
> Each checklist references specific design screenshots for comparison.

---

## 1. Visual Verification Process

Use the Playwright MCP server to visually verify each use case after implementation.

### Steps

1. **Ensure the application is running**
2. **Navigate to the route** — open the page defined in the use case's UI/Routes section
3. **Walk through the main flow** — perform each step from the use case's Main Flow
4. **Take screenshots** — capture the page state at key interaction points
5. **Compare against design screenshots:**
   - Resize browser to **mobile width (~390px)** and compare against the corresponding `design/screenshots/mobile/` screenshot
   - Resize browser to **desktop width (~1080px)** and compare against the corresponding `design/screenshots/desktop/` screenshot
   - Check **structural fidelity** — layout, component placement, visual hierarchy. This is not a pixel-perfect comparison; focus on whether the implementation faithfully represents the design intent
6. **Record results** — note any visual issues in the per-use-case checklist below

---

## 2. Per-Use-Case Verification Checklist

> Copy the template below for new use cases. Populated checklists for UC-001 through UC-004 follow.

### Template: UC-[NNN]: [Feature Title]

**Use case spec:** [`use-case-NNN-name.md`](use-cases/use-case-NNN-name.md)
**Mobile screenshot:** `design/screenshots/mobile/ScreenN.png`
**Desktop screenshot:** `design/screenshots/desktop/ScreenN-desktop.png`
**Verified by:** [Name/Agent]
**Date:** [YYYY-MM-DD]

#### Functional

- [ ] Main flow works end-to-end as described in the spec
- [ ] All business rules are enforced (list BR-IDs)
- [ ] All acceptance criteria pass
- [ ] Error/edge cases handled appropriately

#### Visual

- [ ] Page layout matches design screenshot at mobile width (~390px)
- [ ] Page layout matches design screenshot at desktop width (~1080px)
- [ ] Responsive breakpoints work correctly (mobile < 480px, tablet 480–767px, desktop ≥ 768px)
- [ ] Color theme matches warm amber dark theme (dark surfaces, amber accents)
- [ ] Typography hierarchy is correct (title, subtitle, body, labels)
- [ ] Touch targets are minimum 44px height
- [ ] Interactive elements respond correctly (hover, focus, click)

#### Result

- **Status:** [Pass / Fail / Partial]
- **Notes:** [Any issues found or follow-up items]

---

### UC-001: Browse Tickets

**Use case spec:** [`use-case-001-browse-tickets.md`](use-cases/use-case-001-browse-tickets.md)
**Mobile screenshot:** [`Screen1.png`](../design/screenshots/mobile/Screen1.png)
**Desktop screenshot:** [`Screen1-desktop.png`](../design/screenshots/desktop/Screen1-desktop.png)
**Verified by:**
**Date:**

#### Functional

- [ ] Main flow works end-to-end as described in the spec
- [ ] All business rules are enforced: BR-01, BR-02, BR-03, BR-04
- [ ] All acceptance criteria pass
- [ ] Error/edge cases handled appropriately

#### Visual

- [ ] Page layout matches `Screen1.png` at mobile width (~390px)
- [ ] Page layout matches `Screen1-desktop.png` at desktop width (~1080px)
- [ ] Responsive grid: 1 column (mobile), 2 columns (tablet), 4 columns (desktop)
- [ ] Filter bar: underline tabs with amber active state and 2px bottom border
- [ ] Ticket cards: 3px left border in mode color, emoji, name, badges, price
- [ ] Color theme matches warm amber dark theme
- [ ] Typography: page title, subtitle, card titles, prices in correct hierarchy
- [ ] Touch targets: filter tabs and card surfaces meet 44px minimum
- [ ] Interactive elements: card hover effect, filter tab switching

#### Result

- **Status:**
- **Notes:**

---

### UC-002: Ticket Detail

**Use case spec:** [`use-case-002-ticket-detail.md`](use-cases/use-case-002-ticket-detail.md)
**Mobile screenshot:** [`Screen2.png`](../design/screenshots/mobile/Screen2.png)
**Desktop screenshot:** [`Screen2-desktop.png`](../design/screenshots/desktop/Screen2-desktop.png)
**Verified by:**
**Date:**

#### Functional

- [ ] Main flow works end-to-end as described in the spec
- [ ] All business rules are enforced: BR-01, BR-02, BR-03, BR-04
- [ ] All acceptance criteria pass
- [ ] Error/edge cases handled appropriately

#### Visual

- [ ] Page layout matches `Screen2.png` at mobile width (~390px)
- [ ] Page layout matches `Screen2-desktop.png` at desktop width (~1080px)
- [ ] Responsive layout: single column (mobile/tablet), 2-panel with 380px right panel (desktop)
- [ ] Back navigation link visible at top
- [ ] Ticket info: large emoji, heading, badge row, description, price with "per ticket"
- [ ] Quantity stepper: 48×48px buttons, centered value display
- [ ] Subtotal box: elevated card with "SUBTOTAL" label and large amber price
- [ ] Primary button: amber gradient, black text, full-width
- [ ] Color theme matches warm amber dark theme
- [ ] Touch targets: stepper buttons and CTA meet 44px minimum

#### Result

- **Status:**
- **Notes:**

---

### UC-003: Checkout

**Use case spec:** [`use-case-003-checkout.md`](use-cases/use-case-003-checkout.md)
**Mobile screenshot:** [`Screen3.png`](../design/screenshots/mobile/Screen3.png)
**Desktop screenshot:** [`Screen3-desktop.png`](../design/screenshots/desktop/Screen3-desktop.png)
**Verified by:**
**Date:**

#### Functional

- [ ] Main flow works end-to-end as described in the spec
- [ ] All business rules are enforced: BR-01, BR-02, BR-03, BR-04, BR-05, BR-06, BR-07
- [ ] All acceptance criteria pass
- [ ] Error/edge cases handled appropriately

#### Visual

- [ ] Page layout matches `Screen3.png` at mobile width (~390px)
- [ ] Page layout matches `Screen3-desktop.png` at desktop width (~1080px)
- [ ] Responsive layout: summary on top + form below (mobile), form left + sticky summary right at 400px (desktop)
- [ ] Column order swap: summary first on mobile, moves to right sidebar on desktop
- [ ] Back navigation "← Back to Details" visible at top
- [ ] Order summary card: "ORDER SUMMARY" header, ticket name, subtitle, unit price, total in amber
- [ ] Payment form card: "PAYMENT DETAILS" header, labeled input fields, expiration + CVV side-by-side
- [ ] Purchase button: disabled until form valid (reduced opacity), amber gradient when enabled
- [ ] Color theme matches warm amber dark theme
- [ ] Touch targets: input fields and purchase button meet 44px minimum

#### Result

- **Status:**
- **Notes:**

---

### UC-004: Confirmation

**Use case spec:** [`use-case-004-confirmation.md`](use-cases/use-case-004-confirmation.md)
**Mobile screenshot:** [`Screen4.png`](../design/screenshots/mobile/Screen4.png)
**Desktop screenshot:** [`Screen4-desktop.png`](../design/screenshots/desktop/Screen4-desktop.png)
**Verified by:**
**Date:**

#### Functional

- [ ] Main flow works end-to-end as described in the spec
- [ ] All business rules are enforced: BR-01, BR-02, BR-03, BR-04
- [ ] All acceptance criteria pass
- [ ] Error/edge cases handled appropriately

#### Visual

- [ ] Page layout matches `Screen4.png` at mobile width (~390px)
- [ ] Page layout matches `Screen4-desktop.png` at desktop width (~1080px)
- [ ] Single centered column at all sizes, max-width 640px
- [ ] Success header: amber gradient circle (60px) with checkmark, heading, subtitle — all centered
- [ ] Confirmation code block: amber gradient background, "CONFIRMATION CODE" label, UUID in monospace
- [ ] Confirmation code font size: 15px mobile, 18px desktop
- [ ] Details list: label-value rows with dividers (Ticket, Mode, Quantity, Total, Card, Purchased)
- [ ] Secondary button: transparent background, amber border and text
- [ ] Color theme matches warm amber dark theme
- [ ] Touch targets: secondary button meets 44px minimum

#### Result

- **Status:**
- **Notes:**
