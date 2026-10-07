package com.expensetracker.controller;

import com.expensetracker.model.dto.RefreshTokenRequest;
import com.expensetracker.model.dto.request.LoginRequest;
import com.expensetracker.model.dto.request.RegisterRequest;
import com.expensetracker.model.dto.response.LoginResponse;
import com.expensetracker.model.dto.response.RefreshTokenResponse;
import com.expensetracker.model.dto.response.UserResponse;
import com.expensetracker.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterRequest request){
         return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        System.out.println("Inside Login Controller");

        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse>refreshToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void>logout(){
        authService.logout();
        return ResponseEntity.noContent().build();
    }
}
