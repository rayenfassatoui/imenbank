package com.imenbank.imenbank.service;

import com.imenbank.imenbank.dto.RequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface RequestService {
    
    RequestDTO createRequest(RequestDTO requestDTO);
    
    RequestDTO updateRequest(Long id, RequestDTO requestDTO);
    
    void deleteRequest(Long id);
    
    RequestDTO getRequestById(Long id);
    
    RequestDTO getRequestByNumber(String requestNumber);
    
    List<RequestDTO> getAllRequests();
    
    List<RequestDTO> getRequestsByStatus(String status);
    
    List<RequestDTO> getRequestsByType(String type);
    
    List<RequestDTO> getRequestsByDate(LocalDate date);
    
    RequestDTO assignDriver(Long requestId, Long driverId);
    
    RequestDTO assignTransporter(Long requestId, Long transporterId);
    
    RequestDTO confirmRequest(Long requestId);
    
    RequestDTO updateRequestStatus(Long requestId, String status);
} 