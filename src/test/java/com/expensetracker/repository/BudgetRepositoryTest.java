package com.expensetracker.repository;

import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.Role;
import com.expensetracker.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BudgetRepositoryTest {
    @Autowired
    BudgetRepository budgetRepository;
    @Autowired
    UserRepository userRepository;
    @Test
    void shouldFindBudgetByYearMonthAndUser() {

        User user = new User();
        user.setName("Suganya");
        user.setEmail("budget@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        user = userRepository.save(user);

        Budget budget = new Budget(
                "2026-09",
                10000
        );
        budget.setUser(user);

        budgetRepository.save(budget);

        Optional<Budget> result =
                budgetRepository.findByYearMonthAndUser(
                        "2026-09",
                        user
                );

        assertTrue(result.isPresent());
        assertEquals("2026-09", result.get().getYearMonth());
        assertEquals(10000, result.get().getBudgetAmount());
        assertEquals(user.getId(), result.get().getUser().getId());
    }
    @Test
    void shouldNotFindBudgetBelongingToAnotherUser() {

        User user1 = new User();
        user1.setName("Suganya");
        user1.setEmail("budgetuser1@test.com");
        user1.setPassword("password");
        user1.setRole(Role.USER);

        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("budgetuser2@test.com");
        user2.setPassword("password");
        user2.setRole(Role.USER);

        user2 = userRepository.save(user2);

        Budget budget = new Budget(
                "2026-09",
                10000
        );

        budget.setUser(user1);
        budgetRepository.save(budget);

        Optional<Budget> result =
                budgetRepository.findByYearMonthAndUser(
                        "2026-09",
                        user2
                );

        assertTrue(result.isEmpty());
    }
}
