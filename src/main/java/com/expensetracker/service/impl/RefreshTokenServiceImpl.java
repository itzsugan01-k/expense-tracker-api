package com.expensetracker.service.impl;

import com.expensetracker.exception.RefreshTokenNotFoundException;
import com.expensetracker.model.entity.RefreshToken;
import com.expensetracker.model.entity.User;
import com.expensetracker.repository.RefreshTokenRepository;
import com.expensetracker.security.JwtService;
import com.expensetracker.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository tokenRepository;
    private final JwtService jwtService;
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshTokenServiceImpl(RefreshTokenRepository tokenRepository, JwtService jwtService) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    public RefreshToken createRefreshToken(User user) {

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        RefreshToken entity = new RefreshToken();
        entity.setToken(refreshToken);
        entity.setExpiryDate(LocalDateTime.now().plus(Duration.ofMillis(refreshExpiration)));
        entity.setUser(user);
        tokenRepository.save(entity);
        return entity;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new RuntimeException("Refresh token has expired");
        }
        return token;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return tokenRepository.findByToken(token).orElseThrow
                (()->new RefreshTokenNotFoundException("Refresh token not found"));
    }

    @Override
    @Transactional
    public void deleteByUser(User user) {
        tokenRepository.deleteByUser(user);
    }
    public void delete(RefreshToken refreshToken){
        tokenRepository.delete(refreshToken);
    }
}
