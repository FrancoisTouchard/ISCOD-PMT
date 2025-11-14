package com.iscod.pmt.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.iscod.pmt.models.HistoryEntry;
import com.iscod.pmt.services.HistoryEntryService;

class HistoryEntryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HistoryEntryService historyService;

    @InjectMocks
    private HistoryEntryController historyController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(historyController).build();
    }

    @Test
    void testFindAll() throws Exception {
        when(historyService.findAll()).thenReturn(List.of(new HistoryEntry()));

        mockMvc.perform(MockMvcRequestBuilders.get("/history"))
                .andExpect(status().isOk());

        verify(historyService).findAll();
    }

    @Test
    void testFindByProject() throws Exception {
        UUID id = UUID.randomUUID();
        when(historyService.findAllEntriesByProjectIdOrderedByDate(id))
                .thenReturn(List.of(new HistoryEntry()));

        mockMvc.perform(MockMvcRequestBuilders.get("/history/project/" + id))
                .andExpect(status().isOk());

        verify(historyService).findAllEntriesByProjectIdOrderedByDate(id);
    }

    @Test
    void testFindByTask() throws Exception {
        UUID id = UUID.randomUUID();
        when(historyService.findAllEntriesByTaskIdOrderedByDate(id))
                .thenReturn(List.of(new HistoryEntry()));

        mockMvc.perform(MockMvcRequestBuilders.get("/history/task/" + id))
                .andExpect(status().isOk());

        verify(historyService).findAllEntriesByTaskIdOrderedByDate(id);
    }
}
