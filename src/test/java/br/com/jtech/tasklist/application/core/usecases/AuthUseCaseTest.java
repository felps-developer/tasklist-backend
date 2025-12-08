/*
*  @(#)AuthUseCaseTest.java
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
package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
* class AuthUseCaseTest 
* 
* @author jtech
*/
@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private AuthOutputGateway authOutputGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("123")
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        User newUser = User.builder()
                .name("New User")
                .email("new@example.com")
                .password("password123")
                .build();

        when(authOutputGateway.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");
        when(authOutputGateway.save(any(User.class))).thenReturn(user);

        // When
        User result = authUseCase.register(newUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(authOutputGateway).existsByEmail(newUser.getEmail());
        verify(passwordEncoder).encode(newUser.getPassword());
        verify(authOutputGateway).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        User newUser = User.builder()
                .name("New User")
                .email("existing@example.com")
                .password("password123")
                .build();

        when(authOutputGateway.existsByEmail(newUser.getEmail())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> authUseCase.register(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já está em uso");

        verify(authOutputGateway).existsByEmail(newUser.getEmail());
        verify(authOutputGateway, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        when(authOutputGateway.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(email)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn("refreshToken");

        // When
        var result = authUseCase.login(email, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        verify(authOutputGateway).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(jwtTokenProvider).generateToken(email);
        verify(jwtTokenProvider).generateRefreshToken(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "notfound@example.com";
        String password = "password123";

        when(authOutputGateway.findByEmail(email)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authUseCase.login(email, password))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(authOutputGateway).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // Given
        String email = "test@example.com";
        String password = "wrongPassword";

        when(authOutputGateway.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authUseCase.login(email, password))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");

        verify(authOutputGateway).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }
}

