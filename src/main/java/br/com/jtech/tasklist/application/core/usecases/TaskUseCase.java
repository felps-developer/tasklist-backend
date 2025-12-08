/*
*  @(#)TaskUseCase.java
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

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.input.TaskInputGateway;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
* class TaskUseCase 
* 
* @author jtech
*/
@Service
@RequiredArgsConstructor
public class TaskUseCase implements TaskInputGateway {

    private final TaskOutputGateway taskOutputGateway;
    private final AuthOutputGateway authOutputGateway;

    @Override
    public Task create(Task task, String userEmail) {
        User user = authOutputGateway.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        task.setUserId(user.getId());
        return taskOutputGateway.save(task, user);
    }

    @Override
    public List<Task> findAllByUserEmail(String userEmail) {
        User user = authOutputGateway.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return taskOutputGateway.findAllByUserId(UUID.fromString(user.getId()));
    }

    @Override
    public Optional<Task> findByIdAndUserEmail(String id, String userEmail) {
        User user = authOutputGateway.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return taskOutputGateway.findByIdAndUserId(UUID.fromString(id), UUID.fromString(user.getId()));
    }

    @Override
    public Task update(Task task, String userEmail) {
        User user = authOutputGateway.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Task existingTask = taskOutputGateway.findByIdAndUserId(
                UUID.fromString(task.getId()), 
                UUID.fromString(user.getId()))
                .orElseThrow(() -> new br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        if (task.getCompleted() != null) {
            existingTask.setCompleted(task.getCompleted());
        }

        return taskOutputGateway.save(existingTask, user);
    }

    @Override
    public void delete(String id, String userEmail) {
        User user = authOutputGateway.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Task task = taskOutputGateway.findByIdAndUserId(
                UUID.fromString(id), 
                UUID.fromString(user.getId()))
                .orElseThrow(() -> new br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));

        taskOutputGateway.deleteById(UUID.fromString(task.getId()));
    }
}

