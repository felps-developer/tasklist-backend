/*
*  @(#)TaskUseCaseTest.java
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
package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.AuthOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskOutputGateway;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
* class TaskUseCaseTest 
* 
* @author jtech
*/
@ExtendWith(MockitoExtension.class)
class TaskUseCaseTest {

    @Mock
    private TaskOutputGateway taskOutputGateway;

    @Mock
    private AuthOutputGateway authOutputGateway;

    @InjectMocks
    private TaskUseCase taskUseCase;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Test User")
                .email("test@example.com")
                .build();

        task = Task.builder()
                .id(UUID.randomUUID().toString())
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .userId(user.getId())
                .build();
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        // Given
        String userEmail = "test@example.com";
        Task newTask = Task.builder()
                .title("New Task")
                .description("New Description")
                .completed(false)
                .build();

        when(authOutputGateway.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskOutputGateway.save(any(Task.class), eq(user))).thenReturn(task);

        // When
        Task result = taskUseCase.create(newTask, userEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(authOutputGateway).findByEmail(userEmail);
        verify(taskOutputGateway).save(any(Task.class), eq(user));
    }

    @Test
    void shouldFindAllTasksByUserEmail() {
        // Given
        String userEmail = "test@example.com";
        List<Task> tasks = Arrays.asList(task);

        when(authOutputGateway.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskOutputGateway.findAllByUserId(UUID.fromString(user.getId()))).thenReturn(tasks);

        // When
        List<Task> result = taskUseCase.findAllByUserEmail(userEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        verify(authOutputGateway).findByEmail(userEmail);
        verify(taskOutputGateway).findAllByUserId(UUID.fromString(user.getId()));
    }

    @Test
    void shouldFindTaskByIdAndUserEmail() {
        // Given
        String userEmail = "test@example.com";
        String taskId = task.getId();

        when(authOutputGateway.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskOutputGateway.findByIdAndUserId(UUID.fromString(taskId), UUID.fromString(user.getId())))
                .thenReturn(Optional.of(task));

        // When
        Optional<Task> result = taskUseCase.findByIdAndUserEmail(taskId, userEmail);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Task");
        verify(authOutputGateway).findByEmail(userEmail);
        verify(taskOutputGateway).findByIdAndUserId(UUID.fromString(taskId), UUID.fromString(user.getId()));
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        // Given
        String userEmail = "test@example.com";
        Task updatedTask = Task.builder()
                .id(task.getId())
                .title("Updated Task")
                .description("Updated Description")
                .completed(true)
                .build();

        when(authOutputGateway.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskOutputGateway.findByIdAndUserId(UUID.fromString(task.getId()), UUID.fromString(user.getId())))
                .thenReturn(Optional.of(task));
        when(taskOutputGateway.save(any(Task.class), eq(user))).thenReturn(updatedTask);

        // When
        Task result = taskUseCase.update(updatedTask, userEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Task");
        verify(authOutputGateway).findByEmail(userEmail);
        verify(taskOutputGateway).findByIdAndUserId(UUID.fromString(task.getId()), UUID.fromString(user.getId()));
        verify(taskOutputGateway).save(any(Task.class), eq(user));
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForUpdate() {
        // Given
        String userEmail = "test@example.com";
        Task updatedTask = Task.builder()
                .id(task.getId())
                .title("Updated Task")
                .build();

        when(authOutputGateway.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskOutputGateway.findByIdAndUserId(UUID.fromString(task.getId()), UUID.fromString(user.getId())))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> taskUseCase.update(updatedTask, userEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tarefa não encontrada ou você não tem permissão para acessá-la");

        verify(authOutputGateway).findByEmail(userEmail);
        verify(taskOutputGateway).findByIdAndUserId(UUID.fromString(task.getId()), UUID.fromString(user.getId()));
        verify(taskOutputGateway, never()).save(any(Task.class), any(User.class));
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        // Given
        String userEmail = "test@example.com";
        String taskId = task.getId();

        when(authOutputGateway.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskOutputGateway.findByIdAndUserId(UUID.fromString(taskId), UUID.fromString(user.getId())))
                .thenReturn(Optional.of(task));
        doNothing().when(taskOutputGateway).deleteById(UUID.fromString(taskId));

        // When
        taskUseCase.delete(taskId, userEmail);

        // Then
        verify(authOutputGateway).findByEmail(userEmail);
        verify(taskOutputGateway).findByIdAndUserId(UUID.fromString(taskId), UUID.fromString(user.getId()));
        verify(taskOutputGateway).deleteById(UUID.fromString(taskId));
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForDelete() {
        // Given
        String userEmail = "test@example.com";
        String taskId = task.getId();

        when(authOutputGateway.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskOutputGateway.findByIdAndUserId(UUID.fromString(taskId), UUID.fromString(user.getId())))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> taskUseCase.delete(taskId, userEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tarefa não encontrada ou você não tem permissão para acessá-la");

        verify(authOutputGateway).findByEmail(userEmail);
        verify(taskOutputGateway).findByIdAndUserId(UUID.fromString(taskId), UUID.fromString(user.getId()));
        verify(taskOutputGateway, never()).deleteById(any(UUID.class));
    }
}

