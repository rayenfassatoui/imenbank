package com.imenbank.imenbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    
    private Long id;
    
    @NotNull(message = "Request ID is required")
    private Long requestId;
    
    private String requestNumber;
    
    @NotBlank(message = "Transaction type is required")
    private String type; // ENTRY, EXIT
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private LocalDateTime transactionDate;
    
    private String description;
    
    private String status; // PENDING, CONFIRMED, REJECTED
    
    private String confirmedBy;
    
    private LocalDateTime confirmationDate;
    
    private String referenceNumber;
} 