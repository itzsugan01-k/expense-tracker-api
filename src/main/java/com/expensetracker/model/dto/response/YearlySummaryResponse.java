package com.expensetracker.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearlySummaryResponse {

    private int year;

    private double totalExpense;

    private double averageMonthlyExpense;

    private int totalTransactions;

    private String topCategory;

    private ExpenseSummary highestExpense;
}