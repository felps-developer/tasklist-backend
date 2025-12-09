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

import br.com.jtech.tasklist.dto.TaskListFilterDTO;
import br.com.jtech.tasklist.dto.TaskListRequest;
import br.com.jtech.tasklist.dto.TaskListResponse;
import br.com.jtech.tasklist.entity.TaskListEntity;
import br.com.jtech.tasklist.entity.UserEntity;
import br.com.jtech.tasklist.repository.TaskListRepository;
import br.com.jtech.tasklist.repository.UserRepository;
import br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException;
import br.com.jtech.tasklist.service.TaskListService;

@Service
public class TaskListServiceImpl implements TaskListService {

    @Autowired
    private TaskListRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<TaskListResponse> findAll(TaskListFilterDTO filter, Pageable pageable, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Page<TaskListEntity> listsPage;
        String name = filter.getName();
        
        if (name != null && !name.trim().isEmpty()) {
            listsPage = repository.findByUser_IdAndNameContainingIgnoreCase(user.getId(), name.trim(), pageable);
        } else {
            listsPage = repository.findByUser_Id(user.getId(), pageable);
        }

        List<TaskListResponse> responseList = new ArrayList<>();
        for (TaskListEntity taskList : listsPage) {
            responseList.add(toResponse(taskList));
        }

        return new PageImpl<>(responseList, pageable, listsPage.getTotalElements());
    }

    @Override
    public List<TaskListResponse> list(TaskListFilterDTO filter, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        List<TaskListEntity> lists;
        String name = filter.getName();
        
        if (name != null && !name.trim().isEmpty()) {
            lists = repository.findByUser_IdAndNameContainingIgnoreCase(user.getId(), name.trim());
        } else {
            lists = repository.findByUser_Id(user.getId());
        }

        List<TaskListResponse> responseList = new ArrayList<>();
        for (TaskListEntity taskList : lists) {
            responseList.add(toResponse(taskList));
        }

        return responseList;
    }

    @Override
    public TaskListResponse findById(String id, String userEmail) {
        if (id == null || id.equals("all") || !isValidUUID(id)) {
            throw new ResourceNotFoundException("Lista não encontrada");
        }

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskListEntity taskList = repository.findByIdAndUser_Id(UUID.fromString(id), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));

        return toResponse(taskList);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public TaskListResponse save(TaskListRequest request, String userEmail) {
        try {
            TaskListEntity taskList = convert(request, userEmail);
            repository.save(taskList);
            return toResponse(taskList);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Erro de integridade: " + ex.getMessage());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Erro ao salvar lista: " + ex.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public TaskListResponse update(String id, TaskListRequest request, String userEmail) {
        try {
            if (id == null || id.equals("all") || !isValidUUID(id)) {
                throw new ResourceNotFoundException("Lista não encontrada");
            }

            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            TaskListEntity found = repository.findByIdAndUser_Id(UUID.fromString(id), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));

            TaskListEntity taskList = convert(found, request);
            repository.save(taskList);
            return toResponse(taskList);
        } catch (DataIntegrityViolationException ex) {
            String message = ex.getMessage();
            if (message != null && message.contains("name")) {
                throw new IllegalArgumentException("O campo nome já está sendo utilizado");
            }
            throw new IllegalArgumentException("Erro de integridade: " + ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Erro ao atualizar lista: " + ex.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void softDelete(String id, String userEmail) {
        if (id == null || id.equals("all") || !isValidUUID(id)) {
            throw new ResourceNotFoundException("Lista não encontrada");
        }

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TaskListEntity taskList = repository.findByIdAndUser_Id(UUID.fromString(id), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada ou você não tem permissão para acessá-la"));

        try {
            taskList.setActive(false);
            repository.save(taskList);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Registro não pode ser excluído, pois o mesmo tem registros relacionados.");
        }
    }

    @Override
    public TaskListEntity convert(TaskListRequest dto, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return TaskListEntity.builder()
                .name(dto.getName())
                .user(user)
                .build();
    }

    public TaskListEntity convert(TaskListEntity taskList, TaskListRequest dto) {
        if (dto.getName() != null) {
            taskList.setName(dto.getName());
        }

        return taskList;
    }

    private TaskListResponse toResponse(TaskListEntity taskList) {
        return TaskListResponse.builder()
                .id(taskList.getId().toString())
                .name(taskList.getName())
                .createdAt(taskList.getCreatedAt())
                .updatedAt(taskList.getUpdatedAt())
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

