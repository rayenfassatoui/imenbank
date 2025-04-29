package com.imenbank.imenbank.repository;

import com.imenbank.imenbank.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    Optional<Driver> findByMatricule(String matricule);
    
    Optional<Driver> findByCin(String cin);
    
    List<Driver> findByLastNameContainingIgnoreCase(String lastName);
    
    List<Driver> findByLicenseNumber(String licenseNumber);
}