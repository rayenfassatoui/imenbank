package com.imenbank.imenbank.controller;

import com.imenbank.imenbank.dto.TransactionDTO;
import com.imenbank.imenbank.dto.TransactionReportDTO;
import com.imenbank.imenbank.service.FundsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
public class FundsController {

    private final FundsService fundsService;

    // Transaction management endpoints
    
    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(fundsService.getTransactionById(id));
    }
    
    @GetMapping("/transactions/request/{requestId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByRequestId(@PathVariable Long requestId) {
        return ResponseEntity.ok(fundsService.getTransactionsByRequestId(requestId));
    }
    
    @GetMapping("/transactions/request-number/{requestNumber}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByRequestNumber(@PathVariable String requestNumber) {
        return ResponseEntity.ok(fundsService.getTransactionsByRequestNumber(requestNumber));
    }
    
    @GetMapping("/transactions/type/{type}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(@PathVariable String type) {
        return ResponseEntity.ok(fundsService.getTransactionsByType(type));
    }
    
    @GetMapping("/transactions/status/{status}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(fundsService.getTransactionsByStatus(status));
    }
    
    @GetMapping("/transactions/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(fundsService.getTransactionsByDateRange(fromDate, toDate));
    }
    
    @PutMapping("/transactions/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(fundsService.updateTransaction(id, transactionDTO));
    }
    
    @DeleteMapping("/transactions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        fundsService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
    
    // Fund entry/exit endpoints
    
    @PostMapping("/entry")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> registerFundEntry(@Valid @RequestBody TransactionDTO transactionDTO) {
        return new ResponseEntity<>(fundsService.registerFundEntry(transactionDTO), HttpStatus.CREATED);
    }
    
    @PostMapping("/exit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> registerFundExit(@Valid @RequestBody TransactionDTO transactionDTO) {
        return new ResponseEntity<>(fundsService.registerFundExit(transactionDTO), HttpStatus.CREATED);
    }
    
    // Confirmation endpoints
    
    @PutMapping("/transactions/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> confirmTransaction(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(fundsService.confirmTransaction(id, authentication.getName()));
    }
    
    @PutMapping("/transactions/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> rejectTransaction(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        return ResponseEntity.ok(fundsService.rejectTransaction(id, authentication.getName(), reason));
    }
    
    // Reporting endpoints
    
    @GetMapping("/reports/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or hasRole('MANAGER')")
    public ResponseEntity<TransactionReportDTO> generateTransactionReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(fundsService.generateTransactionReport(fromDate, toDate));
    }
    
    @GetMapping("/reports/request/{requestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or hasRole('MANAGER')")
    public ResponseEntity<TransactionReportDTO> generateRequestTransactionReport(@PathVariable Long requestId) {
        return ResponseEntity.ok(fundsService.generateRequestTransactionReport(requestId));
    }
} 