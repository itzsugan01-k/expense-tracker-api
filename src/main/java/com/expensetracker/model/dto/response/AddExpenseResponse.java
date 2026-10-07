package com.expensetracker.model.dto.response;

import com.expensetracker.model.dto.BudgetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddExpenseResponse {

    private ExpenseResponse expense;

    private String warning;

    private BudgetStatus status;
}