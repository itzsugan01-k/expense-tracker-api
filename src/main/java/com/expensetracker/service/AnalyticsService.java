package com.expensetracker.service;

import com.expensetracker.model.dto.response.CategoryBreakdownResponse;
import com.expensetracker.model.dto.response.DailySpendingResponse;
import com.expensetracker.model.dto.response.MonthlyTrendResponse;
import com.expensetracker.model.dto.response.YearlySummaryResponse;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public interface AnalyticsService {
    List<MonthlyTrendResponse> getMonthWiseTrend(Year year);

    List<CategoryBreakdownResponse> getCategoryBreakdown(YearMonth yearMonth);

    List<DailySpendingResponse> getDailySpending(YearMonth yearMonth);

    YearlySummaryResponse getYearlySummary(Year year);
}
