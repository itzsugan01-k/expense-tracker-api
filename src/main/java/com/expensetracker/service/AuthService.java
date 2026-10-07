package com.expensetracker.service;

import com.expensetracker.model.dto.RefreshTokenRequest;
import com.expensetracker.model.dto.request.LoginRequest;
import com.expensetracker.model.dto.request.RegisterRequest;
import com.expensetracker.model.dto.response.LoginResponse;
import com.expensetracker.model.dto.response.RefreshTokenResponse;
import com.expensetracker.model.dto.response.UserResponse;
import org.jspecify.annotations.Nullable;

public interface AuthService {
     UserResponse registerUser(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

     void logout();
}
