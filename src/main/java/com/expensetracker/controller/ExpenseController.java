package com.expensetracker.controller;

import com.expensetracker.model.dto.request.ExpenseFilterRequest;
import com.expensetracker.model.dto.request.ExpenseRequest;
import com.expensetracker.model.dto.response.ExpenseResponse;
import com.expensetracker.model.dto.response.MonthlySummaryResponse;
import com.expensetracker.model.entity.Category;
import com.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@Slf4j
@Validated
@SecurityRequirement(name = "Bearer Authentication")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> addExpense(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.addExpense(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(expenseService.getExpenseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpenseById(@PathVariable int id, @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(expenseService.updateExpense(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> listAllExpenses( Pageable pageable) {
        System.out.println("Pageable = " + pageable);
        System.out.println("Sort = " + pageable.getSort());
        return ResponseEntity.ok(expenseService.listAllExpense(pageable));
    }
    @Operation(summary = "Delete Expense")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Expense deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExpenseById(@PathVariable int id){
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExpenseResponse>> searchByDescription(@RequestParam String description){
      return   ResponseEntity.ok(expenseService.searchByDescription(description));
    }

    @GetMapping("/category")
    public ResponseEntity<List<ExpenseResponse>> filterByCategory(@RequestParam Category category){
        return   ResponseEntity.ok(expenseService.filterByCategory(category));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ExpenseResponse>> dateBetweenExpenses(@RequestParam LocalDate startDate,@RequestParam LocalDate endDate){
        return   ResponseEntity.ok(expenseService.dateBetweenExpenses(startDate,endDate));
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryResponse> expenseStatistics(@RequestParam YearMonth yearMonth){
        return   ResponseEntity.ok(expenseService.expenseStatistics(yearMonth));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ExpenseResponse>> filterExpenses(ExpenseFilterRequest request,Pageable pageable){
        return   ResponseEntity.ok(expenseService.filterExpenses(request,pageable));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportCsv(){
         InputStreamResource resource =
                new InputStreamResource(expenseService.exportCsv());
        ContentDisposition contentDisposition= ContentDisposition.attachment().filename("expenses.csv").build();
         return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition.toString())
                 .contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportExcel()  {
        InputStreamResource resource =
                new InputStreamResource(expenseService.exportExcel());
        ContentDisposition contentDisposition= ContentDisposition.attachment().filename("expenses.xlsx").build();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition.toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(resource);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportPdf(){
        InputStreamResource resource =
                new InputStreamResource(expenseService.exportPdf());
        ContentDisposition contentDisposition= ContentDisposition.attachment().filename("expenses.pdf").build();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition.toString())
                .contentType(MediaType.APPLICATION_PDF).body(resource);
    }

}


