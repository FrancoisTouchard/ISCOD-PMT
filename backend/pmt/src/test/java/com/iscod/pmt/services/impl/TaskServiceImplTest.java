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
    void partialUpdate_UpdateAssignees_ShouldNotNotifyExistingAssignees() {
        // Ajouter une assignation existante
        TaskAssignment existingAssignment = new TaskAssignment(testTask, testUser, testProject);
        testTask.getAssignments().add(existingAssignment);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("assigneeIds", Arrays.asList(userId.toString()));
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(new ContributorId(userId, projectId)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        taskService.partialUpdate(taskId, projectId, userId, updates);
        
        verify(emailService, never()).sendTaskAssignmentNotification(any(), any());
    }
    
    @Test
    void partialUpdate_UpdateAssignees_WithMultipleUsers_ShouldNotifyOnlyNew() {
        UUID user2Id = UUID.randomUUID();
        UUID user3Id = UUID.randomUUID();
        
        AppUser user2 = new AppUser();
        user2.setId(user2Id);
        user2.setEmail("user2@example.com");
        
        AppUser user3 = new AppUser();
        user3.setId(user3Id);
        user3.setEmail("user3@example.com");
        
        // user1 déjà assigné
        TaskAssignment existingAssignment = new TaskAssignment(testTask, testUser, testProject);
        testTask.getAssignments().add(existingAssignment);
        
        Contributor contributor1 = new Contributor();
        contributor1.setUser(testUser);
        
        Contributor contributor2 = new Contributor();
        contributor2.setUser(user2);
        
        Contributor contributor3 = new Contributor();
        contributor3.setUser(user3);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("assigneeIds", Arrays.asList(userId.toString(), user2Id.toString(), user3Id.toString()));
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(new ContributorId(userId, projectId)))
            .thenReturn(Optional.of(contributor1));
        when(contributorRepository.findById(new ContributorId(user2Id, projectId)))
            .thenReturn(Optional.of(contributor2));
        when(contributorRepository.findById(new ContributorId(user3Id, projectId)))
            .thenReturn(Optional.of(contributor3));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        taskService.partialUpdate(taskId, projectId, userId, updates);
        
        verify(emailService, times(1)).sendTaskAssignmentNotification(testTask, user2);
        verify(emailService, times(1)).sendTaskAssignmentNotification(testTask, user3);
        verify(emailService, never()).sendTaskAssignmentNotification(eq(testTask), eq(testUser));
        verify(emailService, times(2)).sendTaskAssignmentNotification(any(), any());
    }
    
    @Test
    void partialUpdate_UpdateAssigneesWithEmptyList_ShouldClearAssignments() {
        TaskAssignment existingAssignment = new TaskAssignment(testTask, testUser, testProject);
        testTask.getAssignments().add(existingAssignment);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("assigneeIds", new ArrayList<>());
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertTrue(testTask.getAssignments().isEmpty());
        verify(emailService, never()).sendTaskAssignmentNotification(any(), any());
    }
    
    @Test
    void partialUpdate_UpdateAssigneesWithInvalidUser_ShouldThrowException() {
        UUID invalidUserId = UUID.randomUUID();
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("assigneeIds", Arrays.asList(invalidUserId.toString()));
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(new ContributorId(userId, projectId)))
            .thenReturn(Optional.of(contributor));
        when(contributorRepository.findById(new ContributorId(invalidUserId, projectId)))
            .thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.partialUpdate(taskId, projectId, userId, updates);
        });
        
        verify(taskRepository, never()).save(any());
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
    
    @Test
    void partialUpdate_UpdateDueDateWithString_ShouldUpdateDate() {
        Map<String, Object> updates = new HashMap<>();
        LocalDate newDate = LocalDate.now().plusDays(10);
        updates.put("dueDate", newDate.toString());
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertEquals(newDate, testTask.getDueDate());
    }
    
    @Test
    void partialUpdate_UpdateDueDateWithLocalDate_ShouldUpdateDate() {
        Map<String, Object> updates = new HashMap<>();
        LocalDate newDate = LocalDate.now().plusDays(15);
        updates.put("dueDate", newDate);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertEquals(newDate, testTask.getDueDate());
    }
    
    @Test
    void partialUpdate_UpdateEndDateWithString_ShouldUpdateDate() {
        Map<String, Object> updates = new HashMap<>();
        LocalDate newDate = LocalDate.now().plusDays(20);
        updates.put("endDate", newDate.toString());
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertEquals(newDate, testTask.getEndDate());
    }
    
    @Test
    void partialUpdate_UpdateEndDateWithLocalDate_ShouldUpdateDate() {
        Map<String, Object> updates = new HashMap<>();
        LocalDate newDate = LocalDate.now().plusDays(25);
        updates.put("endDate", newDate);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertEquals(newDate, testTask.getEndDate());
    }
    
    @Test
    void partialUpdate_UpdateEndDateWithNull_ShouldSetNullDate() {
        testTask.setEndDate(LocalDate.now().plusDays(5));
        Map<String, Object> updates = new HashMap<>();
        updates.put("endDate", null);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertNull(testTask.getEndDate());
        verify(historyEntryService, times(1)).createHistoryEntry(
            eq(testTask), 
            eq(testUser), 
            eq("endDate"), 
            anyString(), 
            eq("")
        );
    }
    
    @Test
    void partialUpdate_UpdateAllFieldsAtOnce_ShouldHandleCorrectly() {
        UUID newUserId = UUID.randomUUID();
        AppUser newUser = new AppUser();
        newUser.setId(newUserId);
        newUser.setEmail("new@example.com");
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        Contributor newContributor = new Contributor();
        newContributor.setUser(newUser);
        
        LocalDate newDueDate = LocalDate.now().plusDays(14);
        LocalDate newEndDate = LocalDate.now().plusDays(21);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Completely New Name");
        updates.put("description", "Completely New Description");
        updates.put("dueDate", newDueDate);
        updates.put("endDate", newEndDate);
        updates.put("priority", "CRITICAL");
        updates.put("status", "IN_PROGRESS");
        updates.put("assigneeIds", Arrays.asList(newUserId.toString()));
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(new ContributorId(userId, projectId)))
            .thenReturn(Optional.of(contributor));
        when(contributorRepository.findById(new ContributorId(newUserId, projectId)))
            .thenReturn(Optional.of(newContributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertNotNull(result);
        verify(historyEntryService, times(7)).createHistoryEntry(any(), any(), any(), any(), any());
        verify(emailService, times(1)).sendTaskAssignmentNotification(testTask, newUser);
        verify(taskRepository, times(1)).save(testTask);
    }
    
    @Test
    void partialUpdate_WithSomeChangedAndSomeUnchangedValues_ShouldOnlyUpdateChanged() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", testTask.getName()); // inchangé
        updates.put("description", "New Description"); // changé
        updates.put("status", testTask.getStatus().toString()); // inchangé
        updates.put("priority", "HIGH"); // changé
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        // Seulement 2 entrées d'historique (description et priority)
        verify(historyEntryService, times(2)).createHistoryEntry(any(), any(), any(), any(), any());
        verify(taskRepository, times(1)).save(testTask);
    }
    
    @Test
    void partialUpdate_UpdatePriorityToCRITICAL_ShouldWork() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("priority", "CRITICAL");
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertEquals(TaskPriority.CRITICAL, testTask.getPriority());
    }
    
    @Test
    void partialUpdate_UpdateStatusToBLOCKED_ShouldWork() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "BLOCKED");
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertEquals(TaskStatus.BLOCKED, testTask.getStatus());
    }
    
    @Test
    void partialUpdate_ReassignToSameUsers_ShouldNotSendNotifications() {
        TaskAssignment assignment1 = new TaskAssignment(testTask, testUser, testProject);
        testTask.getAssignments().add(assignment1);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("assigneeIds", Arrays.asList(userId.toString()));
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(new ContributorId(userId, projectId)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        taskService.partialUpdate(taskId, projectId, userId, updates);
        
        verify(emailService, never()).sendTaskAssignmentNotification(any(), any());
    }
    
    @Test
    void partialUpdate_RemoveAllAssigneesAndAddNew_ShouldNotifyNewOnes() {
        // Configuration initiale avec un assigné
        TaskAssignment oldAssignment = new TaskAssignment(testTask, testUser, testProject);
        testTask.getAssignments().add(oldAssignment);
        
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
        
        taskService.partialUpdate(taskId, projectId, userId, updates);
        
        verify(emailService, times(1)).sendTaskAssignmentNotification(testTask, newUser);
        verify(emailService, never()).sendTaskAssignmentNotification(eq(testTask), eq(testUser));
    }
    
    @Test
    void partialUpdate_UpdateDueDateToPastDate_ShouldWork() {
        Map<String, Object> updates = new HashMap<>();
        LocalDate pastDate = LocalDate.now().minusDays(5);
        updates.put("dueDate", pastDate);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertEquals(pastDate, testTask.getDueDate());
    }
    
    @Test
    void partialUpdate_UpdateEndDateToBeforeStartDate_ShouldWork() {
        testTask.setDueDate(LocalDate.now().plusDays(10));
        
        Map<String, Object> updates = new HashMap<>();
        LocalDate earlyEndDate = LocalDate.now().plusDays(5);
        updates.put("endDate", earlyEndDate);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertEquals(earlyEndDate, testTask.getEndDate());
    }
    
    @Test
    void findAll_WithMultipleTasks_ShouldReturnAllTasks() {
        Task task2 = new Task();
        task2.setId(UUID.randomUUID());
        task2.setName("Task 2");
        
        Task task3 = new Task();
        task3.setId(UUID.randomUUID());
        task3.setName("Task 3");
        
        List<Task> tasks = Arrays.asList(testTask, task2, task3);
        when(taskRepository.findAll()).thenReturn(tasks);
        
        List<Task> result = taskService.findAll();
        
        assertNotNull(result);
        assertEquals(3, result.size());
    }
    
    @Test
    void findTasksByProjectId_WithMultipleTasks_ShouldReturnAllProjectTasks() {
        Task task2 = new Task();
        task2.setProject(testProject);
        
        List<Task> tasks = Arrays.asList(testTask, task2);
        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);
        
        List<Task> result = taskService.findTasksByProjectId(projectId);
        
        assertEquals(2, result.size());
    }
    
    @Test
    void addTask_WithAllPriorities_ShouldCreateTasks() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // Test CRITICAL
        taskService.addTask(projectId, "Task", "Desc", LocalDate.now(), 
                           TaskPriority.CRITICAL, null, TaskStatus.TODO);
        
        // Test HIGH
        taskService.addTask(projectId, "Task", "Desc", LocalDate.now(), 
                           TaskPriority.HIGH, null, TaskStatus.TODO);
        
        // Test LOW
        taskService.addTask(projectId, "Task", "Desc", LocalDate.now(), 
                           TaskPriority.LOW, null, TaskStatus.TODO);
        
        verify(taskRepository, times(3)).save(any(Task.class));
    }
    
    @Test
    void addTask_WithAllStatuses_ShouldCreateTasks() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // Test différents statuts
        taskService.addTask(projectId, "Task", "Desc", LocalDate.now(), 
                           TaskPriority.MEDIUM, null, TaskStatus.TODO);
        
        taskService.addTask(projectId, "Task", "Desc", LocalDate.now(), 
                           TaskPriority.MEDIUM, null, TaskStatus.IN_PROGRESS);
        
        taskService.addTask(projectId, "Task", "Desc", LocalDate.now(), 
                           TaskPriority.MEDIUM, null, TaskStatus.DONE);
        
        taskService.addTask(projectId, "Task", "Desc", LocalDate.now(), 
                           TaskPriority.MEDIUM, null, TaskStatus.BLOCKED);
        
        verify(taskRepository, times(4)).save(any(Task.class));
    }
    
    @Test
    void partialUpdate_WithLongDescription_ShouldWork() {
        Map<String, Object> updates = new HashMap<>();
        String longDesc = "A".repeat(1000);
        updates.put("description", longDesc);
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertNotNull(result);
        verify(taskRepository, times(1)).save(testTask);
    }
    
    @Test
    void partialUpdate_WithSpecialCharactersInName_ShouldWork() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Task with special chars: @#$%^&*()");
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertNotNull(result);
    }
    
    @Test
    void partialUpdate_UpdateMultipleFields_ShouldUpdateAllFields() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Name");
        updates.put("description", "Updated Description");
        updates.put("status", "IN_PROGRESS");
        updates.put("priority", "HIGH");
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        verify(historyEntryService, times(4)).createHistoryEntry(any(), any(), any(), any(), any());
        verify(taskRepository, times(1)).save(testTask);
    }
    
    @Test
    void partialUpdate_WithUnchangedValue_ShouldNotCreateHistoryEntry() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", testTask.getName()); // même valeur
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        verify(historyEntryService, never()).createHistoryEntry(any(), any(), any(), any(), any());
        verify(taskRepository, times(1)).save(testTask);
    }
    
    @Test
    void partialUpdate_WithEmptyUpdates_ShouldNotThrowException() {
        Map<String, Object> updates = new HashMap<>();
        
        Contributor contributor = new Contributor();
        contributor.setUser(testUser);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(contributorRepository.findById(any(ContributorId.class)))
            .thenReturn(Optional.of(contributor));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.partialUpdate(taskId, projectId, userId, updates);
        
        assertNotNull(result);
        verify(taskRepository, times(1)).save(testTask);
        verify(historyEntryService, never()).createHistoryEntry(any(), any(), any(), any(), any());
    }
    
    
    
}