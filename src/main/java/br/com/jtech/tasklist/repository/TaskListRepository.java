
package br.com.jtech.tasklist.repository;

import br.com.jtech.tasklist.entity.TaskListEntity;
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
* class TaskListRepository 
* 
* @author jtech
*/
@Repository
public interface TaskListRepository extends JpaRepository<TaskListEntity, UUID> {
    
    List<TaskListEntity> findByUser_Id(UUID userId);
    
    @Query("SELECT tl FROM TaskListEntity tl WHERE tl.user.id = :userId AND LOWER(tl.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TaskListEntity> findByUser_IdAndNameContainingIgnoreCase(@Param("userId") UUID userId, @Param("name") String name);
    
    Page<TaskListEntity> findByUser_Id(UUID userId, Pageable pageable);
    
    @Query("SELECT tl FROM TaskListEntity tl WHERE tl.user.id = :userId AND LOWER(tl.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<TaskListEntity> findByUser_IdAndNameContainingIgnoreCase(@Param("userId") UUID userId, @Param("name") String name, Pageable pageable);
    
    Optional<TaskListEntity> findByIdAndUser_Id(UUID id, UUID userId);
    
    boolean existsByIdAndUser_Id(UUID id, UUID userId);
}

