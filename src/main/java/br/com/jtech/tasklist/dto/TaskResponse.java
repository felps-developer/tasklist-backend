package br.com.jtech.tasklist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
* class TaskResponse 
* 
* @author jtech
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String id;
    private String title;
    private String description;
    private Boolean completed;
    private String taskListId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

