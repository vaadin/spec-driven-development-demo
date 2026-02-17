# UC-004: Manage Shows

**As an** admin, **I want to** schedule screenings **so that** visitors can browse and buy tickets.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I navigate to the admin shows page.
- I see a grid listing all shows with movie title, date, time, and available seats.
- To add a show, I click "Add Show", select a movie, set date, time, and available seats, then save.
- To edit a show, I select it in the grid, modify fields in the form, and save.
- To delete a show, I select it in the grid and click "Delete".
- The system confirms deletion before removing the show.

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | All show fields are required: movie, date, time, available seats |
| BR-02 | Available seats must be a positive number |
| BR-03 | Movie must be selected from existing movies |
| BR-04 | A show with existing bookings cannot be deleted |
| BR-05 | Only authenticated admin users can access this view |

---

## Acceptance Criteria

- [ ] Admin can view a grid of all shows
- [ ] Admin can add a new show by selecting a movie and setting date, time, and available seats
- [ ] Admin can edit an existing show
- [ ] Admin can delete a show that has no bookings
- [ ] Deleting a show with existing bookings is prevented with an error message
- [ ] All fields are validated as required before saving
- [ ] Page requires authentication

---

## UI / Routes

- Master-detail layout: grid on the left, form on the right
- Grid columns: movie title, date, time, available seats
- Form fields: movie (dropdown of existing movies), date (date picker), time (time picker), available seats (number)
- Action buttons: Save, Delete, Cancel

| Route | Access | Notes |
|-------|--------|-------|
| `/admin/shows` | authenticated | Vaadin @Route("admin/shows") |
