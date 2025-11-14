package com.iscod.pmt.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.iscod.pmt.models.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private TaskRepository taskRepository;
    
    private Project testProject;
    private AppUser testUser;
    
    @BeforeEach
    void setUp() {
        // Créer un utilisateur
        testUser = new AppUser();
        testUser.setName("Toto");       
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        entityManager.persist(testUser);
        
        // Créer un projet
        testProject = new Project();
        testProject.setName("Test Project");
        testProject.setDescription("Description");
        testProject.setStartDate(LocalDate.now());
        testProject.setCreator(testUser);
        entityManager.persist(testProject);
        
        entityManager.flush();
    }
    
    @Test
    void findByProjectId_WithExistingTasks_ShouldReturnTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setName("Task 1");
        task1.setDescription("Description 1");
        task1.setProject(testProject);
        task1.setDueDate(LocalDate.now().plusDays(7));
        task1.setPriority(TaskPriority.HIGH);
        task1.setStatus(TaskStatus.TODO);
        entityManager.persist(task1);
        
        Task task2 = new Task();
        task2.setName("Task 2");
        task2.setDescription("Description 2");
        task2.setProject(testProject);
        task2.setDueDate(LocalDate.now().plusDays(14));
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        entityManager.persist(task2);
        
        entityManager.flush();
        
        // Act
        List<Task> tasks = taskRepository.findByProjectId(testProject.getId());
        
        // Assert
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getName().equals("Task 1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getName().equals("Task 2")));
    }
    
    @Test
    void findByProjectId_WithNoTasks_ShouldReturnEmptyList() {
        // Act
        List<Task> tasks = taskRepository.findByProjectId(testProject.getId());
        
        // Assert
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }
    
    @Test
    void findByProjectId_WithNonExistingProject_ShouldReturnEmptyList() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        
        // Act
        List<Task> tasks = taskRepository.findByProjectId(nonExistingId);
        
        // Assert
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }
    
    @Test
    void save_ShouldPersistTask() {
        // Arrange
        Task task = new Task();
        task.setName("New Task");
        task.setDescription("New Description");
        task.setProject(testProject);
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setPriority(TaskPriority.LOW);
        task.setStatus(TaskStatus.TODO);
        
        // Act
        Task savedTask = taskRepository.save(task);
        entityManager.flush();
        
        // Assert
        assertNotNull(savedTask.getId());
        assertEquals("New Task", savedTask.getName());
        
        Task foundTask = entityManager.find(Task.class, savedTask.getId());
        assertNotNull(foundTask);
        assertEquals("New Task", foundTask.getName());
    }
    
    @Test
    void deleteById_ShouldRemoveTask() {
        // Arrange
        Task task = new Task();
        task.setName("Task to Delete");
        task.setDescription("Description");
        task.setProject(testProject);
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setPriority(TaskPriority.MEDIUM);
        task.setStatus(TaskStatus.TODO);
        entityManager.persist(task);
        entityManager.flush();
        
        UUID taskId = task.getId();
        
        // Act
        taskRepository.deleteById(taskId);
        entityManager.flush();
        
        // Assert
        Task deletedTask = entityManager.find(Task.class, taskId);
        assertNull(deletedTask);
    }
}