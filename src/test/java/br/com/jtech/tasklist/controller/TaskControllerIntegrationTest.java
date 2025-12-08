/*
*  @(#)TaskControllerIntegrationTest.java
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
package br.com.jtech.tasklist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
* class TaskControllerIntegrationTest 
* 
* @author jtech
*/
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login to get token
        String registerBody = """
            {
                "name": "Task User",
                "email": "taskuser@example.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = """
            {
                "email": "taskuser@example.com",
                "password": "password123"
            }
            """;

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        accessToken = objectMapper.readTree(response).get("accessToken").asText();
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        String taskBody = """
            {
                "title": "Test Task",
                "description": "Test Description",
                "completed": false
            }
            """;

        mockMvc.perform(post("/api/v1/tasks")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        String taskBody = """
            {
                "title": "Test Task",
                "description": "Test Description"
            }
            """;

        int status = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskBody))
                .andReturn()
                .getResponse()
                .getStatus();
        
        // Spring Security pode retornar 401 (Unauthorized) ou 403 (Forbidden) quando não há token
        assertTrue(status == 401 || status == 403, "Status deve ser 401 ou 403");
    }

    @Test
    void shouldFindAllTasks() throws Exception {
        // Create a task first
        String taskBody = """
            {
                "title": "Task 1",
                "description": "Description 1"
            }
            """;

        mockMvc.perform(post("/api/v1/tasks")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskBody))
                .andExpect(status().isCreated());

        // Get all tasks
        mockMvc.perform(get("/api/v1/tasks")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Task 1"));
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        // Create a task first
        String createBody = """
            {
                "title": "Original Task",
                "description": "Original Description"
            }
            """;

        String response = mockMvc.perform(post("/api/v1/tasks")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String taskId = objectMapper.readTree(response).get("id").asText();

        // Update the task
        String updateBody = """
            {
                "title": "Updated Task",
                "description": "Updated Description",
                "completed": true
            }
            """;

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        // Create a task first
        String createBody = """
            {
                "title": "Task to Delete",
                "description": "Description"
            }
            """;

        String response = mockMvc.perform(post("/api/v1/tasks")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String taskId = objectMapper.readTree(response).get("id").asText();

        // Delete the task
        mockMvc.perform(delete("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        // Verify task is deleted
        mockMvc.perform(get("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
}

