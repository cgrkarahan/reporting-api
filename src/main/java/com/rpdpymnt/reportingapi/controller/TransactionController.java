package com.rpdpymnt.reportingapi.controller;

import com.rpdpymnt.reportingapi.dto.*;
import com.rpdpymnt.reportingapi.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("transactions")
    ResponseEntity<TransactionListResponse> getTransactions(@RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate fromDate,
                                                            @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate toDate,
                                                            @RequestParam(name = "merchantId", required = false) Long merchantId,
                                                            @RequestParam(name = "acquirerId", required = false) Long acquirerId,
                                                            @RequestParam(name = "errorCode", required = false) ErrorCode errorCode,
                                                            @RequestParam(name = "filterField", required = false) FilterField filter,
                                                            @RequestParam(name = "operation", required = false) Operation[] operation,
                                                            @RequestParam(name = "paymentMethod", required = false) PaymentMethod paymentMethod,
                                                            @RequestParam(name = "status", required = false) Status status,
                                                            @RequestParam(name = "filterField", required = false) FilterField filterField,
                                                            @RequestParam(name = "filterValue", required = false) String filterValue,
                                                            @RequestParam(name = "page", required = false) Integer page) {


        Optional<TransactionListResponse> transactions = transactionService.getTransactions(fromDate, toDate, status, operation, merchantId, acquirerId, paymentMethod, errorCode, filterField, filterValue, page);


        return transactions
                .filter(response -> !response.getData().isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable(name = "id") String id) {

        Optional<TransactionResponse> optionalTransactionResponse = transactionService.getTransaction(id);
        if (optionalTransactionResponse.isPresent()) {
            return ResponseEntity.ok(optionalTransactionResponse.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/transactions/report")
    public ResponseEntity<TransactionReportResponse> getTransactionReport(@RequestParam(name = "fromDate") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate fromDate,
                                                                          @RequestParam(name = "toDate") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate toDate,
                                                                          @RequestParam(name = "merchant", required = false) Long merchant,
                                                                          @RequestParam(name = "acquirer", required = false) Long acquirer) {

        return Optional.ofNullable(transactionService.getTransactionReport(fromDate, toDate, merchant, acquirer))
                .map(transActionReportResponse -> new ResponseEntity(transActionReportResponse, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.OK));
    }

}
