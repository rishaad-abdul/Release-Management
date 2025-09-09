package com.releasetracker.service;

import com.releasetracker.exception.InvalidEnvironmentException;
import com.releasetracker.exception.ReleaseNotFoundException;
import com.releasetracker.exception.UserAlreadyExistsException;
import com.releasetracker.model.Environment;
import com.releasetracker.model.Release;
import com.releasetracker.model.User;
import com.releasetracker.model.UserRole;
import com.releasetracker.repository.ReleaseRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceTest {

    @Mock
    private ReleaseRepository releaseRepository;

    @Mock
    private DeploymentLogService deploymentLogService;

    @InjectMocks
    private ReleaseService releaseService;

    private Release testRelease;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("john.doe", "john@example.com", "John Doe", UserRole.DEVELOPER);
        testUser.setId(1L);
        
        testRelease = new Release("1.0.0", "Initial release", testUser);
        testRelease.setId(1L);
        testRelease.setCurrentEnvironment(Environment.DEV);
    }

    @Test
    void createRelease_Success() {
        when(releaseRepository.existsByVersionNumber("1.0.0")).thenReturn(false);
        when(releaseRepository.save(any(Release.class))).thenReturn(testRelease);

        Release createdRelease = releaseService.createRelease(testRelease);

        assertNotNull(createdRelease);
        assertEquals(testRelease.getVersionNumber(), createdRelease.getVersionNumber());
        verify(releaseRepository).save(testRelease);
    }

    @Test
    void createRelease_ThrowsException_WhenVersionExists() {
        when(releaseRepository.existsByVersionNumber("1.0.0")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
            UserAlreadyExistsException.class,
            () -> releaseService.createRelease(testRelease)
        );

        assertTrue(exception.getMessage().contains("Release version already exists"));
        verify(releaseRepository, never()).save(any(Release.class));
    }

    @Test
    void getAllReleases_ReturnsReleaseList() {
        List<Release> expectedReleases = Arrays.asList(testRelease);
        when(releaseRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedReleases);

        List<Release> actualReleases = releaseService.getAllReleases();

        assertEquals(expectedReleases.size(), actualReleases.size());
        assertEquals(expectedReleases, actualReleases);
        verify(releaseRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getReleaseById_Success() {
        when(releaseRepository.findById(1L)).thenReturn(Optional.of(testRelease));

        Release foundRelease = releaseService.getReleaseById(1L);

        assertNotNull(foundRelease);
        assertEquals(testRelease.getId(), foundRelease.getId());
        verify(releaseRepository).findById(1L);
    }

    @Test
    void getReleaseById_ThrowsException_WhenReleaseNotFound() {
        when(releaseRepository.findById(1L)).thenReturn(Optional.empty());

        ReleaseNotFoundException exception = assertThrows(
            ReleaseNotFoundException.class,
            () -> releaseService.getReleaseById(1L)
        );

        assertTrue(exception.getMessage().contains("Release not found with id"));
        verify(releaseRepository).findById(1L);
    }

    @Test
    void getReleasesByEnvironment_ReturnsFilteredReleases() {
        List<Release> devReleases = Arrays.asList(testRelease);
        when(releaseRepository.findByCurrentEnvironment(Environment.DEV)).thenReturn(devReleases);

        List<Release> foundReleases = releaseService.getReleasesByEnvironment(Environment.DEV);

        assertEquals(1, foundReleases.size());
        assertEquals(Environment.DEV, foundReleases.get(0).getCurrentEnvironment());
        verify(releaseRepository).findByCurrentEnvironment(Environment.DEV);
    }

    @Test
    void promoteRelease_Success() {
        when(releaseRepository.findById(1L)).thenReturn(Optional.of(testRelease));
        when(releaseRepository.save(any(Release.class))).thenReturn(testRelease);

        Release promotedRelease = releaseService.promoteRelease(1L, testUser);

        assertEquals(Environment.QA, promotedRelease.getCurrentEnvironment());
        verify(releaseRepository).findById(1L);
        verify(releaseRepository).save(testRelease);
        verify(deploymentLogService).logDeployment(any(Release.class), eq(Environment.QA), eq(testUser), eq(true), anyString());
    }

    @Test
    void promoteRelease_ThrowsException_WhenAlreadyInProduction() {
        testRelease.setCurrentEnvironment(Environment.PROD);
        when(releaseRepository.findById(1L)).thenReturn(Optional.of(testRelease));

        InvalidEnvironmentException exception = assertThrows(
            InvalidEnvironmentException.class,
            () -> releaseService.promoteRelease(1L, testUser)
        );

        assertTrue(exception.getMessage().contains("already in production"));
        verify(releaseRepository, never()).save(any(Release.class));
    }

    @Test
    void rollbackRelease_Success() {
        testRelease.setCurrentEnvironment(Environment.QA);
        when(releaseRepository.findById(1L)).thenReturn(Optional.of(testRelease));
        when(releaseRepository.save(any(Release.class))).thenReturn(testRelease);

        Release rolledBackRelease = releaseService.rollbackRelease(1L, testUser);

        assertEquals(Environment.DEV, rolledBackRelease.getCurrentEnvironment());
        verify(releaseRepository).findById(1L);
        verify(releaseRepository).save(testRelease);
        verify(deploymentLogService).logDeployment(any(Release.class), eq(Environment.DEV), eq(testUser), eq(true), anyString());
    }

    @Test
    void rollbackRelease_ThrowsException_WhenInDevelopment() {
        when(releaseRepository.findById(1L)).thenReturn(Optional.of(testRelease));

        InvalidEnvironmentException exception = assertThrows(
            InvalidEnvironmentException.class,
            () -> releaseService.rollbackRelease(1L, testUser)
        );

        assertTrue(exception.getMessage().contains("in development"));
        verify(releaseRepository, never()).save(any(Release.class));
    }

    @Test
    void deleteRelease_Success() {
        when(releaseRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> releaseService.deleteRelease(1L));

        verify(releaseRepository).existsById(1L);
        verify(releaseRepository).deleteById(1L);
    }

    @Test
    void deleteRelease_ThrowsException_WhenReleaseNotFound() {
        when(releaseRepository.existsById(1L)).thenReturn(false);

        ReleaseNotFoundException exception = assertThrows(
            ReleaseNotFoundException.class,
            () -> releaseService.deleteRelease(1L)
        );

        assertTrue(exception.getMessage().contains("Release not found with id"));
        verify(releaseRepository, never()).deleteById(1L);
    }
}