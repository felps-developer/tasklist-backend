/*
*  @(#)TaskServiceTest.java
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
package br.com.jtech.tasklist.service;

import br.com.jtech.tasklist.entity.TaskEntity;
import br.com.jtech.tasklist.entity.UserEntity;
import br.com.jtech.tasklist.repository.TaskRepository;
import br.com.jtech.tasklist.repository.UserRepository;
import br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
* class TaskServiceTest 
* 
* @author jtech
*/
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private UserEntity user;
    private TaskEntity task;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .build();

        task = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .user(user)
                .build();
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        // Given
        String userEmail = "test@example.com";
        String title = "New Task";
        String description = "New Description";
        Boolean completed = false;

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);

        // When
        TaskEntity result = taskService.create(title, description, completed, userEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void shouldFindAllTasksByUserEmail() {
        // Given
        String userEmail = "test@example.com";
        List<TaskEntity> tasks = Arrays.asList(task);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByUser_Id(user.getId())).thenReturn(tasks);

        // When
        List<TaskEntity> result = taskService.findAllByUserEmail(userEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByUser_Id(user.getId());
    }

    @Test
    void shouldFindTaskByIdAndUserEmail() {
        // Given
        String userEmail = "test@example.com";
        UUID taskId = task.getId();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser_Id(taskId, user.getId()))
                .thenReturn(Optional.of(task));

        // When
        Optional<TaskEntity> result = taskService.findByIdAndUserEmail(taskId, userEmail);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Task");
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByIdAndUser_Id(taskId, user.getId());
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        // Given
        String userEmail = "test@example.com";
        String updatedTitle = "Updated Task";
        String updatedDescription = "Updated Description";
        Boolean completed = true;

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser_Id(task.getId(), user.getId()))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);

        // When
        TaskEntity result = taskService.update(task.getId(), updatedTitle, updatedDescription, completed, userEmail);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByIdAndUser_Id(task.getId(), user.getId());
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForUpdate() {
        // Given
        String userEmail = "test@example.com";
        String updatedTitle = "Updated Task";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser_Id(task.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> taskService.update(task.getId(), updatedTitle, null, null, userEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tarefa não encontrada ou você não tem permissão para acessá-la");

        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByIdAndUser_Id(task.getId(), user.getId());
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        // Given
        String userEmail = "test@example.com";
        UUID taskId = task.getId();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser_Id(taskId, user.getId()))
                .thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById(taskId);

        // When
        taskService.delete(taskId, userEmail);

        // Then
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByIdAndUser_Id(taskId, user.getId());
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForDelete() {
        // Given
        String userEmail = "test@example.com";
        UUID taskId = task.getId();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser_Id(taskId, user.getId()))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> taskService.delete(taskId, userEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tarefa não encontrada ou você não tem permissão para acessá-la");

        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByIdAndUser_Id(taskId, user.getId());
        verify(taskRepository, never()).deleteById(any(UUID.class));
    }
}

