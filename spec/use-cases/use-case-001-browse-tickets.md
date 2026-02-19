# UC-001: Browse Tickets

> Browse available transit tickets with filtering by transit mode.

---

**As a** rider, **I want to** browse available tickets by transit mode **so that** I can quickly find the ticket I need.

**Status:** Draft
**Date:** 2026-02-19

---

## Main Flow

- I open the app and land on the browse page
- I see a grid of ticket cards showing all available tickets
- Each card displays the ticket name, transit mode, ticket type (single ride or day pass), and price
- I see filter buttons at the top for each transit mode: Bus, Train, Metro, Ferry, and an "All" option
- I tap a filter button (e.g., "Bus") and the grid updates to show only tickets for that mode
- I tap "All" to see all tickets again
- I tap on a ticket card to navigate to the detail page for that ticket

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | All transit modes must be represented in the seed data (Bus, Train, Metro, Ferry) |
| BR-02 | Each transit mode has both a single-ride and a day-pass ticket |
| BR-03 | The "All" filter is selected by default on page load |
| BR-04 | Ticket cards are tappable — clicking navigates to the detail page |

---

## Acceptance Criteria

- [ ] Browse page loads and displays all seeded tickets as cards in a grid
- [ ] Filter buttons for All, Bus, Train, Metro, and Ferry are visible
- [ ] Selecting a mode filter shows only tickets for that mode
- [ ] Selecting "All" shows all tickets
- [ ] Each card shows ticket name, transit mode, ticket type, and price
- [ ] Tapping a card navigates to the ticket detail page (UC-002)
- [ ] Layout is responsive and usable on mobile screens

---

## UI / Routes

- Card grid layout, responsive (1 column on mobile, 2+ on wider screens)
- Filter bar at the top with toggle buttons for each transit mode + "All"
- Each card is a clickable unit that navigates to the detail view

| Route | Access | Notes |
|-------|--------|-------|
| `/` | public | Vaadin @Route — main landing page |
