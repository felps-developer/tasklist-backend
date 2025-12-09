package br.com.jtech.tasklist.service;

import br.com.jtech.tasklist.dto.TaskFilterDTO;
import br.com.jtech.tasklist.dto.TaskRequest;
import br.com.jtech.tasklist.dto.TaskResponse;
import br.com.jtech.tasklist.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    Page<TaskResponse> findAll(TaskFilterDTO filter, Pageable pageable, String userEmail);

    List<TaskResponse> list(TaskFilterDTO filter, String userEmail);

    TaskResponse findById(String id, String userEmail);

    TaskResponse save(TaskRequest request, String userEmail);

    TaskResponse update(String id, TaskRequest request, String userEmail);

    void softDelete(String id, String userEmail);

    TaskEntity convert(TaskRequest dto, String userEmail);
}
