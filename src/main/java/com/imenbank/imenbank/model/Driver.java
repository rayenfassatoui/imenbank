package com.imenbank.imenbank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drivers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Driver extends Employee {
    
    private String licenseNumber;
    
    @OneToMany(mappedBy = "driver")
    private Set<Request> requests = new HashSet<>();
} 