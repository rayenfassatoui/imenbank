package com.imenbank.imenbank.repository;

import com.imenbank.imenbank.model.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransporterRepository extends JpaRepository<Transporter, Long> {
    
    Optional<Transporter> findByMatricule(String matricule);
    
    Optional<Transporter> findByCin(String cin);
    
    List<Transporter> findByLastNameContainingIgnoreCase(String lastName);
    
    List<Transporter> findByVehicleType(String vehicleType);
} 