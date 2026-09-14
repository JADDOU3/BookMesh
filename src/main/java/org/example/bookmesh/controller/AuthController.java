package org.example.bookmesh.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.auth.AuthResponse;
import org.example.bookmesh.dto.auth.LoginRequest;
import org.example.bookmesh.dto.auth.RegisterRequest;
import org.example.bookmesh.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @MutationMapping
    public AuthResponse signup(@Argument RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return response;
    }

    @MutationMapping
    public AuthResponse login(@Argument LoginRequest request) {
        AuthResponse response = authService.login(request);
        return response;
    }
}