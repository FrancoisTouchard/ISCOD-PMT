package com.iscod.pmt.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscod.pmt.models.Project;
import com.iscod.pmt.services.ProjectService;

class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testFindAll() throws Exception {
        when(projectService.findAll()).thenReturn(List.of(new Project()));

        mockMvc.perform(MockMvcRequestBuilders.get("/projects"))
                .andExpect(status().isOk());

        verify(projectService).findAll();
    }

    @Test
    void testFindById() throws Exception {
        UUID id = UUID.randomUUID();
        Project p = new Project();
        p.setId(id);

        when(projectService.findById(id)).thenReturn(p);

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(projectService).findById(id);
    }

    @Test
    void testGetProjectsByUser() throws Exception {
        UUID userId = UUID.randomUUID();
        when(projectService.getProjetsByUtilisateurId(userId)).thenReturn(List.of(new Project()));

        mockMvc.perform(MockMvcRequestBuilders.get("/projects/my-projects/" + userId))
                .andExpect(status().isOk());

        verify(projectService).getProjetsByUtilisateurId(userId);
    }

    @Test
    void testCreate() throws Exception {
        UUID userId = UUID.randomUUID();
        Project p = new Project();
        p.setName("Test Project");

        when(projectService.create(any(), eq(userId))).thenReturn(p);

        mockMvc.perform(MockMvcRequestBuilders.post("/projects")
                .param("creatorId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(p)))
                .andExpect(status().isCreated());

        verify(projectService).create(any(), eq(userId));
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        Project p = new Project();

        mockMvc.perform(MockMvcRequestBuilders.put("/projects/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(p)))
                .andExpect(status().isOk());

        verify(projectService).update(eq(id), any());
    }

    @Test
    void testPartialUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        Map<String, Object> updates = Map.of("name", "Updated");

        mockMvc.perform(MockMvcRequestBuilders.patch("/projects/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updates)))
                .andExpect(status().isOk());

        verify(projectService).partialUpdate(eq(id), any());
    }

    @Test
    void testDelete() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/projects/" + id))
                .andExpect(status().isNoContent());

        verify(projectService).deleteById(id);
    }
}
