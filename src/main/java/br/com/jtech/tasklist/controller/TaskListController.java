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

import br.com.jtech.tasklist.dto.TaskListFilterDTO;
import br.com.jtech.tasklist.dto.TaskListRequest;
import br.com.jtech.tasklist.dto.TaskListResponse;
import br.com.jtech.tasklist.service.TaskListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/task-lists")
@RequiredArgsConstructor
public class TaskListController {

    private final TaskListService taskListService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TaskListResponse create(
            @Valid @RequestBody TaskListRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return taskListService.save(request, userEmail);
    }

    @GetMapping
    public Page<TaskListResponse> findAll(
            @Valid @ModelAttribute TaskListFilterDTO filter,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Pageable pageable = PageRequest.of(filter.getPageOrDefault(), filter.getSizeOrDefault());
        return taskListService.findAll(filter, pageable, userEmail);
    }

    @GetMapping("/all")
    public List<TaskListResponse> findAllWithoutPagination(
            @Valid @ModelAttribute TaskListFilterDTO filter,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return taskListService.list(filter, userEmail);
    }

    @GetMapping("/{id}")
    public TaskListResponse findById(
            @PathVariable String id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return taskListService.findById(id, userEmail);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TaskListResponse update(
            @PathVariable String id,
            @Valid @RequestBody TaskListRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return taskListService.update(id, request, userEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        taskListService.delete(id, userEmail);
    }
}

