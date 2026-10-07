package com.expensetracker.service;

import com.expensetracker.model.dto.response.DashboardResponse;

import java.time.YearMonth;

public interface DashboardService {
    DashboardResponse getDashboard(YearMonth yearMonth);
}
