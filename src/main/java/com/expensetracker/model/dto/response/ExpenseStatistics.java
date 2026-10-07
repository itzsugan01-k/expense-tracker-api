package com.expensetracker.model.dto.response;

import com.expensetracker.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseStatistics {

    private double totalSpent;

    private int totalTransactions;

    private double averageExpense;

    private ExpenseSummary highestExpense;

    private ExpenseSummary lowestExpense;

    private Map<Category, Double> categorySummary;

}