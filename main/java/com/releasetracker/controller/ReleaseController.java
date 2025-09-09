package com.releasetracker.controller;

import com.releasetracker.exception.InvalidEnvironmentException;
import com.releasetracker.exception.ReleaseNotFoundException;
import com.releasetracker.exception.UserAlreadyExistsException;
import com.releasetracker.model.Environment;
import com.releasetracker.model.Release;
import com.releasetracker.model.User;
import com.releasetracker.service.ReleaseService;
import com.releasetracker.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/releases")
@CrossOrigin(origins = "*")
public class ReleaseController {
    
    private final ReleaseService releaseService;
    private final UserService userService;
    
    @Autowired
    public ReleaseController(ReleaseService releaseService, UserService userService) {
        this.releaseService = releaseService;
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<?> createRelease(@Valid @RequestBody Release release) {
        try {
            Release createdRelease = releaseService.createRelease(release);
            return new ResponseEntity<>(createdRelease, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating release", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Release>> getAllReleases() {
        List<Release> releases = releaseService.getAllReleases();
        return new ResponseEntity<>(releases, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getReleaseById(@PathVariable Long id) {
        try {
            Release release = releaseService.getReleaseById(id);
            return new ResponseEntity<>(release, HttpStatus.OK);
        } catch (ReleaseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/version/{versionNumber}")
    public ResponseEntity<?> getReleaseByVersion(@PathVariable String versionNumber) {
        try {
            Release release = releaseService.getReleaseByVersion(versionNumber);
            return new ResponseEntity<>(release, HttpStatus.OK);
        } catch (ReleaseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/environment/{environment}")
    public ResponseEntity<List<Release>> getReleasesByEnvironment(@PathVariable Environment environment) {
        List<Release> releases = releaseService.getReleasesByEnvironment(environment);
        return new ResponseEntity<>(releases, HttpStatus.OK);
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getReleasesByOwner(@PathVariable Long ownerId) {
        try {
            User owner = userService.getUserById(ownerId);
            List<Release> releases = releaseService.getReleasesByOwner(owner);
            return new ResponseEntity<>(releases, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving releases", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}/promote")
    public ResponseEntity<?> promoteRelease(@PathVariable Long id, @RequestParam Long promotedById) {
        try {
            User promotedBy = userService.getUserById(promotedById);
            Release promotedRelease = releaseService.promoteRelease(id, promotedBy);
            return new ResponseEntity<>(promotedRelease, HttpStatus.OK);
        } catch (ReleaseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidEnvironmentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error promoting release", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}/rollback")
    public ResponseEntity<?> rollbackRelease(@PathVariable Long id, @RequestParam Long rolledBackById) {
        try {
            User rolledBackBy = userService.getUserById(rolledBackById);
            Release rolledBackRelease = releaseService.rollbackRelease(id, rolledBackBy);
            return new ResponseEntity<>(rolledBackRelease, HttpStatus.OK);
        } catch (ReleaseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidEnvironmentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error rolling back release", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRelease(@PathVariable Long id, @Valid @RequestBody Release release) {
        try {
            Release updatedRelease = releaseService.updateRelease(id, release);
            return new ResponseEntity<>(updatedRelease, HttpStatus.OK);
        } catch (ReleaseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating release", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRelease(@PathVariable Long id) {
        try {
            releaseService.deleteRelease(id);
            return new ResponseEntity<>("Release deleted successfully", HttpStatus.OK);
        } catch (ReleaseNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}