/*
*  @(#)AuthOutputGateway.java
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
package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.User;

import java.util.Optional;

/**
* interface AuthOutputGateway 
* 
* @author jtech
*/
public interface AuthOutputGateway {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}

