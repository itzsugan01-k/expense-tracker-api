package com.expensetracker.model.dto.request;

import com.expensetracker.model.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequest {
    @NotBlank(message = "Description is required")
    private String description;
    @Positive(message = "amount must be greater than zero")
    private double amount;
    @NotNull(message = "Category is required")
    private Category category;
    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;
}
