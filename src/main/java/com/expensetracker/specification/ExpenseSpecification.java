package com.expensetracker.specification;

import com.expensetracker.model.entity.Category;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;


public class ExpenseSpecification {


    public static Specification<Expense> hasCategory(Category category) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"),category));
    }
    public static Specification<Expense> hasUser(User user) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user"),user));
    }
    public static Specification<Expense> hasKeyword(String description) {
        String pattern="%" +description.toLowerCase() +"%";
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")) ,pattern));
    }

    public static Specification<Expense> amountGreaterThanOrEqualTo(Double minAmount){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("amount"),minAmount));
    }

    public static Specification<Expense> amountLessThanOrEqualTo(Double maxAmount){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("amount"),maxAmount));
    }
     public static Specification<Expense> betweenDates(LocalDate startDate,LocalDate endDate){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("expenseDate"),startDate,endDate));
     }

    public static Specification<Expense> hasStartDate(LocalDate startDate){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("expenseDate"),startDate));
    }
    public static Specification<Expense> hasEndDate(LocalDate endDate){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("expenseDate"),endDate));
    }
}

