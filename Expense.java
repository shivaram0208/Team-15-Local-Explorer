package com.localexplorer.models;

import java.time.LocalDate;

public class Expense {
    private int expenseId;
    private int userId;
    private String category;
    private double amount;
    private String note;
    private LocalDate spentOn;

    public Expense() {}

    public Expense(int expenseId, int userId, String category, double amount, String note, LocalDate spentOn) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.note = note;
        this.spentOn = spentOn;
    }

    public int getExpenseId() { return expenseId; }
    public void setExpenseId(int expenseId) { this.expenseId = expenseId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDate getSpentOn() { return spentOn; }
    public void setSpentOn(LocalDate spentOn) { this.spentOn = spentOn; }
}
