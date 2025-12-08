/*
*  @(#)AuthInputGateway.java
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
package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.application.core.domains.User;

/**
* interface AuthInputGateway 
* 
* @author jtech
*/
public interface AuthInputGateway {

    User register(User user);

    AuthResponse login(String email, String password);
}

