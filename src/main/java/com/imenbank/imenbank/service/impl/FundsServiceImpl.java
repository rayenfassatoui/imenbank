package com.imenbank.imenbank.service.impl;

import com.imenbank.imenbank.dto.TransactionDTO;
import com.imenbank.imenbank.dto.TransactionReportDTO;
import com.imenbank.imenbank.exception.BadRequestException;
import com.imenbank.imenbank.exception.ResourceNotFoundException;
import com.imenbank.imenbank.model.Request;
import com.imenbank.imenbank.model.Transaction;
import com.imenbank.imenbank.repository.RequestRepository;
import com.imenbank.imenbank.repository.TransactionRepository;
import com.imenbank.imenbank.service.FundsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundsServiceImpl implements FundsService {

    private final TransactionRepository transactionRepository;
    private final RequestRepository requestRepository;

    // Transaction management

    @Override
    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        validateTransaction(transactionDTO);
        
        Request request = getRequestById(transactionDTO.getRequestId());
        
        Transaction transaction = Transaction.builder()
                .request(request)
                .type(transactionDTO.getType())
                .amount(transactionDTO.getAmount())
                .transactionDate(LocalDateTime.now())
                .description(transactionDTO.getDescription())
                .status("PENDING")
                .referenceNumber(generateReferenceNumber())
                .build();
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return mapToDTO(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = getTransactionEntityById(id);
        
        // Don't allow updates to confirmed transactions
        if ("CONFIRMED".equals(transaction.getStatus())) {
            throw new BadRequestException("Cannot update a confirmed transaction");
        }
        
        // Update allowed fields
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setAmount(transactionDTO.getAmount());
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        
        return mapToDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionEntityById(id);
        
        // Don't allow deletion of confirmed transactions
        if ("CONFIRMED".equals(transaction.getStatus())) {
            throw new BadRequestException("Cannot delete a confirmed transaction");
        }
        
        transactionRepository.delete(transaction);
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        return mapToDTO(getTransactionEntityById(id));
    }

    // Fund entry/exit confirmation

    @Override
    @Transactional
    public TransactionDTO confirmTransaction(Long id, String confirmedBy) {
        Transaction transaction = getTransactionEntityById(id);
        
        // Don't allow confirmation of already confirmed or rejected transactions
        if ("CONFIRMED".equals(transaction.getStatus())) {
            throw new BadRequestException("Transaction is already confirmed");
        }
        
        if ("REJECTED".equals(transaction.getStatus())) {
            throw new BadRequestException("Cannot confirm a rejected transaction");
        }
        
        // Confirm transaction
        transaction.setStatus("CONFIRMED");
        transaction.setConfirmedBy(confirmedBy);
        transaction.setConfirmationDate(LocalDateTime.now());
        
        // Update request status if needed
        Request request = transaction.getRequest();
        if (!Boolean.TRUE.equals(request.getConfirmed())) {
            request.setConfirmed(true);
            request.setConfirmationDate(LocalDate.now());
            requestRepository.save(request);
        }
        
        Transaction confirmedTransaction = transactionRepository.save(transaction);
        
        return mapToDTO(confirmedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO rejectTransaction(Long id, String rejectedBy, String reason) {
        Transaction transaction = getTransactionEntityById(id);
        
        // Don't allow rejection of already confirmed or rejected transactions
        if ("CONFIRMED".equals(transaction.getStatus())) {
            throw new BadRequestException("Cannot reject a confirmed transaction");
        }
        
        if ("REJECTED".equals(transaction.getStatus())) {
            throw new BadRequestException("Transaction is already rejected");
        }
        
        // Reject transaction
        transaction.setStatus("REJECTED");
        transaction.setConfirmedBy(rejectedBy);
        transaction.setConfirmationDate(LocalDateTime.now());
        transaction.setDescription(reason != null ? reason : transaction.getDescription());
        
        Transaction rejectedTransaction = transactionRepository.save(transaction);
        
        return mapToDTO(rejectedTransaction);
    }

    // Fund entry/exit operations

    @Override
    @Transactional
    public TransactionDTO registerFundEntry(TransactionDTO transactionDTO) {
        // Validate request
        Request request = getRequestById(transactionDTO.getRequestId());
        
        // Validate that request is confirmed
        if (Boolean.FALSE.equals(request.getConfirmed())) {
            throw new BadRequestException("Cannot register fund entry for unconfirmed request");
        }
        
        // Set type to ENTRY
        transactionDTO.setType("ENTRY");
        
        // Create transaction
        return createTransaction(transactionDTO);
    }

    @Override
    @Transactional
    public TransactionDTO registerFundExit(TransactionDTO transactionDTO) {
        // Validate request
        Request request = getRequestById(transactionDTO.getRequestId());
        
        // Validate that request is confirmed
        if (Boolean.FALSE.equals(request.getConfirmed())) {
            throw new BadRequestException("Cannot register fund exit for unconfirmed request");
        }
        
        // Set type to EXIT
        transactionDTO.setType("EXIT");
        
        // Create transaction
        return createTransaction(transactionDTO);
    }

    // Transaction queries

    @Override
    public List<TransactionDTO> getTransactionsByRequestId(Long requestId) {
        // Validate request exists
        getRequestById(requestId);
        
        return transactionRepository.findByRequestId(requestId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByRequestNumber(String requestNumber) {
        return transactionRepository.findByRequestNumber(requestNumber).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByType(String type) {
        validateTransactionType(type);
        
        return transactionRepository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByStatus(String status) {
        validateTransactionStatus(status);
        
        return transactionRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new BadRequestException("From date and to date must be provided");
        }
        
        if (fromDate.isAfter(toDate)) {
            throw new BadRequestException("From date cannot be after to date");
        }
        
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.atTime(LocalTime.MAX);
        
        return transactionRepository.findByTransactionDateBetween(startDateTime, endDateTime).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Reporting

    @Override
    public TransactionReportDTO generateTransactionReport(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new BadRequestException("From date and to date must be provided");
        }
        
        if (fromDate.isAfter(toDate)) {
            throw new BadRequestException("From date cannot be after to date");
        }
        
        List<TransactionDTO> transactions = getTransactionsByDateRange(fromDate, toDate);
        
        return generateReport(transactions, fromDate, toDate);
    }

    @Override
    public TransactionReportDTO generateRequestTransactionReport(Long requestId) {
        // Validate request exists
        Request request = getRequestById(requestId);
        
        List<TransactionDTO> transactions = getTransactionsByRequestId(requestId);
        
        return generateReport(transactions, null, null);
    }

    // Helper methods

    private Transaction getTransactionEntityById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
    }

    private Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
    }

    private void validateTransaction(TransactionDTO transactionDTO) {
        // Check request ID
        if (transactionDTO.getRequestId() == null) {
            throw new BadRequestException("Request ID is required");
        }
        
        // Check transaction type
        if (transactionDTO.getType() == null) {
            throw new BadRequestException("Transaction type is required");
        }
        
        validateTransactionType(transactionDTO.getType());
        
        // Check amount
        if (transactionDTO.getAmount() == null || transactionDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transaction amount must be positive");
        }
    }

    private void validateTransactionType(String type) {
        if (!List.of("ENTRY", "EXIT").contains(type)) {
            throw new BadRequestException("Transaction type must be either ENTRY or EXIT");
        }
    }

    private void validateTransactionStatus(String status) {
        if (!List.of("PENDING", "CONFIRMED", "REJECTED").contains(status)) {
            throw new BadRequestException("Transaction status must be one of: PENDING, CONFIRMED, REJECTED");
        }
    }

    private String generateReferenceNumber() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private TransactionReportDTO generateReport(List<TransactionDTO> transactions, LocalDate fromDate, LocalDate toDate) {
        BigDecimal totalEntryAmount = BigDecimal.ZERO;
        BigDecimal totalExitAmount = BigDecimal.ZERO;
        int confirmedCount = 0;
        int pendingCount = 0;
        int rejectedCount = 0;
        
        for (TransactionDTO transaction : transactions) {
            if ("ENTRY".equals(transaction.getType()) && "CONFIRMED".equals(transaction.getStatus())) {
                totalEntryAmount = totalEntryAmount.add(transaction.getAmount());
            } else if ("EXIT".equals(transaction.getType()) && "CONFIRMED".equals(transaction.getStatus())) {
                totalExitAmount = totalExitAmount.add(transaction.getAmount());
            }
            
            if ("CONFIRMED".equals(transaction.getStatus())) {
                confirmedCount++;
            } else if ("PENDING".equals(transaction.getStatus())) {
                pendingCount++;
            } else if ("REJECTED".equals(transaction.getStatus())) {
                rejectedCount++;
            }
        }
        
        BigDecimal balance = totalEntryAmount.subtract(totalExitAmount);
        
        return TransactionReportDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalEntryAmount(totalEntryAmount)
                .totalExitAmount(totalExitAmount)
                .balance(balance)
                .totalTransactions(transactions.size())
                .confirmedTransactions(confirmedCount)
                .pendingTransactions(pendingCount)
                .rejectedTransactions(rejectedCount)
                .transactions(transactions)
                .build();
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .requestId(transaction.getRequest().getId())
                .requestNumber(transaction.getRequest().getRequestNumber())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .description(transaction.getDescription())
                .status(transaction.getStatus())
                .confirmedBy(transaction.getConfirmedBy())
                .confirmationDate(transaction.getConfirmationDate())
                .referenceNumber(transaction.getReferenceNumber())
                .build();
    }
} 