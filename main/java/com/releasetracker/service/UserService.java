package com.releasetracker.service;

import com.releasetracker.exception.UserAlreadyExistsException;
import com.releasetracker.exception.UserNotFoundException;
import com.releasetracker.model.User;
import com.releasetracker.model.UserRole;
import com.releasetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }
    
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);
        
        if (!existingUser.getUsername().equals(updatedUser.getUsername()) 
            && userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + updatedUser.getUsername());
        }
        
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) 
            && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + updatedUser.getEmail());
        }
        
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setRole(updatedUser.getRole());
        
        return userRepository.save(existingUser);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}