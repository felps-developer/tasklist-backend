/*
*  @(#)TaskListController.java
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

import br.com.jtech.tasklist.dto.TaskListRequest;
import br.com.jtech.tasklist.dto.TaskListResponse;
import br.com.jtech.tasklist.entity.TaskListEntity;
import br.com.jtech.tasklist.service.TaskListService;
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
* class TaskListController
* 
* @author jtech
*/
@RestController
@RequestMapping("/api/v1/task-lists")
@RequiredArgsConstructor
public class TaskListController {

    private final TaskListService taskListService;

    @PostMapping
    public ResponseEntity<TaskListResponse> create(@Valid @RequestBody TaskListRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        
        TaskListEntity created = taskListService.create(request.getName(), userEmail);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskListResponse>> findAll(Authentication authentication) {
        String userEmail = authentication.getName();
        List<TaskListEntity> lists = taskListService.findAllByUserEmail(userEmail);
        List<TaskListResponse> responses = lists.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskListResponse> findById(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        TaskListEntity taskList = taskListService.findByIdAndUserEmail(UUID.fromString(id), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));
        return ResponseEntity.ok(toResponse(taskList));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskListResponse> update(
            @PathVariable String id,
            @Valid @RequestBody TaskListRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        
        TaskListEntity updated = taskListService.update(
            UUID.fromString(id),
            request.getName(),
            userEmail
        );
        
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        taskListService.delete(UUID.fromString(id), userEmail);
        return ResponseEntity.noContent().build();
    }

    private TaskListResponse toResponse(TaskListEntity taskList) {
        return TaskListResponse.builder()
                .id(taskList.getId().toString())
                .name(taskList.getName())
                .createdAt(taskList.getCreatedAt())
                .updatedAt(taskList.getUpdatedAt())
                .build();
    }
}

