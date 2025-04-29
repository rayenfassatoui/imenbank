package com.imenbank.imenbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportDTO {
    
    private LocalDate fromDate;
    private LocalDate toDate;
    
    private BigDecimal totalEntryAmount;
    private BigDecimal totalExitAmount;
    private BigDecimal balance;
    
    private int totalTransactions;
    private int confirmedTransactions;
    private int pendingTransactions;
    private int rejectedTransactions;
    
    private List<TransactionDTO> transactions;
} 