package com.expensetracker.utill;

import com.expensetracker.model.dto.BudgetStatus;
import com.expensetracker.model.dto.response.ExpenseStatistics;
import com.expensetracker.model.dto.response.ExpenseSummary;
import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.Category;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.model.entity.User;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Component
public class Helper {
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public Helper(ExpenseRepository expenseRepository, BudgetRepository budgetRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }
   public User getCurrentUser(){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
       String email = authentication.getName();
       User currentUser=userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
       return currentUser;
   }


    public String getBudgetWarning(Budget budget, double spent) {
        double remaining = budget.getBudgetAmount() - spent;
        if (budget == null) {
            return "No budget has been set for this month.";

        } else if (spent > budget.getBudgetAmount()) {
            return "You have exceeded your budget by ₹" + Math.abs(remaining);

        } else {
            return "You have ₹" + remaining + " remaining in your budget.";

        }
    }

    public ExpenseStatistics setStatistics(YearMonth yearMonth) {

        List<Expense> expenses = getMonthlyExpenses(yearMonth);
        int totalTransaction = expenses.size();
        OptionalDouble average = expenses.stream().mapToDouble(Expense::getAmount).average();

        ExpenseSummary highestExpense = getHighestExpense(expenses);

        ExpenseSummary lowestExpense = expenses.stream().min(Comparator.comparing(Expense::getAmount)).map(
                expense -> new ExpenseSummary(expense.getDescription(), expense.getAmount())
        ).orElse(null);

        Map<Category, Double> categoryTotals = expenses.stream().collect(
                Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));


        double spent = expenses.stream().mapToDouble(Expense::getAmount).sum();
        return new ExpenseStatistics(spent, totalTransaction, average.orElse(0.0), highestExpense, lowestExpense, categoryTotals);
    }

    public String getTopCategory(List<Expense> expenses) {
        Map<Category, Double> categoryTotals = expenses.stream().collect(
                Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));
        String topCategory = categoryTotals.entrySet().stream().
                max(Comparator.comparing(Map.Entry::getValue)).
                map(entry -> entry.getKey().name()).orElse(null);
        return topCategory;
    }

    public ExpenseSummary getHighestExpense(List<Expense> expenses) {
        ExpenseSummary highestExpense = expenses.stream().max(Comparator.comparing(Expense::getAmount)).map(
                expense -> new ExpenseSummary(expense.getDescription(), expense.getAmount())
        ).orElse(null);
        return highestExpense;
    }

    public List<Expense> getMonthlyExpenses(YearMonth yearMonth) {

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return expenseRepository.findByExpenseDateBetweenAndUser(startDate, endDate,getCurrentUser());
    }

    public Budget getMonthlyBudgets(YearMonth yearMonth) {

        return budgetRepository.findByYearMonthAndUser(yearMonth.toString(),getCurrentUser()).orElse(null);
    }

    public double getTotalExpenseAmount(List<Expense> monthlyExpenses) {
        return monthlyExpenses.stream().mapToDouble(Expense::getAmount).sum();

    }

    public List<Expense> getYearlyExpenses(Year year) {

        LocalDate startDate = year.atDay(1);
        LocalDate endDate = year.atDay(year.length());

        return expenseRepository.findByExpenseDateBetweenAndUser(startDate, endDate,getCurrentUser());
    }

    public BudgetStatus getBudgetStatus(Budget budget, double spent) {

        if (budget == null) {
            return BudgetStatus.NOT_SET;
        }

        if (spent <= budget.getBudgetAmount()) {
            return BudgetStatus.WITHIN_BUDGET;
        }

        return BudgetStatus.EXCEEDED;
    }
}
