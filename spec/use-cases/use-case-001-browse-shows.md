# UC-001: Browse Shows

> Browse the list of available shows to find one to attend.

---

**As a** Customer, **I want to** see a list of available shows with cover images, titles, and dates **so that** I can quickly find something I want to attend.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I open the application and land on the show listing page
- I see a grid/list of available shows, each displaying a cover image, title, date/time, and price
- Shows that are sold out are visually distinguished but still visible
- I click on a show to navigate to its detail page

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | All shows are visible regardless of availability |
| BR-02 | Sold-out shows are visually marked and cannot be selected for purchase |
| BR-03 | Shows are ordered by date/time (soonest first) |

---

## Acceptance Criteria

- [ ] The show list displays title, cover image, date/time, and price for each show
- [ ] Sold-out shows are visually distinguished (e.g., greyed out or labelled)
- [ ] Clicking an available show navigates to the detail page for that show
- [ ] Clicking a sold-out show does not navigate to the detail page
- [ ] The page is responsive and usable on both desktop and mobile

---

## UI / Routes

- Card-based grid layout; each card shows cover image, title, date/time, and price
- Responsive: 1 column on mobile, 2–3 columns on desktop

| Route | Access | Notes |
|-------|--------|-------|
| `/` | public | Vaadin @Route — ShowListView |
