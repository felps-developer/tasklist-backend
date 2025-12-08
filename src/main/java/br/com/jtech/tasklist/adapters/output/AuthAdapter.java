/*
*  @(#)AuthAdapter.java
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
package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static br.com.jtech.tasklist.application.core.domains.User.of;

/**
* class AuthAdapter 
* 
* @author jtech
*/
@Component
@RequiredArgsConstructor
public class AuthAdapter implements AuthOutputGateway {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        UserEntity entity = user.toEntity();
        UserEntity saved = userRepository.save(entity);
        return of(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::of);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

