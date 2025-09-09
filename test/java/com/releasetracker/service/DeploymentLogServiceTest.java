package com.releasetracker.service;

import com.releasetracker.model.*;
import com.releasetracker.repository.DeploymentLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeploymentLogServiceTest {

    @Mock
    private DeploymentLogRepository deploymentLogRepository;

    @InjectMocks
    private DeploymentLogService deploymentLogService;

    private DeploymentLog testLog;
    private Release testRelease;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("john.doe", "john@example.com", "John Doe", UserRole.DEVELOPER);
        testUser.setId(1L);
        
        testRelease = new Release("1.0.0", "Initial release", testUser);
        testRelease.setId(1L);
        
        testLog = new DeploymentLog(testRelease, Environment.DEV, testUser, true, "Successful deployment");
        testLog.setId(1L);
    }

    @Test
    void createDeploymentLog_Success() {
        when(deploymentLogRepository.save(any(DeploymentLog.class))).thenReturn(testLog);

        DeploymentLog createdLog = deploymentLogService.createDeploymentLog(testLog);

        assertNotNull(createdLog);
        assertEquals(testLog.getId(), createdLog.getId());
        verify(deploymentLogRepository).save(testLog);
    }

    @Test
    void logDeployment_WithoutNotes_Success() {
        when(deploymentLogRepository.save(any(DeploymentLog.class))).thenReturn(testLog);

        DeploymentLog loggedDeployment = deploymentLogService.logDeployment(
            testRelease, Environment.DEV, testUser, true
        );

        assertNotNull(loggedDeployment);
        verify(deploymentLogRepository).save(any(DeploymentLog.class));
    }

    @Test
    void logDeployment_WithNotes_Success() {
        when(deploymentLogRepository.save(any(DeploymentLog.class))).thenReturn(testLog);

        DeploymentLog loggedDeployment = deploymentLogService.logDeployment(
            testRelease, Environment.DEV, testUser, true, "Test deployment"
        );

        assertNotNull(loggedDeployment);
        verify(deploymentLogRepository).save(any(DeploymentLog.class));
    }

    @Test
    void getAllDeploymentLogs_ReturnsAllLogs() {
        List<DeploymentLog> expectedLogs = Arrays.asList(testLog);
        when(deploymentLogRepository.findAllByOrderByDeploymentTimestampDesc()).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getAllDeploymentLogs();

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findAllByOrderByDeploymentTimestampDesc();
    }

    @Test
    void getDeploymentLogsByRelease_ReturnsFilteredLogs() {
        List<DeploymentLog> expectedLogs = Arrays.asList(testLog);
        when(deploymentLogRepository.findByReleaseOrderByDeploymentTimestampDesc(testRelease)).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getDeploymentLogsByRelease(testRelease);

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findByReleaseOrderByDeploymentTimestampDesc(testRelease);
    }

    @Test
    void getDeploymentLogsByEnvironment_ReturnsFilteredLogs() {
        List<DeploymentLog> expectedLogs = Arrays.asList(testLog);
        when(deploymentLogRepository.findByEnvironmentOrderByDeploymentTimestampDesc(Environment.DEV)).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getDeploymentLogsByEnvironment(Environment.DEV);

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findByEnvironmentOrderByDeploymentTimestampDesc(Environment.DEV);
    }

    @Test
    void getDeploymentLogsByUser_ReturnsFilteredLogs() {
        List<DeploymentLog> expectedLogs = Arrays.asList(testLog);
        when(deploymentLogRepository.findByDeployedByOrderByDeploymentTimestampDesc(testUser)).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getDeploymentLogsByUser(testUser);

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findByDeployedByOrderByDeploymentTimestampDesc(testUser);
    }

    @Test
    void getDeploymentLogsBySuccess_ReturnsFilteredLogs() {
        List<DeploymentLog> expectedLogs = Arrays.asList(testLog);
        when(deploymentLogRepository.findBySuccessOrderByDeploymentTimestampDesc(true)).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getDeploymentLogsBySuccess(true);

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findBySuccessOrderByDeploymentTimestampDesc(true);
    }

    @Test
    void getFailedDeployments_ReturnsFailedLogs() {
        DeploymentLog failedLog = new DeploymentLog(testRelease, Environment.DEV, testUser, false, "Failed deployment");
        List<DeploymentLog> expectedLogs = Arrays.asList(failedLog);
        when(deploymentLogRepository.findBySuccessOrderByDeploymentTimestampDesc(false)).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getFailedDeployments();

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findBySuccessOrderByDeploymentTimestampDesc(false);
    }

    @Test
    void getSuccessfulDeployments_ReturnsSuccessfulLogs() {
        List<DeploymentLog> expectedLogs = Arrays.asList(testLog);
        when(deploymentLogRepository.findBySuccessOrderByDeploymentTimestampDesc(true)).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getSuccessfulDeployments();

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findBySuccessOrderByDeploymentTimestampDesc(true);
    }

    @Test
    void getDeploymentLogsByReleaseAndEnvironment_ReturnsFilteredLogs() {
        List<DeploymentLog> expectedLogs = Arrays.asList(testLog);
        when(deploymentLogRepository.findByReleaseIdAndEnvironment(1L, Environment.DEV)).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getDeploymentLogsByReleaseAndEnvironment(1L, Environment.DEV);

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findByReleaseIdAndEnvironment(1L, Environment.DEV);
    }

    @Test
    void getDeploymentLogsByDateRange_ReturnsFilteredLogs() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<DeploymentLog> expectedLogs = Arrays.asList(testLog);
        when(deploymentLogRepository.findByDeploymentTimestampBetween(startDate, endDate)).thenReturn(expectedLogs);

        List<DeploymentLog> actualLogs = deploymentLogService.getDeploymentLogsByDateRange(startDate, endDate);

        assertEquals(expectedLogs.size(), actualLogs.size());
        assertEquals(expectedLogs, actualLogs);
        verify(deploymentLogRepository).findByDeploymentTimestampBetween(startDate, endDate);
    }
}