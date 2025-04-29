package com.imenbank.imenbank.repository;

import com.imenbank.imenbank.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    
    Optional<Request> findByRequestNumber(String requestNumber);
    
    List<Request> findByRequestDate(LocalDate requestDate);
    
    List<Request> findByType(String type);
    
    List<Request> findByStatus(String status);
    
    List<Request> findByConfirmed(Boolean confirmed);
    
    List<Request> findByDriverId(Long driverId);
    
    List<Request> findByTransporterId(Long transporterId);
    
    List<Request> findByCreatedById(Long userId);
} 