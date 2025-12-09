
package br.com.jtech.tasklist.service;

import br.com.jtech.tasklist.dto.TaskRequest;
import br.com.jtech.tasklist.dto.TaskResponse;
import br.com.jtech.tasklist.entity.TaskEntity;
import br.com.jtech.tasklist.entity.UserEntity;
import br.com.jtech.tasklist.repository.TaskRepository;
import br.com.jtech.tasklist.repository.TaskListRepository;
import br.com.jtech.tasklist.repository.UserRepository;
import br.com.jtech.tasklist.config.infra.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private TaskListRepository taskListRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private br.com.jtech.tasklist.service.impl.TaskServiceImpl taskService;

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
        TaskRequest request = TaskRequest.builder()
                .title("New Task")
                .description("New Description")
                .completed(false)
                .taskListId(null)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);

        // When
        TaskResponse result = taskService.save(request, userEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).save(any(TaskEntity.class));
    }


    @Test
    void shouldUpdateTaskSuccessfully() {
        // Given
        String userEmail = "test@example.com";
        TaskRequest request = TaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .completed(true)
                .taskListId(null)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser_Id(task.getId(), user.getId()))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);

        // When
        TaskResponse result = taskService.update(task.getId().toString(), request, userEmail);

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
        TaskRequest request = TaskRequest.builder()
                .title("Updated Task")
                .description(null)
                .completed(null)
                .taskListId(null)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser_Id(task.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> taskService.update(task.getId().toString(), request, userEmail))
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
        taskService.delete(taskId.toString(), userEmail);

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
        assertThatThrownBy(() -> taskService.delete(taskId.toString(), userEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tarefa não encontrada ou você não tem permissão para acessá-la");

        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByIdAndUser_Id(taskId, user.getId());
        verify(taskRepository, never()).deleteById(any(UUID.class));
    }
}

