/*
*  @(#)TaskController.java
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
package br.com.jtech.tasklist.controller;

import br.com.jtech.tasklist.dto.PageResponse;
import br.com.jtech.tasklist.dto.TaskRequest;
import br.com.jtech.tasklist.dto.TaskResponse;
import br.com.jtech.tasklist.entity.TaskEntity;
import br.com.jtech.tasklist.service.TaskService;
import br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
* class TaskController
* 
* @author jtech
*/
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        
        TaskEntity created = taskService.create(
            request.getTitle(), 
            request.getDescription(), 
            request.getCompleted(), 
            request.getTaskListId(),
            userEmail
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TaskResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String taskListId,
            @RequestParam(required = false) String title,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Page<TaskEntity> tasksPage;
        
        if (taskListId != null && !taskListId.isEmpty() && !taskListId.equals("undefined") && isValidUUID(taskListId)) {
            tasksPage = taskService.findAllByTaskListIdAndUserEmailPaginated(
                UUID.fromString(taskListId), userEmail, page, size, title);
        } else {
            tasksPage = taskService.findAllByUserEmailPaginated(userEmail, page, size, title);
        }
        
        List<TaskResponse> responses = tasksPage.getContent().stream()
                .map(this::toResponse)
                .toList();
        
        PageResponse<TaskResponse> pageResponse = PageResponse.<TaskResponse>builder()
                .content(responses)
                .page(tasksPage.getNumber())
                .size(tasksPage.getSize())
                .totalElements(tasksPage.getTotalElements())
                .totalPages(tasksPage.getTotalPages())
                .first(tasksPage.isFirst())
                .last(tasksPage.isLast())
                .build();
        
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskResponse>> findAllWithoutPagination(
            @RequestParam(required = false) String taskListId,
            @RequestParam(required = false) String title,
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<TaskEntity> tasks;
        
        if (taskListId != null && !taskListId.isEmpty() && !taskListId.equals("undefined") && isValidUUID(taskListId)) {
            tasks = taskService.findAllByTaskListIdAndUserEmailAndTitle(
                UUID.fromString(taskListId), userEmail, title);
        } else {
            tasks = taskService.findAllByUserEmailAndTitle(userEmail, title);
        }
        
        List<TaskResponse> responses = tasks.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
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

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        
        // Validar se é um UUID válido - se não for, retornar 404
        if (id == null || !isValidUUID(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        
        TaskEntity task = taskService.findByIdAndUserEmail(UUID.fromString(id), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));
        return ResponseEntity.ok(toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        
        // Validar se é um UUID válido
        if (id == null || !isValidUUID(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        
        TaskEntity updated = taskService.update(
            UUID.fromString(id),
            request.getTitle(),
            request.getDescription(),
            request.getCompleted(),
            request.getTaskListId(),
            userEmail
        );
        
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        
        // Validar se é um UUID válido
        if (id == null || !isValidUUID(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        
        taskService.delete(UUID.fromString(id), userEmail);
        return ResponseEntity.noContent().build();
    }

    private TaskResponse toResponse(TaskEntity task) {
        return TaskResponse.builder()
                .id(task.getId().toString())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .taskListId(task.getTaskList() != null ? task.getTaskList().getId().toString() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

