package com.imenbank.imenbank.service;

import com.imenbank.imenbank.dto.DriverDTO;
import com.imenbank.imenbank.dto.TransporterDTO;

import java.util.List;

public interface TeamService {
    
    // Driver management
    DriverDTO createDriver(DriverDTO driverDTO);
    
    DriverDTO updateDriver(Long id, DriverDTO driverDTO);
    
    void deleteDriver(Long id);
    
    DriverDTO getDriverById(Long id);
    
    DriverDTO getDriverByMatricule(String matricule);
    
    DriverDTO getDriverByCin(String cin);
    
    List<DriverDTO> getAllDrivers();
    
    List<DriverDTO> getAvailableDrivers();
    
    DriverDTO toggleDriverAvailability(Long id);
    
    // Transporter management
    TransporterDTO createTransporter(TransporterDTO transporterDTO);
    
    TransporterDTO updateTransporter(Long id, TransporterDTO transporterDTO);
    
    void deleteTransporter(Long id);
    
    TransporterDTO getTransporterById(Long id);
    
    TransporterDTO getTransporterByMatricule(String matricule);
    
    TransporterDTO getTransporterByCin(String cin);
    
    List<TransporterDTO> getAllTransporters();
    
    List<TransporterDTO> getAvailableTransporters();
    
    TransporterDTO toggleTransporterAvailability(Long id);
    
    // Team assignment
    DriverDTO assignDriverToRequest(Long driverId, Long requestId);
    
    TransporterDTO assignTransporterToRequest(Long transporterId, Long requestId);
    
    void unassignDriverFromRequest(Long driverId, Long requestId);
    
    void unassignTransporterFromRequest(Long transporterId, Long requestId);
    
    List<DriverDTO> getDriversByRequestId(Long requestId);
    
    List<TransporterDTO> getTransportersByRequestId(Long requestId);
} 