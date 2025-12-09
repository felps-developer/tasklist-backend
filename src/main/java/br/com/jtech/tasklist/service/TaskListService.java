package br.com.jtech.tasklist.service;

import br.com.jtech.tasklist.dto.TaskListFilterDTO;
import br.com.jtech.tasklist.dto.TaskListRequest;
import br.com.jtech.tasklist.dto.TaskListResponse;
import br.com.jtech.tasklist.entity.TaskListEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskListService {

    Page<TaskListResponse> findAll(TaskListFilterDTO filter, Pageable pageable, String userEmail);

    List<TaskListResponse> list(TaskListFilterDTO filter, String userEmail);

    TaskListResponse findById(String id, String userEmail);

    TaskListResponse save(TaskListRequest request, String userEmail);

    TaskListResponse update(String id, TaskListRequest request, String userEmail);

    void softDelete(String id, String userEmail);

    TaskListEntity convert(TaskListRequest dto, String userEmail);
}
