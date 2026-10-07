package com.expensetracker.service.impl;

import com.expensetracker.model.dto.response.CategoryBreakdownResponse;
import com.expensetracker.model.dto.response.DailySpendingResponse;
import com.expensetracker.model.dto.response.MonthlyTrendResponse;
import com.expensetracker.model.dto.response.YearlySummaryResponse;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.service.AnalyticsService;
import com.expensetracker.utill.Helper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    private final Helper helper;

    public AnalyticsServiceImpl(Helper helper) {
        this.helper = helper;
    }

    @Override
    public List<MonthlyTrendResponse> getMonthWiseTrend(Year year) {
        List<MonthlyTrendResponse> responses = new ArrayList<>();

        for (Month m : Month.values()) {
            YearMonth yearMonth = YearMonth.of(year.getValue(), m);
            List<Expense> monthlyExpenses = helper.getMonthlyExpenses(yearMonth);
            double totalExpense = helper.getTotalExpenseAmount(monthlyExpenses);
            responses.add(new MonthlyTrendResponse(yearMonth, totalExpense));
        }
        return responses;
    }


    @Override
    public List<CategoryBreakdownResponse> getCategoryBreakdown(YearMonth yearMonth) {

        List<Expense> monthlyExpenses = helper.getMonthlyExpenses(yearMonth);

        return monthlyExpenses.stream().collect(Collectors.groupingBy(Expense::getCategory,
                Collectors.summingDouble(Expense::getAmount))).entrySet().stream().map(
                entry -> new CategoryBreakdownResponse(entry.getKey(), entry.getValue())).toList();

    }

    @Override
    public List<DailySpendingResponse> getDailySpending(YearMonth yearMonth) {
        List<Expense> monthlyExpenses = helper.getMonthlyExpenses(yearMonth);
        return monthlyExpenses.stream().collect(Collectors.groupingBy(Expense::getExpenseDate,
                        Collectors.summingDouble(Expense::getAmount))).entrySet().stream()
                .sorted(Map.Entry.<LocalDate, Double>comparingByKey().reversed()).
                map(entry -> new DailySpendingResponse(entry.getKey(), entry.getValue())).toList();
    }

    @Override
    public YearlySummaryResponse getYearlySummary(Year year) {
        YearlySummaryResponse summaryResponse = new YearlySummaryResponse();
        List<Expense> monthlyExpenses = helper.getYearlyExpenses(year);
        double totalExpense = helper.getTotalExpenseAmount(monthlyExpenses);
        summaryResponse.setYear(year.getValue());
        summaryResponse.setHighestExpense(helper.getHighestExpense(monthlyExpenses));
        summaryResponse.setTotalTransactions(monthlyExpenses.size());
        summaryResponse.setTotalExpense(totalExpense);
        summaryResponse.setTopCategory(helper.getTopCategory(monthlyExpenses));
        summaryResponse.setAverageMonthlyExpense(totalExpense / 12);
        return summaryResponse;
    }
}
