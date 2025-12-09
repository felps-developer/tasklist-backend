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

import br.com.jtech.tasklist.dto.PageResponse;
import br.com.jtech.tasklist.dto.TaskListRequest;
import br.com.jtech.tasklist.dto.TaskListResponse;
import br.com.jtech.tasklist.entity.TaskListEntity;
import br.com.jtech.tasklist.service.TaskListService;
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
        
        TaskListEntity created = taskListService.create(request, userEmail);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<TaskListResponse>> findAllWithoutPagination(
            @RequestParam(required = false) String name,
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<TaskListEntity> lists = taskListService.findAllByUserEmailAndName(userEmail, name);
        List<TaskListResponse> responses = lists.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<PageResponse<TaskListResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Page<TaskListEntity> listsPage = taskListService.findAllByUserEmailPaginated(userEmail, page, size, name);
        
        List<TaskListResponse> responses = listsPage.getContent().stream()
                .map(this::toResponse)
                .toList();
        
        PageResponse<TaskListResponse> pageResponse = PageResponse.<TaskListResponse>builder()
                .content(responses)
                .page(listsPage.getNumber())
                .size(listsPage.getSize())
                .totalElements(listsPage.getTotalElements())
                .totalPages(listsPage.getTotalPages())
                .first(listsPage.isFirst())
                .last(listsPage.isLast())
                .build();
        
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TaskListResponse> findById(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        
        // Validar se é um UUID válido - se não for, retornar 404
        if (id == null || id.equals("all") || !isValidUUID(id)) {
            throw new ResourceNotFoundException("Lista não encontrada");
        }
        
        TaskListEntity taskList = taskListService.findByIdAndUserEmail(UUID.fromString(id), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));
        return ResponseEntity.ok(toResponse(taskList));
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

    @PutMapping("/{id}")
    public ResponseEntity<TaskListResponse> update(
            @PathVariable String id,
            @Valid @RequestBody TaskListRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        
        // Validar se é um UUID válido
        if (id == null || id.equals("all") || !isValidUUID(id)) {
            throw new ResourceNotFoundException("Lista não encontrada");
        }
        
        TaskListEntity updated = taskListService.update(UUID.fromString(id), request, userEmail);
        
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        String userEmail = authentication.getName();
        
        // Validar se é um UUID válido
        if (id == null || id.equals("all") || !isValidUUID(id)) {
            throw new ResourceNotFoundException("Lista não encontrada");
        }
        
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

