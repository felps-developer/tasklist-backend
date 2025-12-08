/*
*  @(#)TaskRepository.java
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
package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
    
    Optional<TaskEntity> findByIdAndUser_Id(UUID id, UUID userId);
    
    boolean existsByIdAndUser_Id(UUID id, UUID userId);
}

