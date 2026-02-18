# UC-003: Checkout

> Review the order summary and enter credit card details to complete the purchase.

---

**As a** Customer, **I want to** see a summary of my order and pay with my credit card **so that** I can complete the ticket purchase quickly.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I arrive on the checkout page after selecting tickets on the show detail page
- I see an order summary: show title, date/time, number of tickets, price per ticket, and total cost
- I see a credit card form with fields: card number, expiry date (MM/YY), and CVV
- I fill in my credit card details
- I click "Pay" to submit the purchase
- The system processes the payment (simulated — any valid-format card is accepted)
- On success, I am navigated to the ticket confirmation page

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | All credit card fields are mandatory |
| BR-02 | Card number must be 16 digits |
| BR-03 | Expiry date must be in MM/YY format and not in the past |
| BR-04 | CVV must be 3 digits |
| BR-05 | Payment is simulated — any card passing format validation is accepted |
| BR-06 | Available seats for the show are decremented on successful purchase |

---

## Acceptance Criteria

- [ ] Order summary displays show title, date/time, quantity, price per ticket, and total
- [ ] Credit card form has fields for card number, expiry date, and CVV
- [ ] Form validates that all fields are filled and match the required format
- [ ] Invalid input shows clear error messages next to the relevant field
- [ ] Submitting a valid form navigates to the ticket confirmation page
- [ ] Available seats are decremented after a successful purchase
- [ ] The page is responsive and usable on both desktop and mobile

---

## UI / Routes

- Order summary section at the top
- Credit card form below the summary with card number, expiry, and CVV fields
- "Pay" button at the bottom of the form
- Minimal fields — no name, address, or account creation

| Route | Access | Notes |
|-------|--------|-------|
| `/checkout` | public | Vaadin @Route — CheckoutView |
