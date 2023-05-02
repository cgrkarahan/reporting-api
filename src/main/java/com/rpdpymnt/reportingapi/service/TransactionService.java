package com.rpdpymnt.reportingapi.service;

import com.rpdpymnt.reportingapi.dto.*;

import java.time.LocalDate;
import java.util.Optional;

public interface TransactionService {


    Optional<TransactionResponse> getTransaction(String transactionId);

    Optional<TransactionReportResponse> getTransactionReport(LocalDate fromDate,
                                                   LocalDate toDate,
                                                   Long merchantId,
                                                   Long acquirerId);

    Optional<TransactionListResponse> getTransactions (LocalDate fromDate,
                                             LocalDate toDate,
                                             Status status,
                                             Operation [] operation,
                                             Long merchantId,
                                             Long acquirerId,
                                             PaymentMethod paymentMethod,
                                             ErrorCode errorCode,
                                             FilterField filterField,
                                             String filterValue,
                                             Integer page);





}
