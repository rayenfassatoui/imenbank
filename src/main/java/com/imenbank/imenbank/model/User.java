package com.imenbank.imenbank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    @Column(nullable = false)
    private String role;
    
    private boolean active = true;
    
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private Set<Request> requests = new HashSet<>();
} 