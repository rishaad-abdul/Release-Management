package com.releasetracker.controller;

import com.releasetracker.model.DeploymentLog;
import com.releasetracker.model.Environment;
import com.releasetracker.model.Release;
import com.releasetracker.model.User;
import com.releasetracker.service.DeploymentLogService;
import com.releasetracker.service.ReleaseService;
import com.releasetracker.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/deployment-logs")
@CrossOrigin(origins = "*")
public class DeploymentLogController {
    
    private final DeploymentLogService deploymentLogService;
    private final ReleaseService releaseService;
    private final UserService userService;
    
    @Autowired
    public DeploymentLogController(DeploymentLogService deploymentLogService, 
                                   ReleaseService releaseService, 
                                   UserService userService) {
        this.deploymentLogService = deploymentLogService;
        this.releaseService = releaseService;
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<?> createDeploymentLog(@Valid @RequestBody DeploymentLog deploymentLog) {
        try {
            DeploymentLog createdLog = deploymentLogService.createDeploymentLog(deploymentLog);
            return new ResponseEntity<>(createdLog, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating deployment log", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/log")
    public ResponseEntity<?> logDeployment(@RequestParam Long releaseId,
                                          @RequestParam Environment environment,
                                          @RequestParam Long deployedById,
                                          @RequestParam boolean success,
                                          @RequestParam(required = false) String notes) {
        try {
            Release release = releaseService.getReleaseById(releaseId);
            User deployedBy = userService.getUserById(deployedById);
            
            DeploymentLog log;
            if (notes != null && !notes.trim().isEmpty()) {
                log = deploymentLogService.logDeployment(release, environment, deployedBy, success, notes);
            } else {
                log = deploymentLogService.logDeployment(release, environment, deployedBy, success);
            }
            
            return new ResponseEntity<>(log, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error logging deployment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<DeploymentLog>> getAllDeploymentLogs() {
        List<DeploymentLog> logs = deploymentLogService.getAllDeploymentLogs();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
    
    @GetMapping("/release/{releaseId}")
    public ResponseEntity<?> getDeploymentLogsByRelease(@PathVariable Long releaseId) {
        try {
            Release release = releaseService.getReleaseById(releaseId);
            List<DeploymentLog> logs = deploymentLogService.getDeploymentLogsByRelease(release);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving deployment logs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/environment/{environment}")
    public ResponseEntity<List<DeploymentLog>> getDeploymentLogsByEnvironment(@PathVariable Environment environment) {
        List<DeploymentLog> logs = deploymentLogService.getDeploymentLogsByEnvironment(environment);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDeploymentLogsByUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            List<DeploymentLog> logs = deploymentLogService.getDeploymentLogsByUser(user);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving deployment logs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/release/{releaseId}/environment/{environment}")
    public ResponseEntity<List<DeploymentLog>> getDeploymentLogsByReleaseAndEnvironment(
            @PathVariable Long releaseId, 
            @PathVariable Environment environment) {
        List<DeploymentLog> logs = deploymentLogService.getDeploymentLogsByReleaseAndEnvironment(releaseId, environment);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
    
    @GetMapping("/failed")
    public ResponseEntity<List<DeploymentLog>> getFailedDeployments() {
        List<DeploymentLog> logs = deploymentLogService.getFailedDeployments();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
    
    @GetMapping("/successful")
    public ResponseEntity<List<DeploymentLog>> getSuccessfulDeployments() {
        List<DeploymentLog> logs = deploymentLogService.getSuccessfulDeployments();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<DeploymentLog>> getDeploymentLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<DeploymentLog> logs = deploymentLogService.getDeploymentLogsByDateRange(startDate, endDate);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}