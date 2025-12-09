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

import br.com.jtech.tasklist.dto.TaskRequest;
import br.com.jtech.tasklist.entity.TaskEntity;
import br.com.jtech.tasklist.entity.TaskListEntity;
import br.com.jtech.tasklist.entity.UserEntity;
import br.com.jtech.tasklist.repository.TaskRepository;
import br.com.jtech.tasklist.repository.TaskListRepository;
import br.com.jtech.tasklist.repository.UserRepository;
import br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;

    public TaskEntity create(TaskRequest request, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskListEntity taskList = null;
        if (request.getTaskListId() != null && !request.getTaskListId().isEmpty() && !request.getTaskListId().equals("undefined") && isValidUUID(request.getTaskListId())) {
            taskList = taskListRepository.findByIdAndUser_Id(UUID.fromString(request.getTaskListId()), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));
        }

        TaskEntity task = TaskEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .user(user)
                .taskList(taskList)
                .build();

        return taskRepository.save(task);
    }

    public List<TaskEntity> findAllByUserEmail(String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return taskRepository.findByUser_Id(user.getId());
    }

    public List<TaskEntity> findAllByUserEmailAndTitle(String userEmail, String title) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (title == null || title.trim().isEmpty() || title.equals("")) {
            return taskRepository.findByUser_Id(user.getId());
        }

        return taskRepository.findByUser_IdAndTitleContainingIgnoreCase(user.getId(), title.trim());
    }

    public Page<TaskEntity> findAllByUserEmailPaginated(String userEmail, int page, int size, String title) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size);

        if (title == null || title.trim().isEmpty() || title.equals("")) {
            return taskRepository.findByUser_Id(user.getId(), pageable);
        }

        return taskRepository.findByUser_IdAndTitleContainingIgnoreCase(user.getId(), title.trim(), pageable);
    }

    public List<TaskEntity> findAllByTaskListIdAndUserEmail(UUID taskListId, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return taskRepository.findByTaskList_IdAndUser_Id(taskListId, user.getId());
    }

    public Page<TaskEntity> findAllByTaskListIdAndUserEmailPaginated(UUID taskListId, String userEmail, int page, int size, String title) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size);

        if (title == null || title.trim().isEmpty() || title.equals("")) {
            return taskRepository.findByTaskList_IdAndUser_Id(taskListId, user.getId(), pageable);
        }

        return taskRepository.findByTaskList_IdAndUser_IdAndTitleContainingIgnoreCase(taskListId, user.getId(), title.trim(), pageable);
    }

    public List<TaskEntity> findAllByTaskListIdAndUserEmailAndTitle(UUID taskListId, String userEmail, String title) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (title == null || title.trim().isEmpty() || title.equals("")) {
            return taskRepository.findByTaskList_IdAndUser_Id(taskListId, user.getId());
        }

        return taskRepository.findByTaskList_IdAndUser_IdAndTitleContainingIgnoreCase(taskListId, user.getId(), title.trim());
    }

    public Optional<TaskEntity> findByIdAndUserEmail(UUID id, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return taskRepository.findByIdAndUser_Id(id, user.getId());
    }

    public TaskEntity update(UUID id, TaskRequest request, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskEntity existingTask = taskRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        if (request.getCompleted() != null) {
            existingTask.setCompleted(request.getCompleted());
        }

        // Atualizar lista da tarefa
        if (request.getTaskListId() != null && !request.getTaskListId().isEmpty() && !request.getTaskListId().equals("undefined") && isValidUUID(request.getTaskListId())) {
            TaskListEntity taskList = taskListRepository.findByIdAndUser_Id(UUID.fromString(request.getTaskListId()), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));
            existingTask.setTaskList(taskList);
        } else {
            existingTask.setTaskList(null);
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
    
    private boolean isValidUUID(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

