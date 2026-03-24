package com.example.specdriven.expense;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense submitExpense(BigDecimal amount, LocalDate date, ExpenseCategory category,
                                 String description, byte[] receiptData, String receiptFileName,
                                 String receiptContentType) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setReceiptData(receiptData);
        expense.setReceiptFileName(receiptFileName);
        expense.setReceiptContentType(receiptContentType);
        expense.setSubmittedBy(getCurrentUsername());
        expense.setStatus(ExpenseStatus.PENDING);

        return repository.save(expense);
    }

    public List<Expense> getMyExpenses() {
        return repository.findBySubmittedBy(getCurrentUsername());
    }

    public List<Expense> getPendingExpenses() {
        return repository.findByStatus(ExpenseStatus.PENDING);
    }

    public Expense approveExpense(Long expenseId) {
        Expense expense = repository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalStateException("Only pending expenses can be approved");
        }
        expense.setStatus(ExpenseStatus.APPROVED);
        return repository.save(expense);
    }

    public Expense rejectExpense(Long expenseId, String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Rejection comment is required");
        }
        Expense expense = repository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        if (expense.getStatus() != ExpenseStatus.PENDING) {
            throw new IllegalStateException("Only pending expenses can be rejected");
        }
        expense.setStatus(ExpenseStatus.REJECTED);
        expense.setRejectionComment(comment);
        return repository.save(expense);
    }

    public BigDecimal getTotalPendingAmount() {
        return repository.findByStatus(ExpenseStatus.PENDING).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getPendingCount() {
        return repository.findByStatus(ExpenseStatus.PENDING).size();
    }

    public BigDecimal getApprovedAmountThisMonth() {
        YearMonth currentMonth = YearMonth.now();
        return repository.findByStatus(ExpenseStatus.APPROVED).stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(currentMonth))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<ExpenseCategory, BigDecimal> getApprovedByCategory() {
        return repository.findByStatus(ExpenseStatus.APPROVED).stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));
    }

    public List<Expense> getAllExpenses() {
        return repository.findAll();
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
