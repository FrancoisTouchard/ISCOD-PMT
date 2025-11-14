package com.iscod.pmt.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import com.iscod.pmt.exceptions.*;
import com.iscod.pmt.models.*;
import com.iscod.pmt.repositories.*;

class ContributorServiceImplTest {

    @Mock
    private ContributorRepository contributorRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContributorServiceImpl contributorService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByProject() {
        UUID id = UUID.randomUUID();
        Contributor c = new Contributor();

        when(contributorRepository.findByIdIdProject(id)).thenReturn(List.of(c));

        List<Contributor> result = contributorService.findByIdIdProject(id);

        assertEquals(1, result.size());
    }

    @Test
    void testAddContributorByEmail_Success() {
        UUID projectId = UUID.randomUUID();
        String email = "test@test.com";

        Project project = new Project();
        project.setId(projectId);

        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        user.setEmail(email);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(contributorRepository.existsById(any())).thenReturn(false);
        when(contributorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Contributor c = contributorService.addContributorByEmail(projectId, email, Role.MEMBRE);

        assertNotNull(c);
        assertEquals(project, c.getProject());
        assertEquals(user, c.getUser());
    }

    @Test
    void testAddContributor_ProjectNotFound() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            contributorService.addContributorByEmail(projectId, "a@test.com", Role.MEMBRE)
        );
    }

    @Test
    void testAddContributor_UserNotFound() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            contributorService.addContributorByEmail(projectId, "x@test.com", Role.MEMBRE)
        );
    }

    @Test
    void testAddContributor_AlreadyExists() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(contributorRepository.existsById(any())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
            contributorService.addContributorByEmail(projectId, "x@test.com", Role.MEMBRE)
        );
    }

    @Test
    void testPartialUpdate() {
        ContributorId id = new ContributorId(UUID.randomUUID(), UUID.randomUUID());
        Contributor c = new Contributor();
        when(contributorRepository.findById(id)).thenReturn(Optional.of(c));
        when(contributorRepository.save(c)).thenReturn(c);

        contributorService.partialUpdate(id, Map.of("role", Role.ADMINISTRATEUR));

        assertEquals(Role.ADMINISTRATEUR, c.getRole());
        verify(contributorRepository).save(c);
    }

    @Test
    void testDeleteById_NotExists() {
        ContributorId id = new ContributorId(UUID.randomUUID(), UUID.randomUUID());
        when(contributorRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> contributorService.deleteById(id));
    }
}
