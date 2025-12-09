package br.com.jtech.tasklist.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.jtech.tasklist.dto.TaskFilterDTO;
import br.com.jtech.tasklist.dto.TaskRequest;
import br.com.jtech.tasklist.dto.TaskResponse;
import br.com.jtech.tasklist.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TaskResponse create(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return taskService.save(request, userEmail);
    }

    @GetMapping
    public Page<TaskResponse> findAll(
            @Valid @ModelAttribute TaskFilterDTO filter,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Pageable pageable = PageRequest.of(filter.getPageOrDefault(), filter.getSizeOrDefault());
        return taskService.findAll(filter, pageable, userEmail);
    }

    @GetMapping("/all")
    public List<TaskResponse> findAllWithoutPagination(
            @Valid @ModelAttribute TaskFilterDTO filter,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return taskService.list(filter, userEmail);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(
            @PathVariable String id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return taskService.findById(id, userEmail);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TaskResponse update(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return taskService.update(id, request, userEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        taskService.delete(id, userEmail);
    }
}

