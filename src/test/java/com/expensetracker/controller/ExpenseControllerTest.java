package com.expensetracker.controller;

import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.model.dto.BudgetStatus;
import com.expensetracker.model.dto.request.ExpenseRequest;
import com.expensetracker.model.dto.response.AddExpenseResponse;
import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.Category;
import com.expensetracker.security.CustomUserDetailsService;
import com.expensetracker.security.JwtService;
import com.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.expensetracker.model.dto.BudgetStatus.WITHIN_BUDGET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ExpenseService expenseService;
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldAddExpenseSuccessfully() throws Exception{
        AddExpenseResponse response=new AddExpenseResponse();
        response.setStatus(WITHIN_BUDGET);
        when(expenseService.addExpense(any(ExpenseRequest.class))).thenReturn(response);
        mockMvc.perform( post("/expenses")
                .contentType("application/json")
                .content("""
                            {
                                "description": "PIZZA",
                                "amount": 600,
                                "category": "FOOD",
                                "expenseDate": "2026-08-30"
                            }
                            """))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.status").value("WITHIN_BUDGET"));


        ArgumentCaptor<ExpenseRequest> captor=ArgumentCaptor.forClass(ExpenseRequest.class);
        verify(expenseService,times(1)).addExpense(captor.capture());
        ExpenseRequest actualRequest=captor.getValue();
        assertEquals("PIZZA", actualRequest.getDescription());
        assertEquals(600.0, actualRequest.getAmount());
        assertEquals(Category.FOOD, actualRequest.getCategory());
        assertEquals(LocalDate.of(2026, 8, 30), actualRequest.getExpenseDate());

    }


    @Test
    void shouldRejectExpenseWhenAmountIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/expenses")
                                .contentType("application/json")
                                .content("""
                                    {
                                        "description": "PIZZA",
                                        "amount": -600,
                                        "category": "FOOD",
                                        "expenseDate": "2026-08-30"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());
        verifyNoInteractions(expenseService);
    }

    @Test
    void shouldReturnNotFoundWhenExpenseNotFound()throws Exception{
        when(expenseService.addExpense(any(ExpenseRequest.class)))
                .thenThrow(new ExpenseNotFoundException(100));

        mockMvc.perform(
                        post("/expenses")
                                .contentType("application/json")
                                .content("""
                        {
                            "description": "PIZZA",
                            "amount": 600,
                            "category": "FOOD",
                            "expenseDate": "2026-08-30"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("expense not found for id : 100"));
    }

}

