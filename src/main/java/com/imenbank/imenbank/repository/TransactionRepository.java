package com.imenbank.imenbank.repository;

import com.imenbank.imenbank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByRequestId(Long requestId);
    
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    
    List<Transaction> findByType(String type);
    
    List<Transaction> findByStatus(String status);
    
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.request.id = :requestId AND t.type = :type")
    List<Transaction> findByRequestIdAndType(Long requestId, String type);
    
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate AND t.status = :status")
    List<Transaction> findByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status);
    
    @Query("SELECT t FROM Transaction t WHERE t.request.requestNumber = :requestNumber")
    List<Transaction> findByRequestNumber(String requestNumber);
} 