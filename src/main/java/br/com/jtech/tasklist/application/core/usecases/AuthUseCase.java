/*
*  @(#)AuthUseCase.java
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

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.input.AuthInputGateway;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;
import br.com.jtech.tasklist.config.infra.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
* class AuthUseCase 
* 
* @author jtech
*/
@Service
@RequiredArgsConstructor
public class AuthUseCase implements AuthInputGateway {

    private final AuthOutputGateway authOutputGateway;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public User register(User user) {
        if (authOutputGateway.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return authOutputGateway.save(user);
    }

    @Override
    public AuthResponse login(String email, String password) {
        User user = authOutputGateway.findByEmail(email)
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}

