package com.expensetracker.model.dto.request;

import com.expensetracker.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseFilterRequest {

    private Category category;

    private String keyword;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double minAmount;

    private Double maxAmount;
}