package com.expensetracker.service;

import com.expensetracker.model.dto.request.ExpenseFilterRequest;
import com.expensetracker.model.dto.request.ExpenseRequest;
import com.expensetracker.model.dto.response.AddExpenseResponse;
import com.expensetracker.model.dto.response.ExpenseResponse;
import com.expensetracker.model.dto.response.MonthlySummaryResponse;
import com.expensetracker.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ExpenseService {
    AddExpenseResponse addExpense(ExpenseRequest request);
    Page<ExpenseResponse> listAllExpense(Pageable pageable);
    ExpenseResponse getExpenseById(int id);

    ExpenseResponse updateExpense(int id, ExpenseRequest expenseRequest);

    void deleteExpense(int id);
    List<ExpenseResponse>searchByDescription(String description);

     List<ExpenseResponse> filterByCategory(Category category);


     List<ExpenseResponse> dateBetweenExpenses(LocalDate startDate, LocalDate endDate);

     MonthlySummaryResponse expenseStatistics(YearMonth yearMonth);

     Page<ExpenseResponse> filterExpenses(ExpenseFilterRequest request, Pageable pageable);

    ByteArrayInputStream exportCsv();

    ByteArrayInputStream  exportExcel() ;

    ByteArrayInputStream exportPdf();
}
