package com.releasetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "deployment_logs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DeploymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Release is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id")
    private Release release;
    
    @NotNull(message = "Environment is required")
    @Enumerated(EnumType.STRING)
    private Environment environment;
    
    @NotNull(message = "Deployed by user is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployed_by")
    private User deployedBy;
    
    @Column(name = "deployment_timestamp")
    private LocalDateTime deploymentTimestamp;
    
    private String notes;
    
    private boolean success;
    
    @PrePersist
    protected void onCreate() {
        if (deploymentTimestamp == null) {
            deploymentTimestamp = LocalDateTime.now();
        }
    }
    
    public DeploymentLog() {}
    
    public DeploymentLog(Release release, Environment environment, User deployedBy, boolean success) {
        this.release = release;
        this.environment = environment;
        this.deployedBy = deployedBy;
        this.success = success;
        this.deploymentTimestamp = LocalDateTime.now();
    }
    
    public DeploymentLog(Release release, Environment environment, User deployedBy, boolean success, String notes) {
        this(release, environment, deployedBy, success);
        this.notes = notes;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Release getRelease() {
        return release;
    }
    
    public void setRelease(Release release) {
        this.release = release;
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    public User getDeployedBy() {
        return deployedBy;
    }
    
    public void setDeployedBy(User deployedBy) {
        this.deployedBy = deployedBy;
    }
    
    public LocalDateTime getDeploymentTimestamp() {
        return deploymentTimestamp;
    }
    
    public void setDeploymentTimestamp(LocalDateTime deploymentTimestamp) {
        this.deploymentTimestamp = deploymentTimestamp;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @Override
    public String toString() {
        return "DeploymentLog{" +
                "id=" + id +
                ", environment=" + environment +
                ", deploymentTimestamp=" + deploymentTimestamp +
                ", success=" + success +
                ", notes='" + notes + '\'' +
                '}';
    }
}