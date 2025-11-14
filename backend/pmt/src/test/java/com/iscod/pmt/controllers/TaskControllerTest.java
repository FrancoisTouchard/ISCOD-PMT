package com.iscod.pmt.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscod.pmt.models.*;
import com.iscod.pmt.services.TaskService;

class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testFindAll() throws Exception {
        when(taskService.findAll()).thenReturn(List.of(new Task()));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
                .andExpect(status().isOk());

        verify(taskService).findAll();
    }

    @Test
    void testGetTasksByProjectId() throws Exception {
        UUID id = UUID.randomUUID();
        when(taskService.findTasksByProjectId(id)).thenReturn(List.of(new Task()));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/project/" + id))
                .andExpect(status().isOk());

        verify(taskService).findTasksByProjectId(id);
    }

    @Test
    void testAddTask() throws Exception {
        UUID projectId = UUID.randomUUID();

        Map<String, Object> json = new HashMap<>();
        json.put("name", "Test task");
        json.put("dueDate", LocalDate.now().toString());
        json.put("priority", "HIGH");
        json.put("status", "DONE");

        Task newTask = new Task();
        newTask.setId(UUID.randomUUID());

        when(taskService.addTask(
                eq(projectId),
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any()))
                .thenReturn(newTask);

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/project/" + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(json)))
                .andExpect(status().isCreated());

        verify(taskService).addTask(eq(projectId), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testPartialUpdate() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Map<String, Object> updates = Map.of("name", "New name");

        Task updatedTask = new Task();
        updatedTask.setId(taskId);

        when(taskService.partialUpdate(eq(taskId), eq(projectId), eq(userId), any()))
                .thenReturn(updatedTask);

        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/project/" + projectId + "/" + taskId)
                .param("currentUserId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updates)))
                .andExpect(status().isOk());

        verify(taskService).partialUpdate(eq(taskId), eq(projectId), eq(userId), any());
    }

    @Test
    void testDelete() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/" + taskId))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTaskById(taskId);
    }
}
