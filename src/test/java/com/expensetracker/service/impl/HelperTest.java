package com.expensetracker.service.impl;

import com.expensetracker.model.dto.BudgetStatus;
import com.expensetracker.model.entity.Budget;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.utill.Helper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HelperTest {
   @Mock
   private ExpenseRepository expenseRepository;
    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private Helper helper;

    @Test
    void shouldReturnWithinBudgetWhenSpentIsLessThanBudget() {

        // Arrange
        Budget budget = new Budget();
        budget.setBudgetAmount(10000);

        // Act
        BudgetStatus result =
                helper.getBudgetStatus(budget, 6000);

        // Assert
        assertEquals(BudgetStatus.WITHIN_BUDGET, result);
    }

    @Test
    void shouldReturnWithinBudgetWhenSpentEqualsBudget() {

        // Arrange
        Budget budget = new Budget();
        budget.setBudgetAmount(10000);

        // Act
        BudgetStatus result =
                helper.getBudgetStatus(budget, 10000);

        // Assert
        assertEquals(BudgetStatus.WITHIN_BUDGET, result);
    }

    @Test
    void shouldReturnExceededWhenSpentIsGreaterThanBudget() {

        // Arrange
        Budget budget = new Budget();
        budget.setBudgetAmount(10000);

        // Act
        BudgetStatus result =
                helper.getBudgetStatus(budget, 12000);

        // Assert
        assertEquals(BudgetStatus.EXCEEDED, result);
    }

    @Test
    void shouldReturnNotSetWhenBudgetIsNull() {

        // Act
        BudgetStatus result =
                helper.getBudgetStatus(null, 6000);

        // Assert
        assertEquals(BudgetStatus.NOT_SET, result);
    }

}
