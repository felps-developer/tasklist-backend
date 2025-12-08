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

import br.com.jtech.tasklist.dto.TaskRequest;
import br.com.jtech.tasklist.dto.TaskResponse;
import br.com.jtech.tasklist.entity.TaskEntity;
import br.com.jtech.tasklist.service.TaskService;
import br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            userEmail
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll(Authentication authentication) {
        String userEmail = authentication.getName();
        List<TaskEntity> tasks = taskService.findAllByUserEmail(userEmail);
        List<TaskResponse> responses = tasks.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
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
        
        TaskEntity updated = taskService.update(
            UUID.fromString(id),
            request.getTitle(),
            request.getDescription(),
            request.getCompleted(),
            userEmail
        );
        
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        taskService.delete(UUID.fromString(id), userEmail);
        return ResponseEntity.noContent().build();
    }

    private TaskResponse toResponse(TaskEntity task) {
        return TaskResponse.builder()
                .id(task.getId().toString())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

