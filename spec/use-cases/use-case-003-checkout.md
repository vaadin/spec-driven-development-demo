# UC-003: Checkout

> Review order summary and enter credit card information to complete the purchase.

---

**As a** rider, **I want to** review my order and enter my payment details **so that** I can complete my ticket purchase.

**Status:** Draft
**Date:** 2026-02-19

---

## Main Flow

- I arrive on the checkout page after selecting a ticket and quantity
- I see an order summary showing: ticket name, transit mode, ticket type, quantity, unit price, and total price
- Below the summary, I see a credit card form with fields for cardholder name, card number, expiration date, and CVV
- I fill in all four fields
- I tap "Purchase" to complete the transaction
- The system simulates the payment (no real processing), creates a PurchaseOrder record with a UUID confirmation code, and navigates me to the confirmation page

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | All four credit card fields are required before purchase |
| BR-02 | Card number must be 16 digits |
| BR-03 | Expiration date format is MM/YY |
| BR-04 | CVV must be 3 digits |
| BR-05 | Payment is always simulated as successful — no real validation or processing |
| BR-06 | A PurchaseOrder is persisted with a generated UUID confirmation code |
| BR-07 | Only the last 4 digits of the card number are stored |

---

## Acceptance Criteria

- [ ] Order summary displays ticket name, transit mode, ticket type, quantity, unit price, and total
- [ ] Credit card form has fields: cardholder name, card number, expiration (MM/YY), and CVV
- [ ] "Purchase" button is disabled until all fields are filled with valid formats
- [ ] Submitting the form creates a PurchaseOrder record in the database
- [ ] Only the last 4 digits of the card number are persisted
- [ ] After successful purchase, navigates to the confirmation page (UC-004)
- [ ] A back link or button allows returning to the detail page (ticket ID retained in URL for navigation)
- [ ] Layout is responsive and usable on mobile screens

---

## UI / Routes

- Order summary section at the top (read-only)
- Credit card form below with four fields: cardholder name, card number, expiration, CVV
- "Purchase" button at the bottom — disabled until form is valid
- Back navigation to the detail page

| Route | Access | Notes |
|-------|--------|-------|
| `/checkout/{ticketId}/{quantity}` | public | Vaadin @Route with URL parameters — enables back-navigation to detail page |
