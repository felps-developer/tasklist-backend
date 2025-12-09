/*
*  @(#)TaskListControllerIntegrationTest.java
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
* class TaskListControllerIntegrationTest 
* 
* @author jtech
*/
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskListControllerIntegrationTest {

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
                "name": "TaskList User",
                "email": "tasklistuser@example.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = """
            {
                "email": "tasklistuser@example.com",
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
    void shouldCreateTaskListSuccessfully() throws Exception {
        String taskListBody = """
            {
                "name": "Test List"
            }
            """;

        mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskListBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test List"));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        String taskListBody = """
            {
                "name": "Test List"
            }
            """;

        int status = mockMvc.perform(post("/api/v1/task-lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskListBody))
                .andReturn()
                .getResponse()
                .getStatus();
        
        assertTrue(status == 401 || status == 403, "Status deve ser 401 ou 403");
    }

    @Test
    void shouldFindAllTaskLists() throws Exception {
        // Create task lists first
        String taskListBody1 = """
            {
                "name": "List 1"
            }
            """;

        String taskListBody2 = """
            {
                "name": "List 2"
            }
            """;

        mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskListBody1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskListBody2))
                .andExpect(status().isCreated());

        // Get all task lists
        mockMvc.perform(get("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.size").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void shouldFindAllTaskListsWithoutPagination() throws Exception {
        // Create task lists first
        String taskListBody1 = """
            {
                "name": "List 1"
            }
            """;

        String taskListBody2 = """
            {
                "name": "List 2"
            }
            """;

        mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskListBody1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskListBody2))
                .andExpect(status().isCreated());

        // Get all task lists without pagination
        mockMvc.perform(get("/api/v1/task-lists/all")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void shouldFindTaskListByIdSuccessfully() throws Exception {
        // Create a task list first
        String createBody = """
            {
                "name": "List to Find"
            }
            """;

        String response = mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String taskListId = objectMapper.readTree(response).get("id").asText();

        // Get task list by ID
        mockMvc.perform(get("/api/v1/task-lists/" + taskListId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskListId))
                .andExpect(jsonPath("$.name").value("List to Find"));
    }

    @Test
    void shouldReturnNotFoundWhenTaskListNotFound() throws Exception {
        String nonExistentId = "00000000-0000-0000-0000-000000000000";
        
        mockMvc.perform(get("/api/v1/task-lists/" + nonExistentId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateTaskListSuccessfully() throws Exception {
        // Create a task list first
        String createBody = """
            {
                "name": "Original List"
            }
            """;

        String response = mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String taskListId = objectMapper.readTree(response).get("id").asText();

        // Update the task list
        String updateBody = """
            {
                "name": "Updated List"
            }
            """;

        mockMvc.perform(put("/api/v1/task-lists/" + taskListId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated List"));
    }

    @Test
    void shouldDeleteTaskListSuccessfully() throws Exception {
        // Create a task list first
        String createBody = """
            {
                "name": "List to Delete"
            }
            """;

        String response = mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String taskListId = objectMapper.readTree(response).get("id").asText();

        // Delete the task list
        mockMvc.perform(delete("/api/v1/task-lists/" + taskListId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        // Verify task list is deleted
        mockMvc.perform(get("/api/v1/task-lists/" + taskListId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindTaskListsWithPagination() throws Exception {
        // Create multiple task lists
        for (int i = 1; i <= 5; i++) {
            String taskListBody = String.format("""
                {
                    "name": "List %d"
                }
                """, i);

            mockMvc.perform(post("/api/v1/task-lists")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskListBody))
                    .andExpect(status().isCreated());
        }

        // Get task lists with pagination
        mockMvc.perform(get("/api/v1/task-lists")
                .param("page", "0")
                .param("size", "2")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        String invalidBody = """
            {
                "name": ""
            }
            """;

        mockMvc.perform(post("/api/v1/task-lists")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
                .andExpect(status().isBadRequest());
    }
}

