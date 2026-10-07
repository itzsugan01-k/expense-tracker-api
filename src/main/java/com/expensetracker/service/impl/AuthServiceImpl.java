package com.expensetracker.service.impl;

import com.expensetracker.exception.UserAlreadyExistException;
import com.expensetracker.mapper.UserMapper;
import com.expensetracker.model.dto.RefreshTokenRequest;
import com.expensetracker.model.dto.request.LoginRequest;
import com.expensetracker.model.dto.request.RegisterRequest;
import com.expensetracker.model.dto.response.LoginResponse;
import com.expensetracker.model.dto.response.RefreshTokenResponse;
import com.expensetracker.model.dto.response.UserResponse;
import com.expensetracker.model.entity.RefreshToken;
import com.expensetracker.model.entity.Role;
import com.expensetracker.model.entity.User;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtService;
import com.expensetracker.service.AuthService;
import com.expensetracker.utill.Helper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenServiceImpl refreshTokenService;

    private final Helper helper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                           JwtService jwtService, RefreshTokenServiceImpl refreshTokenService, Helper helper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;

        this.helper = helper;
    }

    @Override
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistException(request.getEmail());
        }
        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        User saveduser = userRepository.save(user);
        return UserMapper.toResponse(saveduser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()));

            System.out.println(authentication);
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("user not found"));
            String accessToken = jwtService.generateAccessToken(user.getEmail());

            refreshTokenService.deleteByUser(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return new LoginResponse(accessToken, refreshToken.getToken());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        refreshTokenService.delete(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        return new RefreshTokenResponse(accessToken, newRefreshToken.getToken());
    }

    @Transactional
    @Override
    public void logout() {
        User user = helper.getCurrentUser();
        refreshTokenService.deleteByUser(user);
        log.info("User {} logged out successfully", user.getEmail());
    }


}
