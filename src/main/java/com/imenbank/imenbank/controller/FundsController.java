package com.imenbank.imenbank.controller;

import com.imenbank.imenbank.dto.TransactionDTO;
import com.imenbank.imenbank.dto.TransactionReportDTO;
import com.imenbank.imenbank.service.FundsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Funds Confirmation", description = "API endpoints for managing financial transactions")
public class FundsController {

    private final FundsService fundsService;

    // Transaction management endpoints
    
    @Operation(summary = "Get transaction by ID", description = "Retrieves a transaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction found", 
                content = @Content(schema = @Schema(implementation = TransactionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(
            @Parameter(description = "Transaction ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(fundsService.getTransactionById(id));
    }
    
    @Operation(summary = "Get transactions by request ID", description = "Retrieves all transactions for a specific request")
    @GetMapping("/transactions/request/{requestId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByRequestId(
            @Parameter(description = "Request ID", required = true) @PathVariable Long requestId) {
        return ResponseEntity.ok(fundsService.getTransactionsByRequestId(requestId));
    }
    
    @Operation(summary = "Get transactions by request number", description = "Retrieves all transactions by request number")
    @GetMapping("/transactions/request-number/{requestNumber}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByRequestNumber(
            @Parameter(description = "Request number", required = true) @PathVariable String requestNumber) {
        return ResponseEntity.ok(fundsService.getTransactionsByRequestNumber(requestNumber));
    }
    
    @Operation(summary = "Get transactions by type", description = "Retrieves all transactions of a specific type (ENTRY or EXIT)")
    @GetMapping("/transactions/type/{type}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(
            @Parameter(description = "Transaction type (ENTRY or EXIT)", required = true) @PathVariable String type) {
        return ResponseEntity.ok(fundsService.getTransactionsByType(type));
    }
    
    @Operation(summary = "Get transactions by status", description = "Retrieves all transactions with a specific status")
    @GetMapping("/transactions/status/{status}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByStatus(
            @Parameter(description = "Transaction status (PENDING, CONFIRMED, or REJECTED)", required = true) @PathVariable String status) {
        return ResponseEntity.ok(fundsService.getTransactionsByStatus(status));
    }
    
    @Operation(summary = "Get transactions by date range", description = "Retrieves all transactions within a specified date range")
    @GetMapping("/transactions/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @Parameter(description = "Start date (format: yyyy-MM-dd)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "End date (format: yyyy-MM-dd)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(fundsService.getTransactionsByDateRange(fromDate, toDate));
    }
    
    @Operation(summary = "Update transaction", description = "Updates an existing transaction by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PutMapping("/transactions/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @Parameter(description = "Transaction ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated transaction details", required = true) @Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(fundsService.updateTransaction(id, transactionDTO));
    }
    
    @Operation(summary = "Delete transaction", description = "Deletes a transaction by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transaction deleted"),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @DeleteMapping("/transactions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "Transaction ID", required = true) @PathVariable Long id) {
        fundsService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
    
    // Fund entry/exit endpoints
    
    @Operation(summary = "Register fund entry", description = "Creates a new fund entry transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Fund entry created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @PostMapping("/entry")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> registerFundEntry(
            @Parameter(description = "Fund entry details", required = true) @Valid @RequestBody TransactionDTO transactionDTO) {
        return new ResponseEntity<>(fundsService.registerFundEntry(transactionDTO), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Register fund exit", description = "Creates a new fund exit transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Fund exit created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @PostMapping("/exit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> registerFundExit(
            @Parameter(description = "Fund exit details", required = true) @Valid @RequestBody TransactionDTO transactionDTO) {
        return new ResponseEntity<>(fundsService.registerFundExit(transactionDTO), HttpStatus.CREATED);
    }
    
    // Confirmation endpoints
    
    @Operation(summary = "Confirm transaction", description = "Confirms a pending transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction confirmed"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction state"),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PutMapping("/transactions/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> confirmTransaction(
            @Parameter(description = "Transaction ID", required = true) @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(fundsService.confirmTransaction(id, authentication.getName()));
    }
    
    @Operation(summary = "Reject transaction", description = "Rejects a pending transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction rejected"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction state"),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PutMapping("/transactions/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public ResponseEntity<TransactionDTO> rejectTransaction(
            @Parameter(description = "Transaction ID", required = true) @PathVariable Long id,
            @Parameter(description = "Reason for rejection", required = true) @RequestParam String reason,
            Authentication authentication) {
        return ResponseEntity.ok(fundsService.rejectTransaction(id, authentication.getName(), reason));
    }
    
    // Reporting endpoints
    
    @Operation(summary = "Generate transaction report by date range", description = "Creates a report of transactions within a specified date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report generated successfully",
                content = @Content(schema = @Schema(implementation = TransactionReportDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid date range"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @GetMapping("/reports/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or hasRole('MANAGER')")
    public ResponseEntity<TransactionReportDTO> generateTransactionReport(
            @Parameter(description = "Start date (format: yyyy-MM-dd)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "End date (format: yyyy-MM-dd)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(fundsService.generateTransactionReport(fromDate, toDate));
    }
    
    @Operation(summary = "Generate transaction report by request", description = "Creates a report of all transactions for a specific request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report generated successfully",
                content = @Content(schema = @Schema(implementation = TransactionReportDTO.class))),
        @ApiResponse(responseCode = "404", description = "Request not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @GetMapping("/reports/request/{requestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE') or hasRole('MANAGER')")
    public ResponseEntity<TransactionReportDTO> generateRequestTransactionReport(
            @Parameter(description = "Request ID", required = true) @PathVariable Long requestId) {
        return ResponseEntity.ok(fundsService.generateRequestTransactionReport(requestId));
    }
} 