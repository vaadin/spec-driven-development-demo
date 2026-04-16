# UC-005: Dashboard

**As a** warehouse team member, **I want to** see an at-a-glance overview of inventory health **so that** I can spot problems quickly without digging through individual records.

**Status:** Pending
**Date:** 2026-03-23

---

## Main Flow

- I open the application (or navigate to the Dashboard).
- I see three dashboard widgets:
  1. **Stock KPIs** — displays Total Products count and Total Stock Value as KPIs, plus a combo chart showing the last 12 months trend (Total Products as columns, Total Value as area)
  2. **Inventory Health** — displays Low Stock count and Out of Stock count as KPIs, plus a prioritized list of products requiring attention
  3. **Recent Shipments** — displays the most recent inbound shipments with their status (Pending or Received)

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | Total Stock Value = sum of (currentStock × unitPrice) across all products |
| BR-02 | Low-Stock Items count = number of products where currentStock > 0 and currentStock ≤ reorderPoint |
| BR-03 | Out-of-Stock Items count = number of products where currentStock = 0 |
| BR-04 | Stock KPIs chart shows the last 12 months of data, with Total Products as column chart and Total Value as area chart overlaid |
| BR-05 | Inventory Health list is prioritized with Out-of-Stock items first, then Low-Stock items |
| BR-06 | Within Out-of-Stock items, priority is given to items with the lowest stock balance (which can be negative) and furthest refill date in the future |
| BR-07 | Within Low-Stock items, priority is given to items with stock closest to zero and nearest refill date |
| BR-08 | Inventory Health list displays: product name, SKU, current stock quantity, refill date, unit price, category, and status badge |
| BR-09 | Recent Shipments shows the last 10 shipments, ordered by most recent first |
| BR-10 | Each shipment displays: date, supplier name, shipment number, number of distinct products, total number of items, and status (Pending or Received) |
| BR-11 | Pending shipments have higher visual prominence than Received shipments |
| BR-12 | If there are new shipments since the user's last visit, display a personalized greeting message (e.g., "Hello [Firstname]! There's X new shipments since yesterday.") |
| BR-13 | Any authenticated user can view the dashboard |

---

## Acceptance Criteria

- [ ] The dashboard displays three widgets: Stock KPIs, Inventory Health, and Recent Shipments
- [ ] Stock KPIs widget shows Total Products and Total Value as color-coded KPIs
- [ ] Stock KPIs widget displays a combo chart with 12 months of historical data (columns for products, area for value)
- [ ] Total Stock Value is calculated correctly as sum of (stock × price)
- [ ] Inventory Health widget shows Low Stock and Out of Stock counts as KPIs
- [ ] Inventory Health widget displays a prioritized list with Out-of-Stock items shown first (with red indication)
- [ ] Items in the Inventory Health list are ordered by urgency (lowest stock and furthest refill date first)
- [ ] Each inventory item shows: name, SKU, stock quantity, refill date, unit price, category, and status badge
- [ ] Recent Shipments widget shows the 10 most recent shipments, newest first
- [ ] Each shipment shows: date, supplier, shipment number, product count, item count, and status
- [ ] Pending shipments are visually distinguished from Received shipments
- [ ] A personalized greeting is displayed (e.g., "Hello Firstname!") if there are new shipments since the user's last visit
- [ ] The dashboard is the default landing page after login
- [ ] Both admin and non-admin users can view the dashboard

---

## Tests

> Write UI tests that verify the acceptance criteria above. See [`skills/use-case-tests.md`](../skills/use-case-tests.md) for conventions.

- [ ] `DashboardTest` — browserless tests covering summary card values, low-stock alerts, recent activity, and access control

---

## UI / Routes

- **Desktop layout:** Three-column grid with widgets side by side
- **Mobile layout:** Single column, widgets stacked vertically
- **Greeting message:** If there are new shipments since the user's last visit, display a small notification box with personalized greeting (e.g., "Hello Firstname! There's 3 new shipments since yesterday.")

### Widget 1: Stock KPIs
- KPI display: Total Products (teal indicator) and Total Value (amber indicator)
- Chart: Combo chart showing "Last 12 months" with columns (Total Products) and area (Total Value)

### Widget 2: Inventory Health
- KPI display: Low Stock count (amber) and Out of Stock count (red, prominent)
- List view: Product cards showing name, SKU, stock level, refill date, unit price, category, and status badge
- Sorting: Out of Stock first (red badges), then Low Stock (amber badges), prioritized by urgency

### Widget 3: Recent Shipments
- List view: Shipment cards showing date, supplier, shipment number, product/item counts, and status
- Status indicators: "Pending" (amber) or "Received" (green)
- Icon: Package/box icon for each shipment

| Route | Access | Notes |
|-------|--------|-------|
| `/` | authenticated | Vaadin Flow @Route, default landing page |
