package com.expensetracker.exception;

import java.time.YearMonth;

public class BudgetNotFoundException extends RuntimeException{
    public BudgetNotFoundException(String yearMonth){
        super("Budget is not found with: "+yearMonth);
    }
}
