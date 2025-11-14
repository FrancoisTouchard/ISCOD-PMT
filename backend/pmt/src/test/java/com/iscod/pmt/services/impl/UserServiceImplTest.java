package com.iscod.pmt.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.repositories.UserRepository;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        AppUser u = new AppUser();
        when(userRepository.findAll()).thenReturn(List.of(u));

        List<AppUser> result = userService.findAll();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testFindById_Found() {
        UUID id = UUID.randomUUID();
        AppUser user = new AppUser();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        AppUser result = userService.findById(id);

        assertEquals(user, result);
        verify(userRepository, times(2)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    void testFindByEmail_Found() {
        String email = "test@test.com";
        AppUser user = new AppUser();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        AppUser result = userService.findByEmail(email);

        assertEquals(user, result);
        verify(userRepository, times(2)).findByEmail(email);
    }

    @Test
    void testFindByEmail_NotFound() {
        String email = "unknown@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByEmail(email));
    }

    @Test
    void testCreate() {
        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        when(userRepository.save(user)).thenReturn(user);

        UUID id = userService.create(user);

        assertEquals(user.getId(), id);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        AppUser user = new AppUser();

        userService.update(id, user);

        assertEquals(id, user.getId());
        verify(userRepository).save(user);
    }

    @Test
    void testPartialUpdate() {
        UUID id = UUID.randomUUID();
        AppUser user = new AppUser();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Map<String, Object> updates = Map.of(
            "name", "John",
            "email", "john@test.com"
        );

        userService.partialUpdate(id, updates);

        assertEquals("John", user.getName());
        assertEquals("john@test.com", user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteById() {
        UUID id = UUID.randomUUID();

        userService.deleteById(id);

        verify(userRepository).deleteById(id);
    }
}
