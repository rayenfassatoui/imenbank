package com.imenbank.imenbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO {
    
    private Long id;
    
    @NotBlank(message = "Request number is required")
    private String requestNumber;
    
    @NotBlank(message = "Request code is required")
    private String requestCode;
    
    @NotNull(message = "Request date is required")
    private LocalDate requestDate;
    
    private String culture;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private String status;
    
    private String nature;
    
    private Boolean confirmed;
    
    private Long driverId;
    
    private Long transporterId;
    
    private LocalDate confirmationDate;
    
    private String comments;
} 