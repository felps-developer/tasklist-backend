/*
*  @(#)AuthServiceTest.java
*
*  Copyright (c) J-Tech Solucoes em Informatica.
*  All Rights Reserved.
*
*  This software is the confidential and proprietary information of J-Tech.
*  ("Confidential Information"). You shall not disclose such Confidential
*  Information and shall use it only in accordance with the terms of the
*  license agreement you entered into with J-Tech.
*
*/
package br.com.jtech.tasklist.service;

import br.com.jtech.tasklist.dto.AuthResponse;
import br.com.jtech.tasklist.entity.UserEntity;
import br.com.jtech.tasklist.repository.UserRepository;
import br.com.jtech.tasklist.config.infra.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
* class AuthServiceTest 
* 
* @author jtech
*/
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        String name = "New User";
        String email = "new@example.com";
        String password = "password123";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        // When
        UserEntity result = authService.register(name, email, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        String name = "New User";
        String email = "existing@example.com";
        String password = "password123";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> authService.register(name, email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já está em uso");

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(email)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn("refreshToken");

        // When
        AuthResponse result = authService.login(email, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(jwtTokenProvider).generateToken(email);
        verify(jwtTokenProvider).generateRefreshToken(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "notfound@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authService.login(email, password))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // Given
        String email = "test@example.com";
        String password = "wrongPassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authService.login(email, password))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }
}

