package com.releasetracker.service;

import com.releasetracker.exception.InvalidEnvironmentException;
import com.releasetracker.exception.ReleaseNotFoundException;
import com.releasetracker.exception.UserAlreadyExistsException;
import com.releasetracker.model.Environment;
import com.releasetracker.model.Release;
import com.releasetracker.model.User;
import com.releasetracker.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReleaseService {
    
    private final ReleaseRepository releaseRepository;
    private final DeploymentLogService deploymentLogService;
    
    @Autowired
    public ReleaseService(ReleaseRepository releaseRepository, DeploymentLogService deploymentLogService) {
        this.releaseRepository = releaseRepository;
        this.deploymentLogService = deploymentLogService;
    }
    
    public Release createRelease(Release release) {
        if (releaseRepository.existsByVersionNumber(release.getVersionNumber())) {
            throw new UserAlreadyExistsException("Release version already exists: " + release.getVersionNumber());
        }
        return releaseRepository.save(release);
    }
    
    public List<Release> getAllReleases() {
        return releaseRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Release getReleaseById(Long id) {
        return releaseRepository.findById(id)
                .orElseThrow(() -> new ReleaseNotFoundException("Release not found with id: " + id));
    }
    
    public Release getReleaseByVersion(String versionNumber) {
        return releaseRepository.findByVersionNumber(versionNumber)
                .orElseThrow(() -> new ReleaseNotFoundException("Release not found with version: " + versionNumber));
    }
    
    public List<Release> getReleasesByEnvironment(Environment environment) {
        return releaseRepository.findByCurrentEnvironment(environment);
    }
    
    public List<Release> getReleasesByOwner(User owner) {
        return releaseRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }
    
    public Release promoteRelease(Long releaseId, User promotedBy) {
        Release release = getReleaseById(releaseId);
        Environment currentEnv = release.getCurrentEnvironment();
        Environment nextEnv = currentEnv.getNext();
        
        if (nextEnv == null) {
            throw new InvalidEnvironmentException("Release is already in production and cannot be promoted further");
        }
        
        release.setCurrentEnvironment(nextEnv);
        Release updatedRelease = releaseRepository.save(release);
        
        deploymentLogService.logDeployment(updatedRelease, nextEnv, promotedBy, true, "Release promoted from " + currentEnv + " to " + nextEnv);
        
        return updatedRelease;
    }
    
    public Release rollbackRelease(Long releaseId, User rolledBackBy) {
        Release release = getReleaseById(releaseId);
        Environment currentEnv = release.getCurrentEnvironment();
        Environment previousEnv = currentEnv.getPrevious();
        
        if (previousEnv == null) {
            throw new InvalidEnvironmentException("Release is in development and cannot be rolled back further");
        }
        
        release.setCurrentEnvironment(previousEnv);
        Release updatedRelease = releaseRepository.save(release);
        
        deploymentLogService.logDeployment(updatedRelease, previousEnv, rolledBackBy, true, "Release rolled back from " + currentEnv + " to " + previousEnv);
        
        return updatedRelease;
    }
    
    public Release updateRelease(Long id, Release updatedRelease) {
        Release existingRelease = getReleaseById(id);
        
        if (!existingRelease.getVersionNumber().equals(updatedRelease.getVersionNumber()) 
            && releaseRepository.existsByVersionNumber(updatedRelease.getVersionNumber())) {
            throw new UserAlreadyExistsException("Release version already exists: " + updatedRelease.getVersionNumber());
        }
        
        existingRelease.setVersionNumber(updatedRelease.getVersionNumber());
        existingRelease.setDescription(updatedRelease.getDescription());
        existingRelease.setOwner(updatedRelease.getOwner());
        
        return releaseRepository.save(existingRelease);
    }
    
    public void deleteRelease(Long id) {
        if (!releaseRepository.existsById(id)) {
            throw new ReleaseNotFoundException("Release not found with id: " + id);
        }
        releaseRepository.deleteById(id);
    }
}