package com.imenbank.imenbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Transaction data transfer object for financial operations")
public class TransactionDTO {
    
    @Schema(description = "Unique identifier of the transaction", example = "1")
    private Long id;
    
    @Schema(description = "ID of the request associated with this transaction", example = "101", required = true)
    @NotNull(message = "Request ID is required")
    private Long requestId;
    
    @Schema(description = "Request number for reference", example = "REQ-2023-001")
    private String requestNumber;
    
    @Schema(description = "Type of transaction (ENTRY or EXIT)", example = "ENTRY", required = true)
    @NotBlank(message = "Transaction type is required")
    private String type; // ENTRY, EXIT
    
    @Schema(description = "Amount of the transaction", example = "1500.00", required = true)
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @Schema(description = "Date and time when the transaction was created", example = "2023-06-15T14:30:00")
    private LocalDateTime transactionDate;
    
    @Schema(description = "Additional details about the transaction", example = "Payment for services rendered")
    private String description;
    
    @Schema(description = "Current status of the transaction (PENDING, CONFIRMED, REJECTED)", example = "PENDING")
    private String status; // PENDING, CONFIRMED, REJECTED
    
    @Schema(description = "Username of the person who confirmed or rejected the transaction", example = "john.doe")
    private String confirmedBy;
    
    @Schema(description = "Date and time when the transaction was confirmed or rejected", example = "2023-06-16T10:15:00")
    private LocalDateTime confirmationDate;
    
    @Schema(description = "Unique reference number for the transaction", example = "TXN-AB12CD34")
    private String referenceNumber;
} 