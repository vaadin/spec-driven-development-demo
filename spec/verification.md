# Verification

> Visual verification process using Playwright MCP, plus a per-use-case checklist.
> Copy the checklist (section 2) for each implemented use case.

---

## 1. Visual Verification Process

Use the Playwright MCP server to visually verify each use case after implementation.

### Steps

1. **Ensure the application is running**
2. **Navigate to the route** — open the page defined in the use case's UI/Routes section
3. **Walk through the main flow** — perform each step from the use case's Main Flow
4. **Take screenshots** — capture the page state at key interaction points
5. **Check visual appearance:**
   - Layout matches expectations (spacing, alignment, sizing)
   - Typography is readable and consistent
   - Interactive elements are clearly identifiable
   - Responsive behaviour works at common breakpoints (mobile, tablet, desktop)
6. **Record results** — note any visual issues in the per-use-case checklist below

---

## 2. Per-Use-Case Verification Checklist

> Copy this section for each use case. Name it: **UC-[NNN]: [Feature Title]**

### UC-001: Browse Shows

**Use case spec:** [`use-case-001-browse-shows.md`](use-cases/use-case-001-browse-shows.md)
**Verified by:** Claude
**Date:** 2026-02-18

#### Functional

- [x] Main flow works end-to-end as described in the spec
- [x] All business rules are enforced (BR-01, BR-02, BR-03)
- [x] All acceptance criteria pass
- [x] Error/edge cases handled appropriately

#### Visual

- [x] Page layout matches expectations
- [x] Interactive elements respond correctly (hover, focus, click)
- [x] Loading states and transitions are smooth
- [x] Responsive at mobile and desktop widths

#### Result

- **Status:** Pass
- **Notes:** All 6 shows displayed in card grid. Sold-out shows (Les Misérables, The Lion King) greyed out with SOLD OUT badge and not clickable. Shows sorted by date (soonest first). Low-stock badge shown for Wicked (3 seats). Responsive layout: 2 columns on desktop, 1 column on mobile.

---

### UC-002: View Show Details

**Use case spec:** [`use-case-002-view-show-details.md`](use-cases/use-case-002-view-show-details.md)
**Verified by:** Claude
**Date:** 2026-02-18

#### Functional

- [x] Main flow works end-to-end as described in the spec
- [x] All business rules are enforced (BR-01, BR-02, BR-03, BR-04)
- [x] All acceptance criteria pass
- [x] Error/edge cases handled appropriately

#### Visual

- [x] Page layout matches expectations
- [x] Interactive elements respond correctly (hover, focus, click)
- [x] Loading states and transitions are smooth
- [x] Responsive at mobile and desktop widths

#### Result

- **Status:** Pass
- **Notes:** Cover image, title, description, date/time, and price per ticket all displayed. Ticket quantity selector with +/- step buttons, defaults to 1, min 1, max min(6, availableSeats). Subtotal updates dynamically on quantity change (verified: 3 × $54.99 = $164.97). "Buy Tickets" navigates to checkout with correct params. On mobile, image stacks above content. Back button returns to show list.

---

### UC-003: Checkout

**Use case spec:** [`use-case-003-checkout.md`](use-cases/use-case-003-checkout.md)
**Verified by:** Claude
**Date:** 2026-02-18

#### Functional

- [x] Main flow works end-to-end as described in the spec
- [x] All business rules are enforced (BR-01, BR-02, BR-03, BR-04, BR-05, BR-06)
- [x] All acceptance criteria pass
- [x] Error/edge cases handled appropriately

#### Visual

- [x] Page layout matches expectations
- [x] Interactive elements respond correctly (hover, focus, click)
- [x] Loading states and transitions are smooth
- [x] Responsive at mobile and desktop widths

#### Result

- **Status:** Pass
- **Notes:** Order summary displays show title, date/time, quantity, price per ticket, and total. Credit card form has card number, expiry (MM/YY), and CVV fields. Validation tested: empty fields show clear error messages ("Card number must be 16 digits", "Use MM/YY format", "CVV must be 3 digits"). Submitting valid card (4111111111111111, 12/27, 123) navigates to confirmation. Seats decremented after purchase (verified Chicago 200 → 197). Missing/invalid query params redirect to home.

---

### UC-004: Ticket Confirmation

**Use case spec:** [`use-case-004-ticket-confirmation.md`](use-cases/use-case-004-ticket-confirmation.md)
**Verified by:** Claude
**Date:** 2026-02-18

#### Functional

- [x] Main flow works end-to-end as described in the spec
- [x] All business rules are enforced (BR-01, BR-02)
- [x] All acceptance criteria pass
- [x] Error/edge cases handled appropriately

#### Visual

- [x] Page layout matches expectations
- [x] Interactive elements respond correctly (hover, focus, click)
- [x] Loading states and transitions are smooth
- [x] Responsive at mobile and desktop widths

#### Result

- **Status:** Pass
- **Notes:** UUID confirmation code displayed prominently in dashed-border box. Show title, date/time, ticket count, and total paid shown below. Page accessible via direct URL (tested navigating to /ticket/{uuid} — loads correctly). Clean centered layout with "Browse More Shows" button. Invalid confirmation codes show "Ticket not found" with a back button.
