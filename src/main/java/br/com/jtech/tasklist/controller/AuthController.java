package br.com.jtech.tasklist.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.jtech.tasklist.dto.AuthRequest;
import br.com.jtech.tasklist.dto.AuthResponse;
import br.com.jtech.tasklist.dto.RegisterRequest;
import br.com.jtech.tasklist.dto.UserResponse;
import br.com.jtech.tasklist.service.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        String userEmail = authentication.getName();
        return authService.getCurrentUser(userEmail);
    }
}

