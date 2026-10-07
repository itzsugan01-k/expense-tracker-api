package com.expensetracker.service.impl;

import com.expensetracker.exception.BudgetNotFoundException;
import com.expensetracker.model.dto.request.BudgetRequest;
import com.expensetracker.model.dto.request.BudgetResponse;
import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.User;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.utill.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceImplTest {
    private static final String EMAIL = "test@gmail.com";
    @Mock
    BudgetRepository budgetRepository;
    @Mock
    Helper helper;
    @InjectMocks
    BudgetServiceImpl budgetService;
    private BudgetRequest request;
    private User user;
    private Budget budget;

    @BeforeEach
    void setUp() {
        request = new BudgetRequest();
        request.setAmount(1000);
        request.setYearMonth(YearMonth.parse("2026-07"));

        user = new User();
        user.setId(1);
        user.setName("Suganya");
        user.setEmail(EMAIL);

        budget = new Budget();
        budget.setBudgetAmount(1000);
        budget.setId(1);
        budget.setYearMonth("2026-07");
        budget.setUser(user);

    }

    @Test
    void shouldUpdateExistingBudget() {
        request.setAmount(2000);
        when(helper.getCurrentUser()).thenReturn(user);
        when(budgetRepository.findByYearMonthAndUser(
                request.getYearMonth().toString(), user)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = budgetService.setBudget(request);
        assertEquals("Budget saved successfully", result);
        assertEquals(2000, budget.getBudgetAmount());
        verify(helper).getCurrentUser();
        verify(budgetRepository).findByYearMonthAndUser(request.getYearMonth().toString(), user);
        verify(budgetRepository).save(budget);
    }

    @Test
    void shouldCreateNewBudget() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(budgetRepository.findByYearMonthAndUser(
                request.getYearMonth().toString(), user)).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).
                thenAnswer(invocation -> invocation.getArgument(0));
        String result = budgetService.setBudget(request);
        assertEquals("Budget saved successfully", result);
        ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
        verify(budgetRepository).save(captor.capture());
        Budget savedBudget = captor.getValue();
        assertEquals(1000, savedBudget.getBudgetAmount());
        assertEquals("2026-07", savedBudget.getYearMonth());
        assertEquals(user, savedBudget.getUser());
        verify(budgetRepository).findByYearMonthAndUser(request.getYearMonth().toString(), user);
        verify(budgetRepository).save(savedBudget);
    }

    @Test
    void shouldGetMonthlyBudget() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(budgetRepository.findByYearMonthAndUser(
                request.getYearMonth().toString(), user)).thenReturn(Optional.of(budget));
        BudgetResponse result = budgetService.getBudget(request.getYearMonth());
        assertNotNull(budget);
        assertEquals(1000,result.getAmount());
        verify(helper).getCurrentUser();
        verify(budgetRepository).findByYearMonthAndUser(request.getYearMonth().toString(), user);
    }

    @Test
    void shouldReturnBudgetNotFound() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(budgetRepository.findByYearMonthAndUser(
                request.getYearMonth().toString(), user)).thenReturn(Optional.empty());
        BudgetNotFoundException exception = assertThrows(BudgetNotFoundException.class,
                () -> budgetService.getBudget(request.getYearMonth()));
        assertEquals(
                "Budget not found for 2026-07",
                exception.getMessage()
        );
        verify(helper).getCurrentUser();
        verify(budgetRepository).findByYearMonthAndUser(request.getYearMonth().toString(), user);
    }

    @Test
    void shouldDeleteBudget() {

        when(helper.getCurrentUser()).thenReturn(user);

        when(budgetRepository.findByYearMonthAndUser(
                request.getYearMonth().toString(), user))
                .thenReturn(Optional.of(budget));

        budgetService.deleteBudget(request.getYearMonth());

        verify(helper).getCurrentUser();

        verify(budgetRepository).findByYearMonthAndUser(
                request.getYearMonth().toString(), user);

        verify(budgetRepository).delete(budget);
    }
    @Test
    void shouldThrowExceptionWhenDeletingNonExistingBudget() {

        when(helper.getCurrentUser()).thenReturn(user);

        when(budgetRepository.findByYearMonthAndUser(
                request.getYearMonth().toString(), user))
                .thenReturn(Optional.empty());

        assertThrows(
                BudgetNotFoundException.class,
                () -> budgetService.deleteBudget(request.getYearMonth())
        );

        verify(budgetRepository, never()).delete(any(Budget.class));
    }
}
