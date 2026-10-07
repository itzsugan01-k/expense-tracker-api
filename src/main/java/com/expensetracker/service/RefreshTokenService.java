package com.expensetracker.service;

import com.expensetracker.model.entity.RefreshToken;
import com.expensetracker.model.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken findByToken(String token);
    void deleteByUser(User user);
}
