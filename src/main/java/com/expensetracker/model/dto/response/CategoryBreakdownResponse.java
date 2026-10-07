package com.expensetracker.model.dto.response;

import com.expensetracker.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBreakdownResponse {
    private Category category;

    private double totalAmount;

}
