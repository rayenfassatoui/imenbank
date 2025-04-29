package com.imenbank.imenbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Transaction report containing financial summary and transaction details")
public class TransactionReportDTO {
    
    @Schema(description = "Start date of the report period", example = "2023-01-01")
    private LocalDate fromDate;
    
    @Schema(description = "End date of the report period", example = "2023-01-31")
    private LocalDate toDate;
    
    @Schema(description = "Total amount of all confirmed fund entries", example = "25000.00")
    private BigDecimal totalEntryAmount;
    
    @Schema(description = "Total amount of all confirmed fund exits", example = "15000.00")
    private BigDecimal totalExitAmount;
    
    @Schema(description = "Balance (total entries minus total exits)", example = "10000.00")
    private BigDecimal balance;
    
    @Schema(description = "Total number of transactions in the report", example = "42")
    private int totalTransactions;
    
    @Schema(description = "Number of confirmed transactions", example = "35")
    private int confirmedTransactions;
    
    @Schema(description = "Number of pending transactions", example = "5")
    private int pendingTransactions;
    
    @Schema(description = "Number of rejected transactions", example = "2")
    private int rejectedTransactions;
    
    @Schema(description = "List of all transactions included in the report")
    private List<TransactionDTO> transactions;
} 