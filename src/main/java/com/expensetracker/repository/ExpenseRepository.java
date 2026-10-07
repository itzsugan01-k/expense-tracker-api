package com.expensetracker.repository;

import com.expensetracker.model.entity.Category;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Integer>, JpaSpecificationExecutor<Expense> {
    List<Expense> findByDescriptionContainingIgnoreCaseAndUser(String description, User user);

    List<Expense> findByCategoryAndUser(Category category, User user);

    List<Expense> findByExpenseDateBetweenAndUser(LocalDate startDate, LocalDate endDate, User user);

    Page<Expense> findByUser(User user, Pageable pageable);

    Optional<Expense> findByIdAndUser(int id, User user);

    List<Expense> findByUser(User user);

}
