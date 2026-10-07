package com.expensetracker.controller;

import com.expensetracker.model.dto.response.CategoryBreakdownResponse;
import com.expensetracker.model.dto.response.DailySpendingResponse;
import com.expensetracker.model.dto.response.MonthlyTrendResponse;
import com.expensetracker.model.dto.response.YearlySummaryResponse;
import com.expensetracker.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/monthly-trend")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthWiseTrend(@RequestParam Year year){
        return ResponseEntity.ok(analyticsService.getMonthWiseTrend(year));
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<List<CategoryBreakdownResponse>> getCategoryBreakdown(@RequestParam YearMonth yearMonth){
        return ResponseEntity.ok(analyticsService.getCategoryBreakdown(yearMonth));
    }
    @GetMapping("/daily-spending")
    public ResponseEntity<List<DailySpendingResponse>> getDailySpending(@RequestParam YearMonth yearMonth){
        return ResponseEntity.ok(analyticsService.getDailySpending(yearMonth));
    }

    @GetMapping("/yearly-summary")
    public ResponseEntity<YearlySummaryResponse> getYearlySummary(@RequestParam Year year){
        return ResponseEntity.ok(analyticsService.getYearlySummary(year));
    }



}
