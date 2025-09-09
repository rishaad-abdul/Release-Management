package com.releasetracker.repository;

import com.releasetracker.model.DeploymentLog;
import com.releasetracker.model.Environment;
import com.releasetracker.model.Release;
import com.releasetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeploymentLogRepository extends JpaRepository<DeploymentLog, Long> {
    List<DeploymentLog> findByReleaseOrderByDeploymentTimestampDesc(Release release);
    List<DeploymentLog> findByEnvironmentOrderByDeploymentTimestampDesc(Environment environment);
    List<DeploymentLog> findByDeployedByOrderByDeploymentTimestampDesc(User deployedBy);
    List<DeploymentLog> findBySuccessOrderByDeploymentTimestampDesc(boolean success);
    List<DeploymentLog> findAllByOrderByDeploymentTimestampDesc();
    
    @Query("SELECT dl FROM DeploymentLog dl WHERE dl.deploymentTimestamp BETWEEN :startDate AND :endDate ORDER BY dl.deploymentTimestamp DESC")
    List<DeploymentLog> findByDeploymentTimestampBetween(@Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT dl FROM DeploymentLog dl WHERE dl.release.id = :releaseId AND dl.environment = :environment ORDER BY dl.deploymentTimestamp DESC")
    List<DeploymentLog> findByReleaseIdAndEnvironment(@Param("releaseId") Long releaseId, 
                                                      @Param("environment") Environment environment);
}