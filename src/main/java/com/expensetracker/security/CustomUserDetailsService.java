package com.expensetracker.security;

import com.expensetracker.model.entity.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        User user=userRepository.findByEmail(userName).orElseThrow(()->new UsernameNotFoundException("User is not registered. Please register"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).
                roles(user.getRole().name()).
                password(user.getPassword()).build();
    }
}
