/*
*  @(#)Task.java
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
package br.com.jtech.tasklist.application.core.domains;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
* class Task 
* 
* @author jtech
*/
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private String id;
    private String title;
    private String description;
    private Boolean completed;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Task of(TaskEntity entity) {
        return Task.builder()
            .id(entity.getId().toString())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .completed(entity.getCompleted())
            .userId(entity.getUser().getId().toString())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public TaskEntity toEntity() {
        return TaskEntity.builder()
            .id(id != null ? UUID.fromString(id) : null)
            .title(title)
            .description(description)
            .completed(completed != null ? completed : false)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}

