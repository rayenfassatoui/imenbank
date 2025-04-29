package com.imenbank.imenbank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transporters")
@Data
@EqualsAndHashCode(callSuper = true)
public class Transporter extends Employee {
    
    private String vehicleType;
    
    @OneToMany(mappedBy = "transporter")
    private Set<Request> requests = new HashSet<>();
} 