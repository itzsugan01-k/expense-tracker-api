package com.expensetracker.mapper;

import com.expensetracker.model.dto.request.BudgetRequest;
import com.expensetracker.model.dto.request.BudgetResponse;
import com.expensetracker.model.dto.response.BudgetSummary;
import com.expensetracker.model.entity.Budget;

import java.time.YearMonth;

public class BudgetMapper {
    public static Budget toEntity(BudgetRequest request){
        return new Budget(request.getYearMonth().toString(),request.getAmount());
    }

    public static BudgetSummary toResponseSummary(Budget budget){
        return new BudgetSummary(YearMonth.parse(budget.getYearMonth()), budget.getBudgetAmount());
    }

    public static BudgetResponse toResponse(Budget budget){
        return new BudgetResponse(YearMonth.parse(budget.getYearMonth()), budget.getBudgetAmount());
    }

}
