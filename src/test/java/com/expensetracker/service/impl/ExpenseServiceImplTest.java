package com.expensetracker.service.impl;

import com.expensetracker.exception.BudgetNotFoundException;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.exception.ExportException;
import com.expensetracker.exception.UserNameNotFoundException;
import com.expensetracker.model.dto.BudgetStatus;
import com.expensetracker.model.dto.request.ExpenseRequest;
import com.expensetracker.model.dto.response.AddExpenseResponse;
import com.expensetracker.model.dto.response.ExpenseResponse;
import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.Category;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.model.entity.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.utill.Helper;
import com.lowagie.text.pdf.PdfReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceImplTest {
    private static final int USER_ID = 1;
    private static final int EXPENSE_ID = 1;
    private static final String EMAIL = "test@gmail.com";
    @Mock
    ExpenseRepository expenseRepository;
    @Mock
    Helper helper;
    @InjectMocks
    ExpenseServiceImpl expenseService;
    private User user;
    private Expense expense;
    private ExpenseRequest request;
    private Budget budget;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(USER_ID);
        user.setName("Suganya");
        user.setEmail(EMAIL);

        request = new ExpenseRequest();
        request.setDescription("PIZZA");
        request.setAmount(600);
        request.setCategory(Category.FOOD);
        request.setExpenseDate(LocalDate.now());

        expense = new Expense();
        expense.setId(EXPENSE_ID);
        expense.setDescription("PIZZA");
        expense.setAmount(600);
        expense.setCategory(Category.FOOD);
        expense.setExpenseDate(LocalDate.now());
        expense.setUser(user);

        budget = new Budget();
        budget.setBudgetAmount(10000);
    }

    @Test
    void shouldAddExpenseSuccessfully() {


        List<Expense> expenses = List.of(expense);

        when(helper.getCurrentUser())
                .thenReturn(user);
        when(helper.getMonthlyExpenses(any())).thenReturn(expenses);
        when(helper.getMonthlyBudgets(any())).thenReturn(budget);
        when(helper.getBudgetStatus(any(), anyDouble())).thenReturn(BudgetStatus.WITHIN_BUDGET);
        when(expenseRepository.save(any(Expense.class))).
                thenAnswer(invocation -> invocation.getArgument(0));

        AddExpenseResponse addResponse = expenseService.addExpense(request);
        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());
        Expense savedExpense = captor.getValue();
        assertEquals("PIZZA", savedExpense.getDescription());
        assertEquals(600, savedExpense.getAmount());
        assertEquals(user, savedExpense.getUser());

        assertNotNull(addResponse);
        assertEquals(BudgetStatus.WITHIN_BUDGET, addResponse.getStatus());
        assertNotNull(addResponse.getExpense());
        assertEquals("PIZZA", addResponse.getExpense().getDescription());
        assertEquals(600, addResponse.getExpense().getAmount());

        verify(expenseRepository).save(any(Expense.class));
        verify(helper).getMonthlyExpenses(any());
        verify(helper).getMonthlyBudgets(any());
        verify(helper).getCurrentUser();

    }

    @Test
    void shouldThrowExceptionWhenCurrentUserNotFound() {

        when(helper.getCurrentUser()).thenThrow(new UserNameNotFoundException("User not found"));
        assertThrows(UserNameNotFoundException.class, () -> expenseService.addExpense(request));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenBudgetNotFound() {

        when(helper.getMonthlyBudgets(any())).thenThrow(new BudgetNotFoundException("Budget not found"));
        assertThrows(BudgetNotFoundException.class, () -> expenseService.addExpense(request));
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void shouldThrowExceptionWhenDatabaseSaveFails() {

        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.save(any(Expense.class))).thenThrow(new RuntimeException("Database Error"));
        RuntimeException exception= assertThrows(RuntimeException.class, () -> expenseService.addExpense(request));
        assertEquals("Database Error", exception.getMessage());
        verify(helper).getCurrentUser();
        verify(expenseRepository).save(any(Expense.class));
        verify(helper, never()).getMonthlyBudgets(any());
        verify(helper, never()).getMonthlyExpenses(any());
        verify(helper, never()).getBudgetStatus(any(), anyDouble());
    }

    @Test
    void shouldGetExpenseById() {

        when(expenseRepository.findByIdAndUser(1, user)).thenReturn(Optional.of(expense));
        when(helper.getCurrentUser()).thenReturn(user);
        ExpenseResponse response = expenseService.getExpenseById(1);
        assertNotNull(response);
        assertEquals("PIZZA", response.getDescription());
        assertEquals(600, response.getAmount());
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByIdAndUser(1, user);
    }

    @Test
    void shouldThrowExpenseNotFound() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByIdAndUser(1, user)).
                thenReturn(Optional.empty());
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.getExpenseById(1));
        verify(expenseRepository).findByIdAndUser(1, user);
    }

    @Test
    void shouldUpdateExpense() {
        request.setDescription("MAGGI");
        request.setAmount(500);
        request.setExpenseDate(LocalDate.parse("2026-07-05"));

        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByIdAndUser
                (1, user)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).
                thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse response = expenseService.updateExpense(1, request);
        assertNotNull(response);
        assertEquals("MAGGI", response.getDescription());
        assertEquals(500, response.getAmount());

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());
        Expense savedExpense = captor.getValue();
        assertEquals("MAGGI", savedExpense.getDescription());
        assertEquals(500, savedExpense.getAmount());
        assertEquals(Category.FOOD, savedExpense.getCategory());
        assertEquals(LocalDate.parse("2026-07-05"),
                savedExpense.getExpenseDate());
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByIdAndUser(1, user);


    }

    @Test
    void shouldThrowUpdateExpenseNotFound() {

        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByIdAndUser(1, user)).
                thenReturn(Optional.empty());
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.updateExpense(1, request));
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByIdAndUser(1, user);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldDeleteExpense() {

        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByIdAndUser(1, user)).thenReturn(Optional.of(expense));
        expenseService.deleteExpense(1);
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByIdAndUser(1, user);
        verify(expenseRepository).delete(expense);
    }

    @Test
    void shouldThrowDeleteExpenseNotFound() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByIdAndUser(1, user)).
                thenReturn(Optional.empty());
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.deleteExpense(1));
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByIdAndUser(1, user);
        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    void shouldSearchByDescription() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByDescriptionContainingIgnoreCaseAndUser(
                "PIZZA", user)).thenReturn(List.of(expense));
        List<ExpenseResponse> responses = expenseService.searchByDescription("PIZZA");
        assertEquals(1, responses.size());
        assertEquals("PIZZA", responses.get(0).getDescription());
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByDescriptionContainingIgnoreCaseAndUser("PIZZA", user);
    }

    @Test
    void shouldReturnEmptyListWhenDescriptionNotFound() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByDescriptionContainingIgnoreCaseAndUser(
                "Burger", user)).thenReturn(Collections.emptyList());
        List<ExpenseResponse> responses = expenseService.searchByDescription("Burger");
        assertTrue(responses.isEmpty());
        verify(expenseRepository).findByDescriptionContainingIgnoreCaseAndUser("Burger", user);
    }

    @Test
    void shouldListAllExpense() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Expense> expensePage = new PageImpl<>(List.of(expense));
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUser(user, pageable)).thenReturn(expensePage);
        Page<ExpenseResponse> responses = expenseService.listAllExpense(pageable);
        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        ExpenseResponse response = responses.getContent().get(0);
        assertEquals("PIZZA", response.getDescription());
        assertEquals(600, response.getAmount());
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByUser(user, pageable);

    }

    @Test
    void shouldReturnEmptyExpense() {
        Pageable pageable = PageRequest.of(0, 5);
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUser(user, pageable)).thenReturn(Page.empty());
        Page<ExpenseResponse> responses = expenseService.listAllExpense(pageable);
        assertTrue(responses.isEmpty());
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByUser(user, pageable);
    }

    @Test
    void shouldExportCsv() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUser(user)).thenReturn(List.of(expense));
        ByteArrayInputStream result = expenseService.exportCsv();
        assertNotNull(result);
        String csv = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(csv.contains("Id,Description,Amount,Category,Expense Date"));
        assertTrue(csv.contains("1,PIZZA,600.0,FOOD," + expense.getExpenseDate()));
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByUser(user);

    }

    @Test
    void shouldReturnNoExpenseCsv() {
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUser(user)).thenReturn(Collections.emptyList());
        ByteArrayInputStream result = expenseService.exportCsv();
        assertNotNull(result);
        String csv = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("Id,Description,Amount,Category,Expense Date" + System.lineSeparator()
                , csv);
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByUser(user);
    }

    @Test
    void shouldExportExpenseToPdf() throws Exception {
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUser(user)).thenReturn(List.of(expense));
        ByteArrayInputStream result = expenseService.exportPdf();
        assertNotNull(result);
        byte[] pdfBytes = result.readAllBytes();
        assertTrue(pdfBytes.length > 0);

        assertEquals('%', pdfBytes[0]);
        assertEquals('P', pdfBytes[1]);
        assertEquals('D', pdfBytes[2]);
        assertEquals('F', pdfBytes[3]);
        PdfReader reader = new PdfReader(pdfBytes);
        assertEquals(1, reader.getNumberOfPages());
        reader.close();
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByUser(user);

    }

    @Test
    void shouldThrowExportPdfFileGenerationFail() {
        Expense invalidExpense = new Expense();
        invalidExpense.setId(1);
        invalidExpense.setDescription("Food");
        invalidExpense.setAmount(600);
        invalidExpense.setCategory(null);
        invalidExpense.setExpenseDate(LocalDate.now());
        when(helper.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUser(user)).thenReturn(List.of(invalidExpense));
        ExportException exportException = assertThrows(ExportException.class,
                () -> expenseService.exportPdf());
        assertEquals("Unable to export file", exportException.getMessage());
        verify(helper).getCurrentUser();
        verify(expenseRepository).findByUser(user);
    }


}



