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

## Implementation

This view is implemented as a **React (Hilla) view** with a `@BrowserCallable` service.

**Frontend:** `src/main/frontend/views/@index.tsx` — React component using Vaadin React components
**Backend:** `@BrowserCallable` + `@AnonymousAllowed` service that returns today's movies with their show times

The service provides a method to fetch movies showing today. Vaadin generates TypeScript client code from the `@BrowserCallable` service, giving the React view type-safe access to the backend.

---

## UI / Routes

- Card layout listing today's movies
- Each card contains: poster image, movie title, list of show times
- Clicking a card uses `useNavigate` to go to the movie details view

| Route | Access | Notes |
|-------|--------|-------|
| `/` | public | Hilla file-based route (`views/@index.tsx`), replaces existing placeholder view |
