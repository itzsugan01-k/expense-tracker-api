package com.expensetracker.model.dto.response;

import com.expensetracker.model.dto.BudgetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {

    private YearMonth yearMonth;

    private double budget;

    private double spent;

    private double remaining;

    private int totalTransactions;

    private double averageExpense;

    private ExpenseSummary highestExpense;

    private ExpenseSummary lowestExpense;

    private String topCategory;

    private BudgetStatus budgetStatus;

    private List<ExpenseResponse> recentExpenses;
}