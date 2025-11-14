package com.iscod.pmt.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import com.iscod.pmt.models.*;
import com.iscod.pmt.repositories.HistoryEntryRepository;

class HistoryEntryServiceImplTest {

    @Mock
    private HistoryEntryRepository historyEntryRepository;

    @InjectMocks
    private HistoryEntryServiceImpl historyService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        HistoryEntry entry = new HistoryEntry();
        when(historyEntryRepository.findAll()).thenReturn(List.of(entry));

        List<HistoryEntry> result = historyService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void testCreateHistoryEntry_ValueChanged() {
        Task task = new Task();
        task.setProject(new Project());
        task.setId(UUID.randomUUID());

        AppUser user = new AppUser();

        historyService.createHistoryEntry(task, user, "name", "old", "new");

        verify(historyEntryRepository).save(any());
    }

    @Test
    void testCreateHistoryEntry_NoChange() {
        Task task = new Task();
        task.setProject(new Project());

        AppUser user = new AppUser();

        historyService.createHistoryEntry(task, user, "name", "same", "same");

        verify(historyEntryRepository, never()).save(any());
    }
}
