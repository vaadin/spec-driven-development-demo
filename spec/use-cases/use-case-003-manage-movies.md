# UC-003: Manage Movies

**As an** admin, **I want to** add, edit, and delete movies **so that** the cinema's catalog stays up to date.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I navigate to the admin movies page.
- I see a grid listing all movies with their title and duration.
- To add a movie, I click "Add Movie", fill in the form, and save.
- To edit a movie, I select it in the grid, modify fields in the form, and save.
- To delete a movie, I select it in the grid and click "Delete".
- The system confirms deletion before removing the movie.

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | All movie fields are required: title, description, poster image URL, duration |
| BR-02 | Duration must be a positive number (in minutes) |
| BR-03 | A movie with existing future shows cannot be deleted |
| BR-04 | Only authenticated admin users can access this view |

---

## Acceptance Criteria

- [ ] Admin can view a grid of all movies
- [ ] Admin can add a new movie with title, description, poster image URL, and duration
- [ ] Admin can edit an existing movie
- [ ] Admin can delete a movie that has no future shows
- [ ] Deleting a movie with future shows is prevented with an error message
- [ ] All fields are validated as required before saving
- [ ] Page requires authentication

---

## UI / Routes

- Master-detail layout: grid on the left, form on the right
- Grid columns: title, duration
- Form fields: title (text), description (text area), poster image URL (text), duration in minutes (number)
- Action buttons: Save, Delete, Cancel

| Route | Access | Notes |
|-------|--------|-------|
| `/admin/movies` | authenticated | Vaadin @Route("admin/movies") |
