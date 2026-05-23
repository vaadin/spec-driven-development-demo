# UC-001: Order Flowers

---

**Goal:** As a visitor, I want to order flowers from a catalog so that they will be delivered at the event.

**Status:** Implemented
**Date:** 2026-05-23

> A use case cannot be marked as **Implemented** unless all criteria in the `/implement-use-case` skill are fulfilled.

---

## Actors

- **Primary actor:** Anonymous visitor

---

## Preconditions

- None. The catalog is hard-coded and no authentication is required.

---

## Trigger

Visitor navigates to `/`.

---

## Main Flow

1. Visitor navigates to `/`.
2. System displays the order page: a catalog grid (rows = flowers, columns = that flower's three colors, each cell is a quantity input defaulting to 0), a name field, a phone number field, and a "Confirm order" button.
3. Visitor enters a quantity greater than zero in one or more cells.
4. Visitor enters their name and phone number.
5. Visitor clicks "Confirm order".
6. System validates the submission: at least one quantity is greater than zero, name is non-empty, and phone number is non-empty.
7. System persists the order as an `Order` record with one `OrderLine` per non-zero cell.
8. System navigates to `/order/confirmation`.
9. System displays the message "Thank you for now".

---

## Alternative Flows

### AF-1: No flowers selected

**Branches from:** Main Flow step 6
**Condition:** All quantity cells are 0.

1. System shows an error indicating that at least one flower must be selected.
2. System preserves the entered name and phone number.
3. Returns to Main Flow step 3.

### AF-2: Missing contact info

**Branches from:** Main Flow step 6
**Condition:** Name is empty and/or phone number is empty.

1. System shows a field-level error on each empty required field.
2. System preserves the entered quantities and any populated contact field.
3. Returns to Main Flow step 4.

---

## Postconditions

- **On success:** A new `Order` is persisted with the visitor's name, phone number, and at least one `OrderLine` (flower, color, quantity). The visitor is on `/order/confirmation`.
- **On failure:** No `Order` is persisted. The form remains visible with validation errors and all entered data preserved.

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | The catalog is hard-coded with four flowers, each available in exactly three colors: Rose (Red, Pink, White), Tulip (Red, Yellow, Purple), Lily (White, Pink, Orange), Sunflower (Yellow, Orange, Red). |
| BR-02 | At least one quantity cell must be greater than zero for the order to be accepted. |
| BR-03 | Name is required and must be non-empty. |
| BR-04 | Phone number is required and must be non-empty. No format validation is performed. |
| BR-05 | Quantity per cell is an integer greater than or equal to zero, with no upper bound. |
| BR-06 | No authentication is required to place an order. |

---

## Tests

> Tests verify the flows and business rules above. There is no separate acceptance-criteria list — the flows and rules *are* the acceptance criteria. See `architecture.md` § Testing for conventions.

- [x] `OrderFlowersViewTest` (JUnit 5 + `browserless-test-junit6`)
- [x] Covers Main Flow (steps 2–9)
- [x] Covers AF-1, AF-2
- [x] Covers BR-02, BR-03, BR-04, BR-05
- [x] `OrderServiceTest` (JUnit 5)
- [x] `FlowerCatalogTest` covers BR-01 (catalog contents); `OrderServiceTest` covers persistence of `Order` + `OrderLine`

---

## UI / Routes

The order page is a single Vaadin Flow view. The catalog is rendered as a grid where each row is a flower and each column is one of that flower's three colors; each cell holds an integer quantity field defaulting to 0. Below the grid are a name field, a phone number field, and a "Confirm order" button. Validation errors are shown inline with the offending fields. On success the user is navigated to a separate confirmation view.

- Catalog grid: 4 rows × 3 columns of integer quantity inputs (per-flower colors as defined in BR-01).
- Form fields: name (text), phone number (text).
- Action: "Confirm order" button submits the form.
- Confirmation view: static message "Thank you for now".

| Route | Access | Notes |
|-------|--------|-------|
| `/` | public | Vaadin Flow `@Route("")`, `@AnonymousAllowed`. Order form. |
| `/order/confirmation` | public | Vaadin Flow `@Route("order/confirmation")`, `@AnonymousAllowed`. Thank-you message. |
