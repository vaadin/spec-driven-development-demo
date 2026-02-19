# UC-002: Ticket Detail

> View ticket details and select the number of tickets to purchase.

---

**As a** rider, **I want to** see the details of a ticket and choose how many to buy **so that** I can proceed to checkout with the right quantity.

**Status:** Draft
**Date:** 2026-02-19

---

## Main Flow

- I arrive on the detail page after tapping a ticket card on the browse page
- I see the ticket name, transit mode, ticket type, a description, and the unit price
- I see a quantity selector defaulting to 1
- I adjust the quantity (e.g., increase to 3)
- I see the subtotal update in real time (quantity × unit price)
- I tap a "Continue to Checkout" button to proceed to the summary/payment page

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | Quantity must be between 1 and 5 |
| BR-02 | Default quantity is 1 |
| BR-03 | Subtotal is calculated as quantity × unit price and displayed in real time |
| BR-04 | The "Continue to Checkout" button is always enabled (quantity is always valid given the constraints) |

---

## Acceptance Criteria

- [ ] Detail page displays ticket name, transit mode, ticket type, description, and unit price
- [ ] Quantity selector defaults to 1
- [ ] Quantity cannot go below 1 or above 5
- [ ] Subtotal updates in real time when quantity changes
- [ ] "Continue to Checkout" button navigates to the checkout page (UC-003) passing ticket and quantity
- [ ] A back link or button allows returning to the browse page
- [ ] Layout is responsive and usable on mobile screens

---

## UI / Routes

- Ticket information displayed prominently at the top
- Quantity selector (stepper or number input) with +/- controls
- Subtotal displayed below the quantity selector
- "Continue to Checkout" button at the bottom
- Back navigation to browse page

| Route | Access | Notes |
|-------|--------|-------|
| `/ticket/{ticketId}` | public | Vaadin @Route with URL parameter |
