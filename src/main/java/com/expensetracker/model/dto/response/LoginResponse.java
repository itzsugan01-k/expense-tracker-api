package com.expensetracker.model.dto.response;

import com.expensetracker.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;


    private String type;

    private String email;

    private Role role;

    public LoginResponse(String accessToken,String refreshToken) {
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
    }
}