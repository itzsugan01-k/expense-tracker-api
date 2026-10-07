package com.expensetracker.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetResponse {
    private YearMonth yearMonth;
    private double amount;
}
