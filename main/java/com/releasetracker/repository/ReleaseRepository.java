package com.releasetracker.repository;

import com.releasetracker.model.Environment;
import com.releasetracker.model.Release;
import com.releasetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Long> {
    Optional<Release> findByVersionNumber(String versionNumber);
    List<Release> findByCurrentEnvironment(Environment environment);
    List<Release> findByOwner(User owner);
    List<Release> findByOwnerOrderByCreatedAtDesc(User owner);
    List<Release> findAllByOrderByCreatedAtDesc();
    boolean existsByVersionNumber(String versionNumber);
}