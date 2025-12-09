package br.com.jtech.tasklist.service;

import br.com.jtech.tasklist.dto.AuthRequest;
import br.com.jtech.tasklist.dto.AuthResponse;
import br.com.jtech.tasklist.dto.RegisterRequest;
import br.com.jtech.tasklist.dto.UserResponse;
import br.com.jtech.tasklist.entity.UserEntity;

public interface AuthService {

    UserEntity register(RegisterRequest request);

    AuthResponse login(AuthRequest request);

    UserResponse getCurrentUser(String email);

    UserEntity findByEmail(String email);

    UserEntity convert(RegisterRequest dto);
}
