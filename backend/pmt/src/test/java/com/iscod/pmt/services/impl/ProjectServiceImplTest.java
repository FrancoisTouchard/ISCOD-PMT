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
import com.iscod.pmt.repositories.ProjectRepository;
import com.iscod.pmt.services.ContributorService;
import com.iscod.pmt.services.UserService;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private ContributorService contributorService;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private ProjectServiceImpl projectService;
    
    private Project testProject;
    private AppUser testUser;
    private UUID projectId;
    private UUID userId;
    
    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        testUser = new AppUser();
        testUser.setId(userId);
        testUser.setName("Titi");       
        testUser.setEmail("creator@example.com");
        
        testProject = new Project();
        testProject.setId(projectId);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setStartDate(LocalDate.now());
        testProject.setCreator(testUser);
    }
    
    @Test
    void findAll_ShouldReturnAllProjects() {
        // Arrange
        List<Project> projects = Arrays.asList(testProject);
        when(projectRepository.findAll()).thenReturn(projects);
        
        // Act
        List<Project> result = projectService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        verify(projectRepository, times(1)).findAll();
    }
    
    @Test
    void findById_WithExistingProject_ShouldReturnProject() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        
        // Act
        Project result = projectService.findById(projectId);
        
        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(2)).findById(projectId); // Called twice in implementation
    }
    
    @Test
    void findById_WithNonExistingProject_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.findById(projectId);
        });
    }
    
    @Test
    void create_WithValidData_ShouldCreateProjectAndAdminContributor() {
        // Arrange
        Project newProject = new Project();
        newProject.setName("New Project");
        newProject.setDescription("New Description");
        
        when(userService.findById(userId)).thenReturn(testUser);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(contributorService.create(any(Contributor.class))).thenReturn(null);
        
        // Act
        Project result = projectService.create(newProject, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getCreator());
        verify(userService, times(1)).findById(userId);
        verify(projectRepository, times(1)).save(newProject);
        verify(contributorService, times(1)).create(any(Contributor.class));
    }
    
    @Test
    void update_ShouldUpdateProject() {
        // Arrange
        Project updatedProject = new Project();
        updatedProject.setName("Updated Project");
        updatedProject.setDescription("Updated Description");
        
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        
        // Act
        projectService.update(projectId, updatedProject);
        
        // Assert
        assertEquals(projectId, updatedProject.getId());
        verify(projectRepository, times(1)).save(updatedProject);
    }
    
    @Test
    void partialUpdate_UpdateName_ShouldUpdateOnlyName() {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Partially Updated Name");
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // Act
        projectService.partialUpdate(projectId, updates);
        
        // Assert
        assertEquals("Partially Updated Name", testProject.getName());
        verify(projectRepository, times(1)).save(testProject);
    }
    
    @Test
    void partialUpdate_UpdateDescription_ShouldUpdateOnlyDescription() {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("description", "New Description");
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // Act
        projectService.partialUpdate(projectId, updates);
        
        // Assert
        assertEquals("New Description", testProject.getDescription());
        verify(projectRepository, times(1)).save(testProject);
    }
    
    @Test
    void partialUpdate_UpdateStartDate_ShouldUpdateOnlyStartDate() {
        // Arrange
        LocalDate newDate = LocalDate.of(2024, 1, 15);
        Map<String, Object> updates = new HashMap<>();
        updates.put("startDate", newDate);
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // Act
        projectService.partialUpdate(projectId, updates);
        
        // Assert
        assertEquals(newDate, testProject.getStartDate());
        verify(projectRepository, times(1)).save(testProject);
    }
    
    @Test
    void partialUpdate_UpdateMultipleFields_ShouldUpdateAll() {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "New Name");
        updates.put("description", "New Desc");
        updates.put("startDate", LocalDate.of(2024, 2, 1));
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // Act
        projectService.partialUpdate(projectId, updates);
        
        // Assert
        assertEquals("New Name", testProject.getName());
        assertEquals("New Desc", testProject.getDescription());
        assertEquals(LocalDate.of(2024, 2, 1), testProject.getStartDate());
        verify(projectRepository, times(1)).save(testProject);
    }
    
    @Test
    void deleteById_ShouldDeleteProject() {
        // Arrange
        doNothing().when(projectRepository).deleteById(projectId);
        
        // Act
        projectService.deleteById(projectId);
        
        // Assert
        verify(projectRepository, times(1)).deleteById(projectId);
    }
    
    @Test
    void getProjetsByUtilisateurId_ShouldReturnUserProjects() {
        // Arrange
        Contributor contributor = new Contributor();
        contributor.setProject(testProject);
        contributor.setUser(testUser);
        
        List<Contributor> contributors = Arrays.asList(contributor);
        when(contributorService.findByIdIdUser(userId)).thenReturn(contributors);
        
        // Act
        List<Project> result = projectService.getProjetsByUtilisateurId(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject, result.get(0));
        verify(contributorService, times(1)).findByIdIdUser(userId);
    }
    
    @Test
    void getProjetsByUtilisateurId_WithNoProjects_ShouldReturnEmptyList() {
        // Arrange
        when(contributorService.findByIdIdUser(userId)).thenReturn(new ArrayList<>());
        
        // Act
        List<Project> result = projectService.getProjetsByUtilisateurId(userId);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contributorService, times(1)).findByIdIdUser(userId);
    }
}