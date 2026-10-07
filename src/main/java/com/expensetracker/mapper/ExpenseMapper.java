package com.expensetracker.mapper;

import com.expensetracker.model.dto.request.ExpenseRequest;
import com.expensetracker.model.dto.response.ExpenseResponse;
import com.expensetracker.model.entity.Expense;

public class ExpenseMapper {
    public static Expense toEntity(ExpenseRequest request){
        return new Expense(request.getDescription(), request.getAmount(), request.getCategory(),request.getExpenseDate());
    }

    public static ExpenseResponse toResponse(Expense expense){
        ExpenseResponse response=new ExpenseResponse(expense.getId(), expense.getDescription(),
                expense.getAmount(), expense.getCategory(),expense.getExpenseDate(),expense.getCreatedAt(),expense.getUpdatedAt());
       return response;
    }
}
