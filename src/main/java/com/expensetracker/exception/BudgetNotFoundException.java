package com.expensetracker.exception;



public class BudgetNotFoundException extends RuntimeException{
    public BudgetNotFoundException(String yearMonth){
        super("Budget not found for " + yearMonth);
    }
}
