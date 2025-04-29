package com.imenbank.imenbank.service.impl;

import com.imenbank.imenbank.dto.DriverDTO;
import com.imenbank.imenbank.dto.TransporterDTO;
import com.imenbank.imenbank.exception.BadRequestException;
import com.imenbank.imenbank.exception.ResourceNotFoundException;
import com.imenbank.imenbank.model.Driver;
import com.imenbank.imenbank.model.Request;
import com.imenbank.imenbank.model.Transporter;
import com.imenbank.imenbank.repository.DriverRepository;
import com.imenbank.imenbank.repository.RequestRepository;
import com.imenbank.imenbank.repository.TransporterRepository;
import com.imenbank.imenbank.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final DriverRepository driverRepository;
    private final TransporterRepository transporterRepository;
    private final RequestRepository requestRepository;

    // DRIVER MANAGEMENT

    @Override
    @Transactional
    public DriverDTO createDriver(DriverDTO driverDTO) {
        // Validate driver data
        validateDriverData(driverDTO);
        
        // Create new driver
        Driver driver = Driver.builder()
                .matricule(driverDTO.getMatricule())
                .firstName(driverDTO.getFirstName())
                .lastName(driverDTO.getLastName())
                .cin(driverDTO.getCin())
                .licenseNumber(driverDTO.getLicenseNumber())
                .available(driverDTO.isAvailable())
                .build();
        
        // Save driver
        Driver savedDriver = driverRepository.save(driver);
        
        // Return DTO
        return mapToDriverDTO(savedDriver);
    }

    @Override
    @Transactional
    public DriverDTO updateDriver(Long id, DriverDTO driverDTO) {
        // Find existing driver
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        
        // Check for unique constraints if changing cin or matricule
        if (!driver.getCin().equals(driverDTO.getCin())) {
            driverRepository.findByCin(driverDTO.getCin())
                    .ifPresent(d -> {
                        throw new BadRequestException("CIN already exists: " + driverDTO.getCin());
                    });
        }
        
        if (!driver.getMatricule().equals(driverDTO.getMatricule())) {
            driverRepository.findByMatricule(driverDTO.getMatricule())
                    .ifPresent(d -> {
                        throw new BadRequestException("Matricule already exists: " + driverDTO.getMatricule());
                    });
        }
        
        // Update driver fields
        driver.setMatricule(driverDTO.getMatricule());
        driver.setFirstName(driverDTO.getFirstName());
        driver.setLastName(driverDTO.getLastName());
        driver.setCin(driverDTO.getCin());
        driver.setLicenseNumber(driverDTO.getLicenseNumber());
        driver.setAvailable(driverDTO.isAvailable());
        
        // Save updated driver
        Driver updatedDriver = driverRepository.save(driver);
        
        // Return DTO
        return mapToDriverDTO(updatedDriver);
    }

    @Override
    @Transactional
    public void deleteDriver(Long id) {
        // Find existing driver
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        
        // Check if driver is assigned to any requests
        if (!driver.getRequests().isEmpty()) {
            throw new BadRequestException("Cannot delete driver. Driver is assigned to " + 
                    driver.getRequests().size() + " requests.");
        }
        
        // Delete driver
        driverRepository.delete(driver);
    }

    @Override
    public DriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        
        return mapToDriverDTO(driver);
    }

    @Override
    public DriverDTO getDriverByMatricule(String matricule) {
        Driver driver = driverRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "matricule", matricule));
        
        return mapToDriverDTO(driver);
    }

    @Override
    public DriverDTO getDriverByCin(String cin) {
        Driver driver = driverRepository.findByCin(cin)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "cin", cin));
        
        return mapToDriverDTO(driver);
    }

    @Override
    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(this::mapToDriverDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverDTO> getAvailableDrivers() {
        return driverRepository.findAll().stream()
                .filter(Driver::isAvailable)
                .map(this::mapToDriverDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DriverDTO toggleDriverAvailability(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        
        // If driver has active requests and trying to set to unavailable
        if (!driver.isAvailable() && !driver.getRequests().isEmpty()) {
            // Check if any requests are not completed or cancelled
            boolean hasActiveRequests = driver.getRequests().stream()
                    .anyMatch(r -> !r.getStatus().equals("COMPLETED") && !r.getStatus().equals("CANCELLED"));
            
            if (hasActiveRequests) {
                throw new BadRequestException("Cannot set driver to unavailable. Driver has active requests.");
            }
        }
        
        // Toggle availability
        driver.setAvailable(!driver.isAvailable());
        
        Driver updatedDriver = driverRepository.save(driver);
        
        return mapToDriverDTO(updatedDriver);
    }

    // TRANSPORTER MANAGEMENT

    @Override
    @Transactional
    public TransporterDTO createTransporter(TransporterDTO transporterDTO) {
        // Validate transporter data
        validateTransporterData(transporterDTO);
        
        // Create new transporter
        Transporter transporter = Transporter.builder()
                .matricule(transporterDTO.getMatricule())
                .firstName(transporterDTO.getFirstName())
                .lastName(transporterDTO.getLastName())
                .cin(transporterDTO.getCin())
                .vehicleType(transporterDTO.getVehicleType())
                .available(transporterDTO.isAvailable())
                .build();
        
        // Save transporter
        Transporter savedTransporter = transporterRepository.save(transporter);
        
        // Return DTO
        return mapToTransporterDTO(savedTransporter);
    }

    @Override
    @Transactional
    public TransporterDTO updateTransporter(Long id, TransporterDTO transporterDTO) {
        // Find existing transporter
        Transporter transporter = transporterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", id));
        
        // Check for unique constraints if changing cin or matricule
        if (!transporter.getCin().equals(transporterDTO.getCin())) {
            transporterRepository.findByCin(transporterDTO.getCin())
                    .ifPresent(t -> {
                        throw new BadRequestException("CIN already exists: " + transporterDTO.getCin());
                    });
        }
        
        if (!transporter.getMatricule().equals(transporterDTO.getMatricule())) {
            transporterRepository.findByMatricule(transporterDTO.getMatricule())
                    .ifPresent(t -> {
                        throw new BadRequestException("Matricule already exists: " + transporterDTO.getMatricule());
                    });
        }
        
        // Update transporter fields
        transporter.setMatricule(transporterDTO.getMatricule());
        transporter.setFirstName(transporterDTO.getFirstName());
        transporter.setLastName(transporterDTO.getLastName());
        transporter.setCin(transporterDTO.getCin());
        transporter.setVehicleType(transporterDTO.getVehicleType());
        transporter.setAvailable(transporterDTO.isAvailable());
        
        // Save updated transporter
        Transporter updatedTransporter = transporterRepository.save(transporter);
        
        // Return DTO
        return mapToTransporterDTO(updatedTransporter);
    }

    @Override
    @Transactional
    public void deleteTransporter(Long id) {
        // Find existing transporter
        Transporter transporter = transporterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", id));
        
        // Check if transporter is assigned to any requests
        if (!transporter.getRequests().isEmpty()) {
            throw new BadRequestException("Cannot delete transporter. Transporter is assigned to " + 
                    transporter.getRequests().size() + " requests.");
        }
        
        // Delete transporter
        transporterRepository.delete(transporter);
    }

    @Override
    public TransporterDTO getTransporterById(Long id) {
        Transporter transporter = transporterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", id));
        
        return mapToTransporterDTO(transporter);
    }

    @Override
    public TransporterDTO getTransporterByMatricule(String matricule) {
        Transporter transporter = transporterRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "matricule", matricule));
        
        return mapToTransporterDTO(transporter);
    }

    @Override
    public TransporterDTO getTransporterByCin(String cin) {
        Transporter transporter = transporterRepository.findByCin(cin)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "cin", cin));
        
        return mapToTransporterDTO(transporter);
    }

    @Override
    public List<TransporterDTO> getAllTransporters() {
        return transporterRepository.findAll().stream()
                .map(this::mapToTransporterDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransporterDTO> getAvailableTransporters() {
        return transporterRepository.findAll().stream()
                .filter(Transporter::isAvailable)
                .map(this::mapToTransporterDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransporterDTO toggleTransporterAvailability(Long id) {
        Transporter transporter = transporterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", id));
        
        // If transporter has active requests and trying to set to unavailable
        if (!transporter.isAvailable() && !transporter.getRequests().isEmpty()) {
            // Check if any requests are not completed or cancelled
            boolean hasActiveRequests = transporter.getRequests().stream()
                    .anyMatch(r -> !r.getStatus().equals("COMPLETED") && !r.getStatus().equals("CANCELLED"));
            
            if (hasActiveRequests) {
                throw new BadRequestException("Cannot set transporter to unavailable. Transporter has active requests.");
            }
        }
        
        // Toggle availability
        transporter.setAvailable(!transporter.isAvailable());
        
        Transporter updatedTransporter = transporterRepository.save(transporter);
        
        return mapToTransporterDTO(updatedTransporter);
    }

    // TEAM ASSIGNMENT

    @Override
    @Transactional
    public DriverDTO assignDriverToRequest(Long driverId, Long requestId) {
        // Find driver and request
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        // Check if driver is available
        if (!driver.isAvailable()) {
            throw new BadRequestException("Driver is not available for assignment");
        }
        
        // Check if request already has a driver
        if (request.getDriver() != null) {
            throw new BadRequestException("Request already has a driver assigned");
        }
        
        // Assign driver to request
        request.setDriver(driver);
        driver.getRequests().add(request);
        
        // Update status if needed (both driver and transporter assigned)
        if (request.getTransporter() != null) {
            request.setStatus("ASSIGNED");
        }
        
        // Save changes
        requestRepository.save(request);
        Driver updatedDriver = driverRepository.save(driver);
        
        return mapToDriverDTO(updatedDriver);
    }

    @Override
    @Transactional
    public TransporterDTO assignTransporterToRequest(Long transporterId, Long requestId) {
        // Find transporter and request
        Transporter transporter = transporterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", transporterId));
        
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        // Check if transporter is available
        if (!transporter.isAvailable()) {
            throw new BadRequestException("Transporter is not available for assignment");
        }
        
        // Check if request already has a transporter
        if (request.getTransporter() != null) {
            throw new BadRequestException("Request already has a transporter assigned");
        }
        
        // Assign transporter to request
        request.setTransporter(transporter);
        transporter.getRequests().add(request);
        
        // Update status if needed (both driver and transporter assigned)
        if (request.getDriver() != null) {
            request.setStatus("ASSIGNED");
        }
        
        // Save changes
        requestRepository.save(request);
        Transporter updatedTransporter = transporterRepository.save(transporter);
        
        return mapToTransporterDTO(updatedTransporter);
    }

    @Override
    @Transactional
    public void unassignDriverFromRequest(Long driverId, Long requestId) {
        // Find driver and request
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        // Check if driver is assigned to request
        if (request.getDriver() == null || !request.getDriver().getId().equals(driverId)) {
            throw new BadRequestException("Driver is not assigned to this request");
        }
        
        // Check if request status allows unassignment
        if (request.getStatus().equals("CONFIRMED") || request.getStatus().equals("COMPLETED")) {
            throw new BadRequestException("Cannot unassign driver from a confirmed or completed request");
        }
        
        // Unassign driver from request
        request.setDriver(null);
        driver.getRequests().remove(request);
        
        // Update status
        request.setStatus("PENDING");
        
        // Save changes
        requestRepository.save(request);
        driverRepository.save(driver);
    }

    @Override
    @Transactional
    public void unassignTransporterFromRequest(Long transporterId, Long requestId) {
        // Find transporter and request
        Transporter transporter = transporterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", transporterId));
        
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        // Check if transporter is assigned to request
        if (request.getTransporter() == null || !request.getTransporter().getId().equals(transporterId)) {
            throw new BadRequestException("Transporter is not assigned to this request");
        }
        
        // Check if request status allows unassignment
        if (request.getStatus().equals("CONFIRMED") || request.getStatus().equals("COMPLETED")) {
            throw new BadRequestException("Cannot unassign transporter from a confirmed or completed request");
        }
        
        // Unassign transporter from request
        request.setTransporter(null);
        transporter.getRequests().remove(request);
        
        // Update status
        request.setStatus("PENDING");
        
        // Save changes
        requestRepository.save(request);
        transporterRepository.save(transporter);
    }

    @Override
    public List<DriverDTO> getDriversByRequestId(Long requestId) {
        // Find request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        List<DriverDTO> results = new ArrayList<>();
        
        // Add the assigned driver if exists
        if (request.getDriver() != null) {
            results.add(mapToDriverDTO(request.getDriver()));
        }
        
        return results;
    }

    @Override
    public List<TransporterDTO> getTransportersByRequestId(Long requestId) {
        // Find request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        List<TransporterDTO> results = new ArrayList<>();
        
        // Add the assigned transporter if exists
        if (request.getTransporter() != null) {
            results.add(mapToTransporterDTO(request.getTransporter()));
        }
        
        return results;
    }

    // HELPER METHODS

    private void validateDriverData(DriverDTO driverDTO) {
        // Check for required fields
        if (driverDTO.getMatricule() == null || driverDTO.getMatricule().trim().isEmpty()) {
            throw new BadRequestException("Matricule is required");
        }
        
        if (driverDTO.getFirstName() == null || driverDTO.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("First name is required");
        }
        
        if (driverDTO.getLastName() == null || driverDTO.getLastName().trim().isEmpty()) {
            throw new BadRequestException("Last name is required");
        }
        
        if (driverDTO.getCin() == null || driverDTO.getCin().trim().isEmpty()) {
            throw new BadRequestException("CIN is required");
        }
        
        // Check for unique constraints
        driverRepository.findByCin(driverDTO.getCin())
                .ifPresent(d -> {
                    throw new BadRequestException("CIN already exists: " + driverDTO.getCin());
                });
        
        driverRepository.findByMatricule(driverDTO.getMatricule())
                .ifPresent(d -> {
                    throw new BadRequestException("Matricule already exists: " + driverDTO.getMatricule());
                });
    }

    private void validateTransporterData(TransporterDTO transporterDTO) {
        // Check for required fields
        if (transporterDTO.getMatricule() == null || transporterDTO.getMatricule().trim().isEmpty()) {
            throw new BadRequestException("Matricule is required");
        }
        
        if (transporterDTO.getFirstName() == null || transporterDTO.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("First name is required");
        }
        
        if (transporterDTO.getLastName() == null || transporterDTO.getLastName().trim().isEmpty()) {
            throw new BadRequestException("Last name is required");
        }
        
        if (transporterDTO.getCin() == null || transporterDTO.getCin().trim().isEmpty()) {
            throw new BadRequestException("CIN is required");
        }
        
        // Check for unique constraints
        transporterRepository.findByCin(transporterDTO.getCin())
                .ifPresent(t -> {
                    throw new BadRequestException("CIN already exists: " + transporterDTO.getCin());
                });
        
        transporterRepository.findByMatricule(transporterDTO.getMatricule())
                .ifPresent(t -> {
                    throw new BadRequestException("Matricule already exists: " + transporterDTO.getMatricule());
                });
    }

    private DriverDTO mapToDriverDTO(Driver driver) {
        DriverDTO driverDTO = new DriverDTO();
        
        driverDTO.setId(driver.getId());
        driverDTO.setMatricule(driver.getMatricule());
        driverDTO.setFirstName(driver.getFirstName());
        driverDTO.setLastName(driver.getLastName());
        driverDTO.setCin(driver.getCin());
        driverDTO.setLicenseNumber(driver.getLicenseNumber());
        driverDTO.setAvailable(driver.isAvailable());
        
        // Set request IDs
        List<Long> requestIds = driver.getRequests().stream()
                .map(Request::getId)
                .collect(Collectors.toList());
        driverDTO.setRequestIds(requestIds);
        
        return driverDTO;
    }

    private TransporterDTO mapToTransporterDTO(Transporter transporter) {
        TransporterDTO transporterDTO = new TransporterDTO();
        
        transporterDTO.setId(transporter.getId());
        transporterDTO.setMatricule(transporter.getMatricule());
        transporterDTO.setFirstName(transporter.getFirstName());
        transporterDTO.setLastName(transporter.getLastName());
        transporterDTO.setCin(transporter.getCin());
        transporterDTO.setVehicleType(transporter.getVehicleType());
        transporterDTO.setAvailable(transporter.isAvailable());
        
        // Set request IDs
        List<Long> requestIds = transporter.getRequests().stream()
                .map(Request::getId)
                .collect(Collectors.toList());
        transporterDTO.setRequestIds(requestIds);
        
        return transporterDTO;
    }
} 