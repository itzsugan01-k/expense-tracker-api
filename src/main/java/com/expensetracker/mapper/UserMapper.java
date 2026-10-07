package com.expensetracker.mapper;

import com.expensetracker.model.dto.request.RegisterRequest;
import com.expensetracker.model.dto.response.UserResponse;
import com.expensetracker.model.entity.User;

public class UserMapper {
    public static User toEntity(RegisterRequest request){
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        return user;
    }
    public static UserResponse toResponse(User user){
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
