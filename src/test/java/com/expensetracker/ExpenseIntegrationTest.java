package com.expensetracker;

import com.expensetracker.model.entity.Role;
import com.expensetracker.model.entity.User;
import com.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ExpenseIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldAddExpense() throws Exception {

        User user = new User();
        user.setName("Suganya");
        user.setEmail("suganya@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        userRepository.save(user);


        String request = """
            {
                "description": "PIZZA",
                "amount": 600,
                "category": "FOOD",
                "expenseDate": "2026-09-29"
            }
            """;

        mockMvc.perform(
                        post("/expenses")
                                .with(user("suganya@test.com").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated());
    }
}
