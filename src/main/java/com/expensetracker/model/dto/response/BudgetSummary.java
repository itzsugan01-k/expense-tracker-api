package com.expensetracker.model.dto.response;

import com.expensetracker.model.dto.BudgetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetSummary {
    private YearMonth yearMonth;

    private double budget;

    private double spent;

    private double remaining;

    private BudgetStatus status;
    private double percentageUsed;

    public BudgetSummary(YearMonth yearMonth, double budgetAmount) {
        this.yearMonth = yearMonth;
        this.budget = budgetAmount;
    }
}
