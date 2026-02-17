# UC-001: Browse Today's Movies

**As a** visitor, **I want to** see which movies are showing today **so that** I can pick one to watch.

**Status:** Draft
**Date:** 2026-02-17

---

## Main Flow

- I open the home page.
- I see a list of movies that have screenings scheduled for today.
- Each movie shows its title, poster image, and today's show times.
- I click on a movie to navigate to its details view.

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | Only movies with at least one screening today are shown |
| BR-02 | Show times are displayed in chronological order |
| BR-03 | Past show times for today are still visible but marked as past |

---

## Acceptance Criteria

- [ ] Home page displays movies with screenings scheduled for today
- [ ] Each movie entry shows title, poster image, and show times
- [ ] Movies with no screenings today are not shown
- [ ] Clicking a movie navigates to `/movie/{movieId}`
- [ ] Page is accessible without authentication

---

## UI / Routes

- Grid or card layout listing today's movies
- Each card contains: poster image, movie title, list of show times
- Clicking a card navigates to the movie details view

| Route | Access | Notes |
|-------|--------|-------|
| `/` | public | Vaadin @Route(""), replaces existing placeholder view |
