/*
*  @(#)TaskAdapter.java
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

import br.com.jtech.tasklist.adapters.output.repositories.TaskRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.jtech.tasklist.application.core.domains.Task.of;

/**
* class TaskAdapter 
* 
* @author jtech
*/
@Component
@RequiredArgsConstructor
public class TaskAdapter implements TaskOutputGateway {

    private final TaskRepository taskRepository;

    @Override
    public Task save(Task task, User user) {
        TaskEntity entity = task.toEntity();
        entity.setUser(user.toEntity());
        TaskEntity saved = taskRepository.save(entity);
        return of(saved);
    }

    @Override
    public List<Task> findAllByUserId(UUID userId) {
        return taskRepository.findByUser_Id(userId).stream()
                .map(Task::of)
                .toList();
    }

    @Override
    public Optional<Task> findByIdAndUserId(UUID id, UUID userId) {
        return taskRepository.findByIdAndUser_Id(id, userId)
                .map(Task::of);
    }

    @Override
    public void deleteById(UUID id) {
        taskRepository.deleteById(id);
    }
}

