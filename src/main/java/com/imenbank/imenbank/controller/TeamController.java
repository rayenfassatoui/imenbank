package com.imenbank.imenbank.controller;

import com.imenbank.imenbank.dto.DriverDTO;
import com.imenbank.imenbank.dto.TransporterDTO;
import com.imenbank.imenbank.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // DRIVER ENDPOINTS
    
    @PostMapping("/drivers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody DriverDTO driverDTO) {
        return new ResponseEntity<>(teamService.createDriver(driverDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/drivers/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DriverDTO> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverDTO driverDTO) {
        return ResponseEntity.ok(teamService.updateDriver(id, driverDTO));
    }
    
    @DeleteMapping("/drivers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        teamService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/drivers/{id}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getDriverById(id));
    }
    
    @GetMapping("/drivers/matricule/{matricule}")
    public ResponseEntity<DriverDTO> getDriverByMatricule(@PathVariable String matricule) {
        return ResponseEntity.ok(teamService.getDriverByMatricule(matricule));
    }
    
    @GetMapping("/drivers/cin/{cin}")
    public ResponseEntity<DriverDTO> getDriverByCin(@PathVariable String cin) {
        return ResponseEntity.ok(teamService.getDriverByCin(cin));
    }
    
    @GetMapping("/drivers")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        return ResponseEntity.ok(teamService.getAllDrivers());
    }
    
    @GetMapping("/drivers/available")
    public ResponseEntity<List<DriverDTO>> getAvailableDrivers() {
        return ResponseEntity.ok(teamService.getAvailableDrivers());
    }
    
    @PutMapping("/drivers/{id}/toggle-availability")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DriverDTO> toggleDriverAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.toggleDriverAvailability(id));
    }
    
    // TRANSPORTER ENDPOINTS
    
    @PostMapping("/transporters")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TransporterDTO> createTransporter(@Valid @RequestBody TransporterDTO transporterDTO) {
        return new ResponseEntity<>(teamService.createTransporter(transporterDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/transporters/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TransporterDTO> updateTransporter(
            @PathVariable Long id,
            @Valid @RequestBody TransporterDTO transporterDTO) {
        return ResponseEntity.ok(teamService.updateTransporter(id, transporterDTO));
    }
    
    @DeleteMapping("/transporters/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransporter(@PathVariable Long id) {
        teamService.deleteTransporter(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/transporters/{id}")
    public ResponseEntity<TransporterDTO> getTransporterById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTransporterById(id));
    }
    
    @GetMapping("/transporters/matricule/{matricule}")
    public ResponseEntity<TransporterDTO> getTransporterByMatricule(@PathVariable String matricule) {
        return ResponseEntity.ok(teamService.getTransporterByMatricule(matricule));
    }
    
    @GetMapping("/transporters/cin/{cin}")
    public ResponseEntity<TransporterDTO> getTransporterByCin(@PathVariable String cin) {
        return ResponseEntity.ok(teamService.getTransporterByCin(cin));
    }
    
    @GetMapping("/transporters")
    public ResponseEntity<List<TransporterDTO>> getAllTransporters() {
        return ResponseEntity.ok(teamService.getAllTransporters());
    }
    
    @GetMapping("/transporters/available")
    public ResponseEntity<List<TransporterDTO>> getAvailableTransporters() {
        return ResponseEntity.ok(teamService.getAvailableTransporters());
    }
    
    @PutMapping("/transporters/{id}/toggle-availability")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TransporterDTO> toggleTransporterAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.toggleTransporterAvailability(id));
    }
    
    // TEAM ASSIGNMENT ENDPOINTS
    
    @PutMapping("/teams/requests/{requestId}/assign-driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DriverDTO> assignDriverToRequest(
            @PathVariable Long requestId,
            @PathVariable Long driverId) {
        return ResponseEntity.ok(teamService.assignDriverToRequest(driverId, requestId));
    }
    
    @PutMapping("/teams/requests/{requestId}/assign-transporter/{transporterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TransporterDTO> assignTransporterToRequest(
            @PathVariable Long requestId,
            @PathVariable Long transporterId) {
        return ResponseEntity.ok(teamService.assignTransporterToRequest(transporterId, requestId));
    }
    
    @DeleteMapping("/teams/requests/{requestId}/unassign-driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> unassignDriverFromRequest(
            @PathVariable Long requestId,
            @PathVariable Long driverId) {
        teamService.unassignDriverFromRequest(driverId, requestId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/teams/requests/{requestId}/unassign-transporter/{transporterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> unassignTransporterFromRequest(
            @PathVariable Long requestId,
            @PathVariable Long transporterId) {
        teamService.unassignTransporterFromRequest(transporterId, requestId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/teams/requests/{requestId}/drivers")
    public ResponseEntity<List<DriverDTO>> getDriversByRequestId(@PathVariable Long requestId) {
        return ResponseEntity.ok(teamService.getDriversByRequestId(requestId));
    }
    
    @GetMapping("/teams/requests/{requestId}/transporters")
    public ResponseEntity<List<TransporterDTO>> getTransportersByRequestId(@PathVariable Long requestId) {
        return ResponseEntity.ok(teamService.getTransportersByRequestId(requestId));
    }
} 