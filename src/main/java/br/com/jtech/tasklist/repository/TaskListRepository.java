/*
*  @(#)TaskListRepository.java
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
package br.com.jtech.tasklist.repository;

import br.com.jtech.tasklist.entity.TaskListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
    
    Optional<TaskListEntity> findByIdAndUser_Id(UUID id, UUID userId);
    
    boolean existsByIdAndUser_Id(UUID id, UUID userId);
}

