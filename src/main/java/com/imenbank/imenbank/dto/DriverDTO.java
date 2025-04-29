package com.imenbank.imenbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {
    
    private Long id;
    
    @NotBlank(message = "Matricule is required")
    private String matricule;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "CIN is required")
    private String cin;
    
    private String licenseNumber;
    
    private boolean available = true;
    
    private List<Long> requestIds;
} 