package com.imenbank.imenbank.service.impl;

import com.imenbank.imenbank.dto.RequestDTO;
import com.imenbank.imenbank.exception.BadRequestException;
import com.imenbank.imenbank.exception.ResourceNotFoundException;
import com.imenbank.imenbank.model.Driver;
import com.imenbank.imenbank.model.Request;
import com.imenbank.imenbank.model.Transporter;
import com.imenbank.imenbank.model.User;
import com.imenbank.imenbank.repository.DriverRepository;
import com.imenbank.imenbank.repository.RequestRepository;
import com.imenbank.imenbank.repository.TransporterRepository;
import com.imenbank.imenbank.repository.UserRepository;
import com.imenbank.imenbank.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final DriverRepository driverRepository;
    private final TransporterRepository transporterRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RequestDTO createRequest(RequestDTO requestDTO) {
        // Validate request
        validateRequest(requestDTO);
        
        // Set default values
        if (requestDTO.getRequestDate() == null) {
            requestDTO.setRequestDate(LocalDate.now());
        }
        
        if (requestDTO.getStatus() == null) {
            requestDTO.setStatus("PENDING");
        }
        
        if (requestDTO.getConfirmed() == null) {
            requestDTO.setConfirmed(false);
        }
        
        // Get current user
        User currentUser = getCurrentUser();
        
        // Map DTO to entity
        Request request = mapToEntity(requestDTO);
        request.setCreatedBy(currentUser);
        
        // Save entity
        Request savedRequest = requestRepository.save(request);
        
        // Return DTO
        return mapToDTO(savedRequest);
    }

    @Override
    @Transactional
    public RequestDTO updateRequest(Long id, RequestDTO requestDTO) {
        // Find existing request
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", id));
        
        // Update fields
        request.setRequestNumber(requestDTO.getRequestNumber());
        request.setRequestCode(requestDTO.getRequestCode());
        request.setType(requestDTO.getType());
        request.setAmount(requestDTO.getAmount());
        request.setCulture(requestDTO.getCulture());
        request.setNature(requestDTO.getNature());
        request.setComments(requestDTO.getComments());
        
        // Save updated request
        Request updatedRequest = requestRepository.save(request);
        
        // Return DTO
        return mapToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public void deleteRequest(Long id) {
        // Find existing request
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", id));
        
        // Check if request can be deleted (not confirmed)
        if (Boolean.TRUE.equals(request.getConfirmed())) {
            throw new BadRequestException("Cannot delete a confirmed request");
        }
        
        // Delete request
        requestRepository.delete(request);
    }

    @Override
    public RequestDTO getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", id));
        
        return mapToDTO(request);
    }

    @Override
    public RequestDTO getRequestByNumber(String requestNumber) {
        Request request = requestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "requestNumber", requestNumber));
        
        return mapToDTO(request);
    }

    @Override
    public List<RequestDTO> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDTO> getRequestsByStatus(String status) {
        return requestRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDTO> getRequestsByType(String type) {
        return requestRepository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDTO> getRequestsByDate(LocalDate date) {
        return requestRepository.findByRequestDate(date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDTO assignDriver(Long requestId, Long driverId) {
        // Find request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        // Find driver
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        
        // Assign driver to request
        request.setDriver(driver);
        
        // Update status if needed
        updateStatusBasedOnAssignment(request);
        
        // Save request
        Request updatedRequest = requestRepository.save(request);
        
        return mapToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public RequestDTO assignTransporter(Long requestId, Long transporterId) {
        // Find request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        // Find transporter
        Transporter transporter = transporterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", transporterId));
        
        // Assign transporter to request
        request.setTransporter(transporter);
        
        // Update status if needed
        updateStatusBasedOnAssignment(request);
        
        // Save request
        Request updatedRequest = requestRepository.save(request);
        
        return mapToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public RequestDTO confirmRequest(Long requestId) {
        // Find request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        // Check if team is assigned
        if (request.getDriver() == null || request.getTransporter() == null) {
            throw new BadRequestException("Cannot confirm request without assigned driver and transporter");
        }
        
        // Confirm request
        request.setConfirmed(true);
        request.setConfirmationDate(LocalDate.now());
        request.setStatus("CONFIRMED");
        
        // Save request
        Request confirmedRequest = requestRepository.save(request);
        
        return mapToDTO(confirmedRequest);
    }

    @Override
    @Transactional
    public RequestDTO updateRequestStatus(Long requestId, String status) {
        // Find request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
        
        // Validate status
        validateStatus(status);
        
        // Update status
        request.setStatus(status);
        
        // Set confirmed if status is "CONFIRMED"
        if ("CONFIRMED".equals(status)) {
            request.setConfirmed(true);
            request.setConfirmationDate(LocalDate.now());
        }
        
        // Save request
        Request updatedRequest = requestRepository.save(request);
        
        return mapToDTO(updatedRequest);
    }
    
    // Helper methods
    
    private void validateRequest(RequestDTO requestDTO) {
        // Check for required fields
        if (requestDTO.getRequestNumber() == null || requestDTO.getRequestNumber().trim().isEmpty()) {
            throw new BadRequestException("Request number is required");
        }
        
        if (requestDTO.getRequestCode() == null || requestDTO.getRequestCode().trim().isEmpty()) {
            throw new BadRequestException("Request code is required");
        }
        
        if (requestDTO.getType() == null || requestDTO.getType().trim().isEmpty()) {
            throw new BadRequestException("Request type is required");
        }
        
        if (requestDTO.getAmount() == null || requestDTO.getAmount().doubleValue() <= 0) {
            throw new BadRequestException("Request amount must be positive");
        }
        
        // Check for duplicate request number
        requestRepository.findByRequestNumber(requestDTO.getRequestNumber())
                .ifPresent(r -> {
                    if (!r.getId().equals(requestDTO.getId())) {
                        throw new BadRequestException("Request number already exists: " + requestDTO.getRequestNumber());
                    }
                });
    }
    
    private void validateStatus(String status) {
        List<String> validStatuses = List.of("PENDING", "ASSIGNED", "IN_PROGRESS", "CONFIRMED", "COMPLETED", "CANCELLED");
        if (!validStatuses.contains(status)) {
            throw new BadRequestException("Invalid status. Valid statuses are: " + String.join(", ", validStatuses));
        }
    }
    
    private void updateStatusBasedOnAssignment(Request request) {
        // If both driver and transporter are assigned, update status to ASSIGNED
        if (request.getDriver() != null && request.getTransporter() != null) {
            request.setStatus("ASSIGNED");
        }
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    private Request mapToEntity(RequestDTO requestDTO) {
        Request request = new Request();
        
        request.setRequestNumber(requestDTO.getRequestNumber());
        request.setRequestCode(requestDTO.getRequestCode());
        request.setRequestDate(requestDTO.getRequestDate());
        request.setCulture(requestDTO.getCulture());
        request.setType(requestDTO.getType());
        request.setAmount(requestDTO.getAmount());
        request.setStatus(requestDTO.getStatus());
        request.setNature(requestDTO.getNature());
        request.setConfirmed(requestDTO.getConfirmed());
        request.setComments(requestDTO.getComments());
        request.setConfirmationDate(requestDTO.getConfirmationDate());
        
        // Set driver if driverId is provided
        if (requestDTO.getDriverId() != null) {
            Driver driver = driverRepository.findById(requestDTO.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", requestDTO.getDriverId()));
            request.setDriver(driver);
        }
        
        // Set transporter if transporterId is provided
        if (requestDTO.getTransporterId() != null) {
            Transporter transporter = transporterRepository.findById(requestDTO.getTransporterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", requestDTO.getTransporterId()));
            request.setTransporter(transporter);
        }
        
        return request;
    }
    
    private RequestDTO mapToDTO(Request request) {
        RequestDTO requestDTO = new RequestDTO();
        
        requestDTO.setId(request.getId());
        requestDTO.setRequestNumber(request.getRequestNumber());
        requestDTO.setRequestCode(request.getRequestCode());
        requestDTO.setRequestDate(request.getRequestDate());
        requestDTO.setCulture(request.getCulture());
        requestDTO.setType(request.getType());
        requestDTO.setAmount(request.getAmount());
        requestDTO.setStatus(request.getStatus());
        requestDTO.setNature(request.getNature());
        requestDTO.setConfirmed(request.getConfirmed());
        requestDTO.setComments(request.getComments());
        requestDTO.setConfirmationDate(request.getConfirmationDate());
        
        // Set IDs for related entities
        if (request.getDriver() != null) {
            requestDTO.setDriverId(request.getDriver().getId());
        }
        
        if (request.getTransporter() != null) {
            requestDTO.setTransporterId(request.getTransporter().getId());
        }
        
        return requestDTO;
    }
} 