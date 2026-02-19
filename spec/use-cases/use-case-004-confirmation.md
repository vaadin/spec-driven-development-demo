# UC-004: Confirmation

> Display purchase confirmation with ticket details and a unique validation code.

---

**As a** rider, **I want to** see my ticket confirmation with a unique code **so that** I have proof of my purchase.

**Status:** Draft
**Date:** 2026-02-19

---

## Main Flow

- I arrive on the confirmation page after a successful purchase
- I see a confirmation message (e.g., "Purchase Successful!")
- I see the full ticket details: ticket name, transit mode, ticket type, quantity, and total price
- I see the last 4 digits of the card used
- I see the purchase date and time
- I see a prominent UUID confirmation code that serves as my ticket validation/uniqueness identifier
- I can tap a "Buy Another Ticket" button to return to the browse page

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | The UUID confirmation code is generated at purchase time and stored in the PurchaseOrder |
| BR-02 | The confirmation code is displayed prominently as the primary identifier |
| BR-03 | Only the last 4 digits of the credit card are shown |
| BR-04 | The confirmation page is accessible only via a valid purchase — navigating directly without a valid order shows an error or redirects to browse |

---

## Acceptance Criteria

- [ ] Confirmation page displays a success message
- [ ] Ticket name, transit mode, ticket type, quantity, and total price are shown
- [ ] Last 4 digits of the credit card are displayed
- [ ] Purchase date and time are displayed
- [ ] UUID confirmation code is prominently displayed
- [ ] "Buy Another Ticket" button navigates back to the browse page (UC-001)
- [ ] Navigating directly to the confirmation page without a valid purchase redirects to browse
- [ ] Layout is responsive and usable on mobile screens

---

## UI / Routes

- Success banner or heading at the top
- Ticket and order details in a clear summary layout
- UUID confirmation code displayed large and prominent (the user's "ticket")
- "Buy Another Ticket" button at the bottom

| Route | Access | Notes |
|-------|--------|-------|
| `/confirmation/{confirmationCode}` | public | Vaadin @Route with UUID parameter — looks up PurchaseOrder by confirmation code |
