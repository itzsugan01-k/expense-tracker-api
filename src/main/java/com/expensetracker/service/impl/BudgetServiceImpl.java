package com.expensetracker.service.impl;

import com.expensetracker.exception.BudgetNotFoundException;
import com.expensetracker.mapper.BudgetMapper;
import com.expensetracker.model.dto.request.BudgetRequest;
import com.expensetracker.model.dto.request.BudgetResponse;
import com.expensetracker.model.dto.response.BudgetSummary;
import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.service.BudgetService;
import com.expensetracker.utill.Helper;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final Helper helper;

    public BudgetServiceImpl(BudgetRepository budgetRepository,Helper helper) {
        this.budgetRepository = budgetRepository;
        this.helper = helper;
    }

    @Override
    public String setBudget(BudgetRequest request) {
        Optional<Budget> budgetOptional = budgetRepository.findByYearMonthAndUser(
                request.getYearMonth().toString(),helper.getCurrentUser());

        if (budgetOptional.isPresent()) {
            Budget budget = budgetOptional.get();
            budget.setBudgetAmount(request.getAmount());
            budgetRepository.save(budget);
        } else {
            Budget budget = BudgetMapper.toEntity(request);
            budget.setUser(helper.getCurrentUser());
            budgetRepository.save(budget);
        }

        return "Budget saved successfully";
    }

    @Override
    public BudgetResponse getBudget(YearMonth yearMonth) {
        return BudgetMapper.toResponse(getMonthBudget(yearMonth));
    }

    private Budget getMonthBudget(YearMonth yearMonth) {
        Budget budget = budgetRepository.findByYearMonthAndUser(
                yearMonth.toString(),helper.getCurrentUser()).orElseThrow(() -> new BudgetNotFoundException(yearMonth.toString()));
        return budget;
    }

    @Override
    public void deleteBudget(YearMonth yearMonth) {
        budgetRepository.delete(getMonthBudget(yearMonth));

    }

    @Override
    public BudgetSummary buildBudgetSummary(YearMonth yearMonth) {

        BudgetSummary budgetSummary = new BudgetSummary();
        Budget budget = getMonthBudget(yearMonth);
        List<Expense> expenses = helper.getMonthlyExpenses(yearMonth);
        double spent = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double remaining = budget.getBudgetAmount() - spent;
        double percentageUsed = budget.getBudgetAmount() == 0
                ? 0 : (spent / budget.getBudgetAmount()) * 100;

        budgetSummary.setStatus(helper.getBudgetStatus(budget,spent));

        budgetSummary.setBudget(budget.getBudgetAmount());
        budgetSummary.setRemaining(remaining);
        budgetSummary.setSpent(spent);
        budgetSummary.setPercentageUsed(percentageUsed);
        budgetSummary.setYearMonth(yearMonth);

        return budgetSummary;
    }
}
