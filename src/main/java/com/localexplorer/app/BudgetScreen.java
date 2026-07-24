package com.localexplorer.app;

import com.localexplorer.dao.DatabaseConnection;
import com.localexplorer.models.Expense;
import com.localexplorer.utilities.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Budget screen: UI wiring + DB CRUD + chart logic all in one file.
 * No login, so expenses are tracked against a fixed demo user (id 1,
 * seeded in schema.sql).
 */
public class BudgetScreen {

    private static final int DEFAULT_USER_ID = 1;

    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField amountField;
    @FXML private TextField noteField;
    @FXML private PieChart spendingChart;
    @FXML private Label totalLabel;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        categoryBox.setItems(FXCollections.observableArrayList("Food", "Transport", "Shopping", "Entertainment"));
        categoryBox.setValue("Food");
        refreshChart();
    }

    @FXML
    private void addExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            boolean ok = insertExpense(new Expense(0, DEFAULT_USER_ID, categoryBox.getValue(), amount, noteField.getText(), LocalDate.now()));
            statusLabel.setText(ok ? "Expense added." : "Failed to add expense.");
            amountField.clear();
            noteField.clear();
            refreshChart();
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter a valid amount.");
        }
    }

    private void refreshChart() {
        List<Expense> expenses = fetchExpenses(DEFAULT_USER_ID);

        Map<String, Double> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        spendingChart.getData().clear();
        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            spendingChart.getData().add(new PieChart.Data(entry.getKey() + " (\u20B9" + entry.getValue() + ")", entry.getValue()));
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        totalLabel.setText("Total spent: \u20B9" + total);
    }

    // ---- Direct DB access (no DAO/Service indirection) ----

    private boolean insertExpense(Expense e) {
        String sql = "INSERT INTO expenses (user_id, category, amount, note, spent_on) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getUserId());
            ps.setString(2, e.getCategory());
            ps.setDouble(3, e.getAmount());
            ps.setString(4, e.getNote());
            ps.setDate(5, Date.valueOf(e.getSpentOn()));
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private List<Expense> fetchExpenses(int userId) {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ? ORDER BY spent_on DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Expense(
                            rs.getInt("expense_id"),
                            rs.getInt("user_id"),
                            rs.getString("category"),
                            rs.getDouble("amount"),
                            rs.getString("note"),
                            rs.getDate("spent_on").toLocalDate()
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @FXML
    private void backToDashboard() {
        SceneManager.switchTo("/com/localexplorer/fxml/Dashboard.fxml", "Local Explorer AI Pro - Dashboard");
    }
}
