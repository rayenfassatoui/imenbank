package com.imenbank.imenbank.controller;

import com.imenbank.imenbank.dto.RequestDTO;
import com.imenbank.imenbank.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<RequestDTO> createRequest(@Valid @RequestBody RequestDTO requestDTO) {
        return new ResponseEntity<>(requestService.createRequest(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<RequestDTO> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok(requestService.updateRequest(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDTO> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @GetMapping("/number/{requestNumber}")
    public ResponseEntity<RequestDTO> getRequestByNumber(@PathVariable String requestNumber) {
        return ResponseEntity.ok(requestService.getRequestByNumber(requestNumber));
    }

    @GetMapping
    public ResponseEntity<List<RequestDTO>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequestDTO>> getRequestsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(requestService.getRequestsByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<RequestDTO>> getRequestsByType(@PathVariable String type) {
        return ResponseEntity.ok(requestService.getRequestsByType(type));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<RequestDTO>> getRequestsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(requestService.getRequestsByDate(date));
    }

    @PutMapping("/{requestId}/assign-driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<RequestDTO> assignDriver(
            @PathVariable Long requestId,
            @PathVariable Long driverId) {
        return ResponseEntity.ok(requestService.assignDriver(requestId, driverId));
    }

    @PutMapping("/{requestId}/assign-transporter/{transporterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<RequestDTO> assignTransporter(
            @PathVariable Long requestId,
            @PathVariable Long transporterId) {
        return ResponseEntity.ok(requestService.assignTransporter(requestId, transporterId));
    }

    @PutMapping("/{requestId}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<RequestDTO> confirmRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.confirmRequest(requestId));
    }

    @PutMapping("/{requestId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<RequestDTO> updateRequestStatus(
            @PathVariable Long requestId,
            @PathVariable String status) {
        return ResponseEntity.ok(requestService.updateRequestStatus(requestId, status));
    }
} 