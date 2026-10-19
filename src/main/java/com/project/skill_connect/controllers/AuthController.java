package com.project.skill_connect.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.skill_connect.dto.LoginRequest;
import com.project.skill_connect.dto.RegisterRequest;
import com.project.skill_connect.dto.UserResponse;
import com.project.skill_connect.entities.User;
import com.project.skill_connect.services.UserService;

import jakarta.validation.Valid;

import com.project.skill_connect.dto.TokenResponse;
import com.project.skill_connect.security.JwtService;

@RestController 
@RequestMapping ("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setContact(request.getContact());
        user.setRole(request.getRole());

        User savedUser = userService.registerUser(user);

        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setContact(savedUser.getContact());
        response.setRole(savedUser.getRole());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request){
        User user = userService.authenticateUser(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user.getEmail());
        return new TokenResponse(token);
    }
}









