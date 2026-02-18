# UC-002: View Show Details

> View show details and select the number of tickets to purchase.

---

**As a** Customer, **I want to** see more details about a show and choose how many tickets I want **so that** I can decide whether to attend and proceed to purchase.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I arrive on the show detail page after clicking a show from the listing
- I see the cover image, title, full description, date/time, and price per ticket
- I see a ticket quantity selector (defaulting to 1)
- I adjust the quantity to the number of tickets I want
- I click a "Buy Tickets" button to proceed to checkout

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | Minimum ticket quantity is 1 |
| BR-02 | Maximum ticket quantity is 6 per transaction |
| BR-03 | Ticket quantity cannot exceed the number of available seats |
| BR-04 | The subtotal (quantity x price) is displayed and updates as quantity changes |

---

## Acceptance Criteria

- [ ] The page displays cover image, title, description, date/time, and price per ticket
- [ ] The ticket quantity selector defaults to 1
- [ ] The quantity cannot be set below 1 or above 6
- [ ] The quantity cannot exceed available seats
- [ ] A running subtotal is shown and updates when quantity changes
- [ ] Clicking "Buy Tickets" navigates to the checkout page with the selected show and quantity
- [ ] The page is responsive and usable on both desktop and mobile

---

## UI / Routes

- Cover image displayed prominently, description below or beside it
- Ticket quantity selector with +/- controls or a number input
- Subtotal displayed near the quantity selector
- "Buy Tickets" call-to-action button

| Route | Access | Notes |
|-------|--------|-------|
| `/show/:id` | public | Vaadin @Route — ShowDetailView |
