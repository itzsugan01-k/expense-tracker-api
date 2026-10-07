package com.expensetracker.repository;

import com.expensetracker.model.entity.Category;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.model.entity.Role;
import com.expensetracker.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ExpenseRepositoryTest {
    @Autowired
    ExpenseRepository expenseRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void findByDescriptionContainingIgnoreCaseAndUser() {
        // Arrange - create user
        User user = new User();
        user.setName("Suganya");
        user.setEmail("suganya@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        user = userRepository.save(user);

        // Arrange - create expenses
        Expense expense1 = new Expense(
                "PIZZA",
                600,
                Category.FOOD,
                LocalDate.of(2026, 9, 1)
        );
        expense1.setUser(user);

        Expense expense2 = new Expense(
                "Pizza Party",
                1000,
                Category.FOOD,
                LocalDate.of(2026, 9, 2)
        );
        expense2.setUser(user);

        Expense expense3 = new Expense(
                "BURGER",
                500,
                Category.FOOD,
                LocalDate.of(2026, 9, 3)
        );
        expense3.setUser(user);

        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        expenseRepository.save(expense3);
        List<Expense> expenses = expenseRepository.findByDescriptionContainingIgnoreCaseAndUser("pizza", user);
        assertEquals(2, expenses.size());
        assertTrue(expenses.stream().anyMatch(e -> e.getDescription().equals("PIZZA")));
        assertTrue(expenses.stream().anyMatch(e -> e.getDescription().equals("Pizza Party")));
    }

    @Test
    void shouldReturnExpensesOnlyForRequestedUser() {
        // Arrange - User 1
        User user1 = new User();
        user1.setName("Suganya");
        user1.setEmail("suganya1@test.com");
        user1.setPassword("password");
        user1.setRole(Role.USER);

        user1 = userRepository.save(user1);

        // Arrange - User 2
        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@test.com");
        user2.setPassword("password");
        user2.setRole(Role.USER);

        user2 = userRepository.save(user2);
        // User 1's expense
        Expense expense1 = new Expense(
                "PIZZA",
                600,
                Category.FOOD,
                LocalDate.of(2026, 9, 1)
        );
        expense1.setUser(user1);

        // User 2's expense
        Expense expense2 = new Expense(
                "PIZZA",
                800,
                Category.FOOD,
                LocalDate.of(2026, 9, 2)
        );
        expense2.setUser(user2);

        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        // Act
        List<Expense> result =
                expenseRepository
                        .findByDescriptionContainingIgnoreCaseAndUser(
                                "pizza",
                                user1
                        );
        // Assert
        assertEquals(1, result.size());
        assertEquals("PIZZA", result.get(0).getDescription());
        assertEquals(user1.getId(), result.get(0).getUser().getId());

    }

    @Test
    void shouldFindExpensesByCategoryAndUser() {

        User user = new User();
        user.setName("Suganya");
        user.setEmail("category@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        user = userRepository.save(user);

        Expense expense1 = new Expense(
                "PIZZA",
                600,
                Category.FOOD,
                LocalDate.of(2026, 9, 1)
        );
        expense1.setUser(user);

        Expense expense2 = new Expense(
                "BUS TICKET",
                100,
                Category.TRAVEL,
                LocalDate.of(2026, 9, 2)
        );
        expense2.setUser(user);

        Expense expense3 = new Expense(
                "BURGER",
                500,
                Category.FOOD,
                LocalDate.of(2026, 9, 3)
        );
        expense3.setUser(user);

        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        expenseRepository.save(expense3);

        List<Expense> result =
                expenseRepository.findByCategoryAndUser(
                        Category.FOOD,
                        user
                );

        assertEquals(2, result.size());

        assertTrue(result.stream()
                .allMatch(e -> e.getCategory() == Category.FOOD));
    }

    @Test
    void shouldFindExpensesByDateRangeAndUser() {

        User user = new User();
        user.setName("Suganya");
        user.setEmail("daterange@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        user = userRepository.save(user);

        Expense expense1 = new Expense(
                "PIZZA",
                600,
                Category.FOOD,
                LocalDate.of(2026, 9, 1)
        );
        expense1.setUser(user);

        Expense expense2 = new Expense(
                "BURGER",
                500,
                Category.FOOD,
                LocalDate.of(2026, 9, 10)
        );
        expense2.setUser(user);

        Expense expense3 = new Expense(
                "MOVIE",
                300,
                Category.ENTERTAINMENT,
                LocalDate.of(2026, 9, 20)
        );
        expense3.setUser(user);

        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        expenseRepository.save(expense3);

        List<Expense> result =
                expenseRepository.findByExpenseDateBetweenAndUser(
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 10),
                        user
                );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> !e.getExpenseDate().isBefore(LocalDate.of(2026, 9, 1))
                && !e.getExpenseDate().isAfter(LocalDate.of(2026, 9, 10))));
    }

    @Test
    void shouldFindExpensesByUserWithPagination() {

        User user = new User();
        user.setName("Suganya");
        user.setEmail("daterange@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        user = userRepository.save(user);
        for (int i = 1; i <= 5; i++) {
            Expense expense = new Expense(
                    "Expense " + i,
                    100 * i,
                    Category.FOOD,
                    LocalDate.of(2026, 9, i)
            );

            expense.setUser(user);
            expenseRepository.save(expense);

        }
        Pageable pageable = PageRequest.of(0, 2);
        Page<Expense> expensePage = expenseRepository.findByUser(user, pageable);
        assertEquals(2, expensePage.getContent().size());
        assertEquals(5, expensePage.getTotalElements());
        assertEquals(3, expensePage.getTotalPages());
    }

    @Test
    void shouldFindExpenseByIdAndUser() {

        User user = new User();
        user.setName("Suganya");
        user.setEmail("findbyid@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        user = userRepository.save(user);

        Expense expense = new Expense(
                "PIZZA",
                600,
                Category.FOOD,
                LocalDate.of(2026, 9, 1)
        );
        expense.setUser(user);

        expense = expenseRepository.save(expense);

        Optional<Expense> result =
                expenseRepository.findByIdAndUser(
                        expense.getId(),
                        user
                );

        assertTrue(result.isPresent());
        assertEquals("PIZZA", result.get().getDescription());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void shouldNotFindExpenseBelongingToAnotherUser() {

        User user1 = new User();
        user1.setName("Suganya");
        user1.setEmail("user1@test.com");
        user1.setPassword("password");
        user1.setRole(Role.USER);

        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@test.com");
        user2.setPassword("password");
        user2.setRole(Role.USER);

        user2 = userRepository.save(user2);

        Expense expense = new Expense(
                "PIZZA",
                600,
                Category.FOOD,
                LocalDate.of(2026, 9, 1)
        );
        expense.setUser(user1);

        expense = expenseRepository.save(expense);

        Optional<Expense> result =
                expenseRepository.findByIdAndUser(
                        expense.getId(),
                        user2
                );

        assertTrue(result.isEmpty());
    }
}
