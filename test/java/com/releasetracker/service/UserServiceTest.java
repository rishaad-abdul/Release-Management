package com.releasetracker.service;

import com.releasetracker.exception.UserAlreadyExistsException;
import com.releasetracker.exception.UserNotFoundException;
import com.releasetracker.model.User;
import com.releasetracker.model.UserRole;
import com.releasetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("john.doe", "john@example.com", "John Doe", UserRole.DEVELOPER);
        testUser.setId(1L);
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser);
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void createUser_ThrowsException_WhenUsernameExists() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
            UserAlreadyExistsException.class,
            () -> userService.createUser(testUser)
        );

        assertTrue(exception.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ThrowsException_WhenEmailExists() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
            UserAlreadyExistsException.class,
            () -> userService.createUser(testUser)
        );

        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_ReturnsUserList() {
        List<User> expectedUsers = Arrays.asList(testUser, new User("jane.doe", "jane@example.com", "Jane Doe", UserRole.QA));
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ThrowsException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> userService.getUserById(1L)
        );

        assertTrue(exception.getMessage().contains("User not found with id"));
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByUsername_Success() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserByUsername("john.doe");

        assertNotNull(foundUser);
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        verify(userRepository).findByUsername("john.doe");
    }

    @Test
    void getUsersByRole_ReturnsFilteredUsers() {
        List<User> developers = Arrays.asList(testUser);
        when(userRepository.findByRole(UserRole.DEVELOPER)).thenReturn(developers);

        List<User> foundUsers = userService.getUsersByRole(UserRole.DEVELOPER);

        assertEquals(1, foundUsers.size());
        assertEquals(UserRole.DEVELOPER, foundUsers.get(0).getRole());
        verify(userRepository).findByRole(UserRole.DEVELOPER);
    }

    @Test
    void updateUser_Success() {
        User updatedUserData = new User("john.updated", "john.updated@example.com", "John Updated", UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateUser(1L, updatedUserData);

        assertNotNull(updatedUser);
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ThrowsException_WhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> userService.deleteUser(1L)
        );

        assertTrue(exception.getMessage().contains("User not found with id"));
        verify(userRepository, never()).deleteById(1L);
    }
}