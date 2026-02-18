# UC-004: Ticket Confirmation

> View the purchased ticket with a unique identifier to present at the venue.

---

**As a** Customer, **I want to** see my ticket with a unique code after purchasing **so that** I can show it at the venue for entry.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I arrive on the ticket confirmation page after a successful payment
- I see a confirmation message with the show title, date/time, number of tickets, and total paid
- I see a unique confirmation code (UUID) displayed prominently
- I can save or screenshot this page to present at the venue later

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | Each purchase generates a unique UUID confirmation code |
| BR-02 | The confirmation page is accessible via a direct URL using the confirmation code |

---

## Acceptance Criteria

- [ ] The page displays show title, date/time, number of tickets, and total paid
- [ ] A unique UUID confirmation code is displayed prominently
- [ ] The confirmation page is reachable via a direct URL (e.g., `/ticket/:confirmationCode`)
- [ ] The page is responsive and usable on both desktop and mobile

---

## UI / Routes

- Confirmation code displayed large and centred for easy scanning/screenshotting
- Show details and purchase summary below the code
- Clean, minimal layout — no navigation distractions

| Route | Access | Notes |
|-------|--------|-------|
| `/ticket/:confirmationCode` | public | Vaadin @Route — TicketConfirmationView |
