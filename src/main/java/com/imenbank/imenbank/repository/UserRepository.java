package com.imenbank.imenbank.repository;

import com.imenbank.imenbank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    List<User> findByRole(String role);
    
    List<User> findByActive(boolean active);
    
    List<User> findByLastNameContainingIgnoreCase(String lastName);
    
    boolean existsByUsername(String username);
} 