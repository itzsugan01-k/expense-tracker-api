package com.expensetracker.service.impl;

import com.expensetracker.mapper.ExpenseMapper;
import com.expensetracker.model.dto.response.DashboardResponse;
import com.expensetracker.model.dto.response.ExpenseResponse;
import com.expensetracker.model.dto.response.ExpenseStatistics;
import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.service.DashboardService;
import com.expensetracker.utill.Helper;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final Helper helper;

    public DashboardServiceImpl(ExpenseRepository expenseRepository, BudgetRepository budgetRepository, Helper helper) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.helper = helper;
    }

    @Override
    public DashboardResponse getDashboard(YearMonth yearMonth) {

        DashboardResponse dashboardResponse = new DashboardResponse();

        ExpenseStatistics statistics = helper.setStatistics(yearMonth);
        List<Expense>expenses=helper.getMonthlyExpenses(yearMonth);


        List<ExpenseResponse> recentExpenses = expenses.stream().
                sorted(Comparator.comparing(Expense::getExpenseDate).reversed()).
                limit(5).map(expense -> ExpenseMapper.toResponse(expense)).toList();


        Budget budget = helper.getMonthlyBudgets(yearMonth);

        double remaining = budget!= null? (budget.getBudgetAmount() - statistics.getTotalSpent()) :0;
        double budgetAmount=budget!= null? budget.getBudgetAmount() : 0;

        dashboardResponse.setBudgetStatus(helper.getBudgetStatus(budget,statistics.getTotalSpent()));

        dashboardResponse.setYearMonth(yearMonth);
        dashboardResponse.setHighestExpense(statistics.getHighestExpense());
        dashboardResponse.setLowestExpense(statistics.getLowestExpense());
        dashboardResponse.setAverageExpense(statistics.getAverageExpense());
        dashboardResponse.setRemaining(remaining);
        dashboardResponse.setSpent(statistics.getTotalSpent());
        dashboardResponse.setBudget(budgetAmount);
        dashboardResponse.setTopCategory(helper.getTopCategory(expenses));
        dashboardResponse.setRecentExpenses(recentExpenses);
        dashboardResponse.setTotalTransactions(statistics.getTotalTransactions());
        return dashboardResponse;
    }


}
