# UC-002: Purchase Tickets

**As a** visitor, **I want to** view movie details and buy tickets **so that** I can attend a screening.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I click on a movie from the home page and arrive at the movie details view.
- I see the movie title, description, poster image, and today's show times.
- I select a show time.
- I choose the number of tickets (1–10).
- I enter my name and email address.
- I click "Purchase".
- The system validates availability and my input.
- The system generates a confirmation code and displays a confirmation screen.
- The confirmation screen shows: confirmation code, movie title, show time, number of tickets, and a note that payment is collected at pickup.

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | Cannot purchase more tickets than available seats for the selected show |
| BR-02 | Maximum 10 tickets per order |
| BR-03 | Name and email are required |
| BR-04 | No online payment — payment is collected at pickup |
| BR-05 | No seat selection — only ticket count |
| BR-06 | Confirmation code must be unique |
| BR-07 | Only today's show times are listed for purchase |

---

## Acceptance Criteria

- [ ] Movie details page shows title, description, poster, and today's show times
- [ ] User can select a show time
- [ ] User can choose number of tickets (1–10)
- [ ] User must enter name and email before purchasing
- [ ] Purchase is rejected if requested tickets exceed available seats
- [ ] Successful purchase displays a confirmation screen with code, movie, time, ticket count, and payment note
- [ ] Confirmation code is unique
- [ ] Page is accessible without authentication

---

## UI / Routes

- Top section: movie poster, title, and description
- Show time selection: list or buttons for today's available times
- Ticket form: number of tickets selector, name field, email field, purchase button
- Confirmation view: displayed after successful purchase (can be same route or inline)

| Route | Access | Notes |
|-------|--------|-------|
| `/movie/{movieId}` | public | Vaadin @Route("movie") with URL parameter |
