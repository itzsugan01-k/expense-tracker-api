package com.expensetracker.controller;

import com.expensetracker.model.dto.request.BudgetRequest;
import com.expensetracker.model.dto.request.BudgetResponse;
import com.expensetracker.model.dto.response.BudgetSummary;
import com.expensetracker.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/budgets")
@Slf4j
@Validated
public  class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<String> setBudget(@RequestBody BudgetRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.setBudget(request));
    }

    @GetMapping("/year_month")
    public ResponseEntity<BudgetResponse> getBudget(@RequestParam YearMonth yearMonth){
        return ResponseEntity.ok(budgetService.getBudget(yearMonth));
    }
    @Operation(summary = "Delete Budget")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @DeleteMapping("/year_month")
    public ResponseEntity<Void> deleteBudget(@RequestParam YearMonth yearMonth){
        budgetService.deleteBudget(yearMonth);
        return  ResponseEntity.noContent().build();

    }

    @GetMapping("/summary")
    public ResponseEntity<BudgetSummary> buildBudgetSummary(@RequestParam YearMonth yearMonth){
        return ResponseEntity.ok(budgetService.buildBudgetSummary(yearMonth));
    }
}
