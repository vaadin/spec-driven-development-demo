package com.example.specdriven.expense;

public enum ExpenseCategory {
    TRAVEL("Travel"),
    MEALS("Meals"),
    OFFICE_SUPPLIES("Office Supplies"),
    OTHER("Other");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
