
package br.com.jtech.tasklist.repository;

import br.com.jtech.tasklist.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
* class TaskRepository 
* 
* @author jtech
*/
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    
    List<TaskEntity> findByUser_Id(UUID userId);
    
    Page<TaskEntity> findByUser_Id(UUID userId, Pageable pageable);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<TaskEntity> findByUser_IdAndTitleContainingIgnoreCase(@Param("userId") UUID userId, @Param("title") String title);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<TaskEntity> findByUser_IdAndTitleContainingIgnoreCase(@Param("userId") UUID userId, @Param("title") String title, Pageable pageable);
    
    List<TaskEntity> findByTaskList_IdAndUser_Id(UUID taskListId, UUID userId);
    
    Page<TaskEntity> findByTaskList_IdAndUser_Id(UUID taskListId, UUID userId, Pageable pageable);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.taskList.id = :taskListId AND t.user.id = :userId AND LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<TaskEntity> findByTaskList_IdAndUser_IdAndTitleContainingIgnoreCase(@Param("taskListId") UUID taskListId, @Param("userId") UUID userId, @Param("title") String title);
    
    @Query("SELECT t FROM TaskEntity t WHERE t.taskList.id = :taskListId AND t.user.id = :userId AND LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<TaskEntity> findByTaskList_IdAndUser_IdAndTitleContainingIgnoreCase(@Param("taskListId") UUID taskListId, @Param("userId") UUID userId, @Param("title") String title, Pageable pageable);
    
    Optional<TaskEntity> findByIdAndUser_Id(UUID id, UUID userId);
    
    boolean existsByIdAndUser_Id(UUID id, UUID userId);
}

