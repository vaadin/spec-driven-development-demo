# UC-005: View Bookings

**As an** admin, **I want to** look up bookings **so that** I can verify tickets at pickup.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I navigate to the admin bookings page.
- I see a searchable list of all bookings.
- I can search by confirmation code, customer name, or email.
- The list shows: confirmation code, customer name, email, movie title, show time, and number of tickets.
- I find the booking I need and verify the customer's ticket.

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | Search matches against confirmation code, customer name, and email |
| BR-02 | Search is case-insensitive |
| BR-03 | Only authenticated admin users can access this view |

---

## Acceptance Criteria

- [ ] Admin can view a list of all bookings
- [ ] Admin can search bookings by confirmation code
- [ ] Admin can search bookings by customer name
- [ ] Admin can search bookings by email
- [ ] Each booking displays: confirmation code, customer name, email, movie title, show time, number of tickets
- [ ] Page requires authentication

---

## UI / Routes

- Search field at the top of the page
- Grid showing booking records below the search field
- Grid columns: confirmation code, customer name, email, movie title, show time, number of tickets

| Route | Access | Notes |
|-------|--------|-------|
| `/admin/bookings` | authenticated | Vaadin @Route("admin/bookings") |
