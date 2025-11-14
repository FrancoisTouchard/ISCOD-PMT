package com.iscod.pmt.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscod.pmt.models.*;
import com.iscod.pmt.services.ContributorService;

class ContributorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContributorService contributorService;

    @InjectMocks
    private ContributorController contributorController;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(contributorController).build();
    }

    @Test
    void testGetContributorsByProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        when(contributorService.findByIdIdProject(projectId)).thenReturn(List.of(new Contributor()));

        mockMvc.perform(MockMvcRequestBuilders.get("/contributors/project/" + projectId))
                .andExpect(status().isOk());

        verify(contributorService).findByIdIdProject(projectId);
    }

    @Test
    void testGetContributorsByUser() throws Exception {
        UUID userId = UUID.randomUUID();
        when(contributorService.findByIdIdUser(userId)).thenReturn(List.of(new Contributor()));

        mockMvc.perform(MockMvcRequestBuilders.get("/contributors/user/" + userId))
                .andExpect(status().isOk());

        verify(contributorService).findByIdIdUser(userId);
    }

    @Test
    void testAddContributor() throws Exception {
        UUID projectId = UUID.randomUUID();
        Contributor c = new Contributor();

        Map<String, String> body = Map.of("email", "test@test.com", "role", "OBSERVATEUR");

        when(contributorService.addContributorByEmail(projectId, "test@test.com", Role.OBSERVATEUR))
                .thenReturn(c);

        mockMvc.perform(MockMvcRequestBuilders.post("/contributors/project/" + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        verify(contributorService).addContributorByEmail(projectId, "test@test.com", Role.OBSERVATEUR);
    }

    @Test
    void testPartialUpdate() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Map<String, Object> updates = Map.of("role", "MEMBRE");

        Contributor updated = new Contributor();
        when(contributorService.partialUpdate(any(), any())).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.patch(
                "/contributors/project/" + projectId + "/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updates)))
                .andExpect(status().isOk());

        verify(contributorService).partialUpdate(any(), any());
    }

    @Test
    void testDelete() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete(
                "/contributors/project/" + projectId + "/user/" + userId))
                .andExpect(status().isNoContent());

        verify(contributorService).deleteById(any());
    }
}
