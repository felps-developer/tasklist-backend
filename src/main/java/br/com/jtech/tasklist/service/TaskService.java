/*
*  @(#)TaskService.java
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

import br.com.jtech.tasklist.entity.TaskEntity;
import br.com.jtech.tasklist.entity.UserEntity;
import br.com.jtech.tasklist.repository.TaskRepository;
import br.com.jtech.tasklist.repository.UserRepository;
import br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
* class TaskService 
* 
* @author jtech
*/
@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskEntity create(String title, String description, Boolean completed, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskEntity task = TaskEntity.builder()
                .title(title)
                .description(description)
                .completed(completed != null ? completed : false)
                .user(user)
                .build();

        return taskRepository.save(task);
    }

    public List<TaskEntity> findAllByUserEmail(String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return taskRepository.findByUser_Id(user.getId());
    }

    public Optional<TaskEntity> findByIdAndUserEmail(UUID id, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return taskRepository.findByIdAndUser_Id(id, user.getId());
    }

    public TaskEntity update(UUID id, String title, String description, Boolean completed, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskEntity existingTask = taskRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));

        existingTask.setTitle(title);
        existingTask.setDescription(description);
        if (completed != null) {
            existingTask.setCompleted(completed);
        }

        return taskRepository.save(existingTask);
    }

    public void delete(UUID id, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskEntity task = taskRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));

        taskRepository.deleteById(task.getId());
    }
}

