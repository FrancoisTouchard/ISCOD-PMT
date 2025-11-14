package com.iscod.pmt.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.*;
import com.iscod.pmt.repositories.*;
import com.iscod.pmt.services.EmailService;
import com.iscod.pmt.services.HistoryEntryService;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private ContributorRepository contributorRepository;
    
    @Mock
    private HistoryEntryService historyEntryService;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private TaskServiceImpl taskService;
    
    private Project testProject;
    private Task testTask;
    private AppUser testUser;
    private UUID projectId;
    private UUID taskId;
    private UUID userId;
    
    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        testProject = new Project();
        testProject.setId(projectId);
        testProject.setName("Test Project");
        
        testUser = new AppUser();
        testUser.setId(userId);
        testUser.setName("Tata");      
        testUser.setEmail("test@example.com");
        
        testTask = new Task();
        testTask.setId(taskId);
        testTask.setName("Test Task");
        testTask.setDescription("Description");
        testTask.setProject(testProject);
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setPriority(TaskPriority.MEDIUM);
        testTask.setStatus(TaskStatus.TODO);
    }
    
    @Test
    void findAll_ShouldReturnAllTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(tasks);
        
        // Act
        List<Task> result = taskService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getName());
        verify(taskRepository, times(1)).findAll();
    }
    
    @Test
    void findTasksByProjectId_ShouldReturnTasksForProject() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);
        
        // Act
        List<Task> result = taskService.findTasksByProjectId(projectId);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskId, result.get(0).getId());
        verify(taskRepository, times(1)).findByProjectId(projectId);
    }
    
    @Test
    void addTask_WithValidData_ShouldCreateTask() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // Act
        Task result = taskService.addTask(
            projectId, 
            "New Task", 
            "Description", 
            LocalDate.now().plusDays(7),
            TaskPriority.HIGH,
            null,
            TaskStatus.TODO
        );
        
        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getName());
        verify(projectRepository, times(1)).findById(projectId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    
    @Test
    void addTask_WithInvalidProject_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.addTask(
                projectId, 
                "New Task", 
                "Description", 
                LocalDate.now().plusDays(7),
                TaskPriority.HIGH,
                null,
                TaskStatus.TODO
            );
        });
        
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void deleteTaskById_WithExistingTask_ShouldDelete() {
        // Arrange
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);
        
        // Act
        taskService.deleteTaskById(taskId);
        
        // Assert
        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }
    
    @Test
    void deleteTaskById_WithNonExistingTask_ShouldThrowException() {
        // Arrange
        when(taskRepository.existsById(taskId)).thenReturn(false);
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteTaskById(taskId);
        });
        
        verify(taskRepository, never()).deleteById(any());
    }
    
    @Test
    void assignTaskToUser_WithValidData_ShouldAssignAndSendEmail() {
        // Arrange
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        contributor.setProject(testProject);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        doNothing().when(emailService).sendTaskAssignmentNotification(any(), any());
        
        // Act
        taskService.assignTaskToUser(taskId, userId, projectId);
        
        // Assert
        verify(taskRepository, times(1)).save(testTask);
        verify(emailService, times(1)).sendTaskAssignmentNotification(testTask, testUser);
    }
    
    @Test
    void partialUpdate_UpdateName_ShouldUpdateAndCreateHistory() {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Task Name");
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        doNothing().when(historyEntryService).createHistoryEntry(any(), any(), any(), any(), any());
        
        // Act
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).save(testTask);
        verify(historyEntryService, times(1)).createHistoryEntry(
            eq(testTask), 
            eq(testUser), 
            eq("name"), 
            anyString(), 
            eq("Updated Task Name")
        );
    }
    
    @Test
    void partialUpdate_UpdateStatus_ShouldUpdateStatus() {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "IN_PROGRESS");
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // Act
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        // Assert
        assertEquals(TaskStatus.IN_PROGRESS, testTask.getStatus());
        verify(taskRepository, times(1)).save(testTask);
    }
    
    @Test
    void partialUpdate_UpdateAssignees_ShouldNotifyNewAssignees() {
        // Arrange
        UUID newUserId = UUID.randomUUID();
        AppUser newUser = new AppUser();
        newUser.setId(newUserId);
        newUser.setEmail("new@example.com");
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        Contributor newContributor = new Contributor();
        newContributor.setUser(newUser);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("assigneeIds", Arrays.asList(newUserId.toString()));
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(new ContributorId(userId, projectId)))
            .thenReturn(Optional.of(contributor));
        when(contributorRepository.findById(new ContributorId(newUserId, projectId)))
            .thenReturn(Optional.of(newContributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // Act
        taskService.partialUpdate(taskId, projectId, userId, updates);
        
        // Assert
        verify(emailService, times(1)).sendTaskAssignmentNotification(testTask, newUser);
    }
    
    @Test
    void partialUpdate_WithInvalidTask_ShouldThrowException() {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.partialUpdate(taskId, projectId, userId, updates);
        });
    }
}