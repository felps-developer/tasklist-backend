/*
*  @(#)TaskInputGateway.java
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

import br.com.jtech.tasklist.application.core.domains.Task;

import java.util.List;
import java.util.Optional;

/**
* interface TaskInputGateway 
* 
* @author jtech
*/
public interface TaskInputGateway {

    Task create(Task task, String userEmail);

    List<Task> findAllByUserEmail(String userEmail);

    Optional<Task> findByIdAndUserEmail(String id, String userEmail);

    Task update(Task task, String userEmail);

    void delete(String id, String userEmail);
}

