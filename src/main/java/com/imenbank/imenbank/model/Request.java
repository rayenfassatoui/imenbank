package com.imenbank.imenbank.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "requests")
@Data
public class Request {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String requestNumber;
    
    @Column(nullable = false)
    private String requestCode;
    
    @Column(nullable = false)
    private LocalDate requestDate;
    
    private String culture;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    private String status;
    
    private String nature;
    
    private Boolean confirmed;
    
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
    
    @ManyToOne
    @JoinColumn(name = "transporter_id")
    private Transporter transporter;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;
    
    private LocalDate confirmationDate;
    
    private String comments;
} 