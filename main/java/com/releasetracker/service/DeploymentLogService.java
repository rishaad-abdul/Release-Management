package com.releasetracker.service;

import com.releasetracker.model.DeploymentLog;
import com.releasetracker.model.Environment;
import com.releasetracker.model.Release;
import com.releasetracker.model.User;
import com.releasetracker.repository.DeploymentLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeploymentLogService {
    
    private final DeploymentLogRepository deploymentLogRepository;
    
    @Autowired
    public DeploymentLogService(DeploymentLogRepository deploymentLogRepository) {
        this.deploymentLogRepository = deploymentLogRepository;
    }
    
    public DeploymentLog createDeploymentLog(DeploymentLog deploymentLog) {
        return deploymentLogRepository.save(deploymentLog);
    }
    
    public DeploymentLog logDeployment(Release release, Environment environment, User deployedBy, boolean success) {
        DeploymentLog log = new DeploymentLog(release, environment, deployedBy, success);
        return deploymentLogRepository.save(log);
    }
    
    public DeploymentLog logDeployment(Release release, Environment environment, User deployedBy, boolean success, String notes) {
        DeploymentLog log = new DeploymentLog(release, environment, deployedBy, success, notes);
        return deploymentLogRepository.save(log);
    }
    
    public List<DeploymentLog> getAllDeploymentLogs() {
        return deploymentLogRepository.findAllByOrderByDeploymentTimestampDesc();
    }
    
    public List<DeploymentLog> getDeploymentLogsByRelease(Release release) {
        return deploymentLogRepository.findByReleaseOrderByDeploymentTimestampDesc(release);
    }
    
    public List<DeploymentLog> getDeploymentLogsByEnvironment(Environment environment) {
        return deploymentLogRepository.findByEnvironmentOrderByDeploymentTimestampDesc(environment);
    }
    
    public List<DeploymentLog> getDeploymentLogsByUser(User user) {
        return deploymentLogRepository.findByDeployedByOrderByDeploymentTimestampDesc(user);
    }
    
    public List<DeploymentLog> getDeploymentLogsBySuccess(boolean success) {
        return deploymentLogRepository.findBySuccessOrderByDeploymentTimestampDesc(success);
    }
    
    public List<DeploymentLog> getDeploymentLogsByReleaseAndEnvironment(Long releaseId, Environment environment) {
        return deploymentLogRepository.findByReleaseIdAndEnvironment(releaseId, environment);
    }
    
    public List<DeploymentLog> getDeploymentLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return deploymentLogRepository.findByDeploymentTimestampBetween(startDate, endDate);
    }
    
    public List<DeploymentLog> getFailedDeployments() {
        return getDeploymentLogsBySuccess(false);
    }
    
    public List<DeploymentLog> getSuccessfulDeployments() {
        return getDeploymentLogsBySuccess(true);
    }
}