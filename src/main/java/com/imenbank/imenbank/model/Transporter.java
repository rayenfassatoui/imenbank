package com.imenbank.imenbank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transporters")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Transporter extends Employee {
    
    private String vehicleType;
    
    private boolean available = true;
    
    @OneToMany(mappedBy = "transporter")
    private Set<Request> requests = new HashSet<>();
} 