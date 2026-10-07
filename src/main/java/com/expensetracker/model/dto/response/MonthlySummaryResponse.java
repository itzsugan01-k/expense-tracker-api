package com.expensetracker.model.dto.response;

import com.expensetracker.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummaryResponse {
    private YearMonth yearMonth;
    private double totalExpense;
    private double averageExpense;
    private int totalTransactions;
    private Map<Category,Double> categorySummary;
    private ExpenseSummary highestExpense;
    private ExpenseSummary lowestExpense;

}
