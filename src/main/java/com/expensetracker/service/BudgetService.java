package com.expensetracker.service;

import com.expensetracker.model.dto.request.BudgetRequest;
import com.expensetracker.model.dto.request.BudgetResponse;
import com.expensetracker.model.dto.response.BudgetSummary;

import java.time.YearMonth;

public interface BudgetService {
    String setBudget(BudgetRequest request);

    BudgetResponse getBudget(YearMonth yearMonth);

     void deleteBudget(YearMonth yearMonth);

     BudgetSummary buildBudgetSummary(YearMonth yearMonth);
}
