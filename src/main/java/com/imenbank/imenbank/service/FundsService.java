package com.imenbank.imenbank.service;

import com.imenbank.imenbank.dto.TransactionDTO;
import com.imenbank.imenbank.dto.TransactionReportDTO;

import java.time.LocalDate;
import java.util.List;

public interface FundsService {
    
    // Transaction management
    TransactionDTO createTransaction(TransactionDTO transactionDTO);
    
    TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO);
    
    void deleteTransaction(Long id);
    
    TransactionDTO getTransactionById(Long id);
    
    // Fund entry/exit confirmation
    TransactionDTO confirmTransaction(Long id, String confirmedBy);
    
    TransactionDTO rejectTransaction(Long id, String rejectedBy, String reason);
    
    // Fund entry/exit operations
    TransactionDTO registerFundEntry(TransactionDTO transactionDTO);
    
    TransactionDTO registerFundExit(TransactionDTO transactionDTO);
    
    // Transaction queries
    List<TransactionDTO> getTransactionsByRequestId(Long requestId);
    
    List<TransactionDTO> getTransactionsByRequestNumber(String requestNumber);
    
    List<TransactionDTO> getTransactionsByType(String type);
    
    List<TransactionDTO> getTransactionsByStatus(String status);
    
    List<TransactionDTO> getTransactionsByDateRange(LocalDate fromDate, LocalDate toDate);
    
    // Reporting
    TransactionReportDTO generateTransactionReport(LocalDate fromDate, LocalDate toDate);
    
    TransactionReportDTO generateRequestTransactionReport(Long requestId);
} 