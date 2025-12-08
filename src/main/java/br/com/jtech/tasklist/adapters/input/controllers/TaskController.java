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
package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.input.TaskInputGateway;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* class TaskController
* 
* @author jtech
*/
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskInputGateway taskInputGateway;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .build();

        Task created = taskInputGateway.create(task, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll(Authentication authentication) {
        String userEmail = authentication.getName();
        List<Task> tasks = taskInputGateway.findAllByUserEmail(userEmail);
        List<TaskResponse> responses = tasks.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        Task task = taskInputGateway.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));
        return ResponseEntity.ok(toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        
        Task task = Task.builder()
                .id(id)
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.getCompleted())
                .build();

        Task updated = taskInputGateway.update(task, userEmail);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        taskInputGateway.delete(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}

