package br.com.jtech.tasklist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
* class TaskListResponse 
* 
* @author jtech
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListResponse {

    private String id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

