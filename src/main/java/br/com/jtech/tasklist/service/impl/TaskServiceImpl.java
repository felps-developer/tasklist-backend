package br.com.jtech.tasklist.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.jtech.tasklist.dto.TaskFilterDTO;
import br.com.jtech.tasklist.dto.TaskRequest;
import br.com.jtech.tasklist.dto.TaskResponse;
import br.com.jtech.tasklist.entity.TaskEntity;
import br.com.jtech.tasklist.entity.TaskListEntity;
import br.com.jtech.tasklist.entity.UserEntity;
import br.com.jtech.tasklist.repository.TaskRepository;
import br.com.jtech.tasklist.repository.TaskListRepository;
import br.com.jtech.tasklist.repository.UserRepository;
import br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException;
import br.com.jtech.tasklist.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskListRepository taskListRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<TaskResponse> findAll(TaskFilterDTO filter, Pageable pageable, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        UUID taskListId = null;
        if (filter.getTaskListId() != null && isValidUUID(filter.getTaskListId())) {
            taskListId = UUID.fromString(filter.getTaskListId());
        }

        Page<TaskEntity> tasksPage;
        String title = filter.getTitle();
        
        if (taskListId != null) {
            if (title != null && !title.trim().isEmpty()) {
                tasksPage = repository.findByTaskList_IdAndUser_IdAndTitleContainingIgnoreCase(taskListId, user.getId(), title.trim(), pageable);
            } else {
                tasksPage = repository.findByTaskList_IdAndUser_Id(taskListId, user.getId(), pageable);
            }
        } else {
            if (title != null && !title.trim().isEmpty()) {
                tasksPage = repository.findByUser_IdAndTitleContainingIgnoreCase(user.getId(), title.trim(), pageable);
            } else {
                tasksPage = repository.findByUser_Id(user.getId(), pageable);
            }
        }

        List<TaskResponse> responseList = new ArrayList<>();
        for (TaskEntity task : tasksPage) {
            responseList.add(toResponse(task));
        }

        return new PageImpl<>(responseList, pageable, tasksPage.getTotalElements());
    }

    @Override
    public List<TaskResponse> list(TaskFilterDTO filter, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        UUID taskListId = null;
        if (filter.getTaskListId() != null && isValidUUID(filter.getTaskListId())) {
            taskListId = UUID.fromString(filter.getTaskListId());
        }

        List<TaskEntity> tasks;
        String title = filter.getTitle();
        
        if (taskListId != null) {
            if (title != null && !title.trim().isEmpty()) {
                tasks = repository.findByTaskList_IdAndUser_IdAndTitleContainingIgnoreCase(taskListId, user.getId(), title.trim());
            } else {
                tasks = repository.findByTaskList_IdAndUser_Id(taskListId, user.getId());
            }
        } else {
            if (title != null && !title.trim().isEmpty()) {
                tasks = repository.findByUser_IdAndTitleContainingIgnoreCase(user.getId(), title.trim());
            } else {
                tasks = repository.findByUser_Id(user.getId());
            }
        }

        List<TaskResponse> responseList = new ArrayList<>();
        for (TaskEntity task : tasks) {
            responseList.add(toResponse(task));
        }

        return responseList;
    }

    @Override
    public TaskResponse findById(String id, String userEmail) {
        if (!isValidUUID(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskEntity task = repository.findByIdAndUser_Id(UUID.fromString(id), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));

        return toResponse(task);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public TaskResponse save(TaskRequest request, String userEmail) {
        try {
            TaskEntity task = convert(request, userEmail);
            repository.save(task);
            return toResponse(task);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Erro de integridade: " + ex.getMessage());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Erro ao salvar tarefa: " + ex.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public TaskResponse update(String id, TaskRequest request, String userEmail) {
        try {
            if (!isValidUUID(id)) {
                throw new ResourceNotFoundException("Tarefa não encontrada");
            }

            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            TaskEntity found = repository.findByIdAndUser_Id(UUID.fromString(id), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));

            TaskEntity task = convert(found, request, userEmail);
            repository.save(task);
            return toResponse(task);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Erro de integridade: " + ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Erro ao atualizar tarefa: " + ex.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void softDelete(String id, String userEmail) {
        if (!isValidUUID(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskEntity task = repository.findByIdAndUser_Id(UUID.fromString(id), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada ou você não tem permissão para acessá-la"));

        try {
            task.setActive(false);
            repository.save(task);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Registro não pode ser excluído, pois o mesmo tem registros relacionados.");
        }
    }

    @Override
    public TaskEntity convert(TaskRequest dto, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskListEntity taskList = null;
        if (dto.getTaskListId() != null && isValidUUID(dto.getTaskListId())) {
            taskList = taskListRepository.findByIdAndUser_Id(UUID.fromString(dto.getTaskListId()), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));
        }

        return TaskEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .completed(dto.getCompleted() != null ? dto.getCompleted() : false)
                .user(user)
                .taskList(taskList)
                .build();
    }

    public TaskEntity convert(TaskEntity task, TaskRequest dto, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        if (dto.getCompleted() != null) {
            task.setCompleted(dto.getCompleted());
        }

        if (dto.getTaskListId() != null && isValidUUID(dto.getTaskListId())) {
            TaskListEntity taskList = taskListRepository.findByIdAndUser_Id(UUID.fromString(dto.getTaskListId()), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));
            task.setTaskList(taskList);
        } else if (dto.getTaskListId() == null) {
            task.setTaskList(null);
        }

        return task;
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

