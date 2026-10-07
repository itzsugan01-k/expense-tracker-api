package com.expensetracker.repository;

import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget,Integer> {
    Optional<Budget>findByYearMonthAndUser(String yearMonth, User user);
}
