# UC-006: Admin Layout & Navigation

**As an** admin, **I want** a shared navigation menu across all admin pages **so that** I can quickly switch between movies, shows, and bookings management.

**Status:** Draft
**Date:** 2026-03-05

---

## Main Flow

- I navigate to `/admin` (or any admin sub-page).
- I see a side navigation menu listing: Movies, Shows, Bookings.
- The current page is highlighted in the menu.
- I click a menu item to navigate to that admin page.
- The selected page loads in the main content area; the menu remains visible.

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | All admin pages share the same layout with a persistent side navigation |
| BR-02 | The menu highlights the currently active page |
| BR-03 | Navigating to `/admin` redirects to `/admin/movies` |
| BR-04 | Only authenticated admin users can see the layout and menu |

---

## Acceptance Criteria

- [ ] A shared layout wraps all `/admin/*` views with a side navigation menu
- [ ] Menu contains links to Movies, Shows, and Bookings
- [ ] The active page's menu item is visually highlighted
- [ ] Clicking a menu item navigates to the corresponding admin page
- [ ] Navigating to `/admin` redirects to `/admin/movies`
- [ ] The layout requires authentication — anonymous users are redirected to login

---

## UI / Routes

- Use Vaadin `AppLayout` with a side `DrawerToggle` and `SideNav` containing `SideNavItem` entries
- The layout is the parent layout for all admin views (`@Route(layout = AdminLayout.class)`)
- Each admin view keeps its existing route but gains the shared layout

| Route | Access | Notes |
|-------|--------|-------|
| `/admin` | authenticated | Redirects to `/admin/movies` |
| `/admin/movies` | authenticated | Existing view, wrapped in AdminLayout |
| `/admin/shows` | authenticated | Existing view, wrapped in AdminLayout |
| `/admin/bookings` | authenticated | Existing view, wrapped in AdminLayout |

---

## Testing

> List key test scenarios for UI unit tests. Each scenario maps to an acceptance criterion.

| Scenario | Steps | Expected |
|----------|-------|----------|
| Menu present | Navigate to any admin view | Side nav with Movies, Shows, Bookings links is visible |
| Navigation | Click "Shows" menu item from Movies view | Shows view loads, Shows item is highlighted |
| Active highlight | Navigate to `/admin/bookings` | Bookings menu item is highlighted |
| Default redirect | Navigate to `/admin` | Redirected to `/admin/movies` |
| Access control | Anonymous user navigates to `/admin` | Redirected to login |

