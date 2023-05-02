package com.rpdpymnt.reportingapi.service.impl;

import com.rpdpymnt.reportingapi.dto.*;
import com.rpdpymnt.reportingapi.exception.ServerResponseException;
import com.rpdpymnt.reportingapi.service.TokenService;
import com.rpdpymnt.reportingapi.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    private static String TRANSACTION_SEARCH_URL = "https://sandbox-reporting.rpdpymnt.com/api/v3/transaction/list";

    private static String TRANSACTION_GET_URL = "https://sandbox-reporting.rpdpymnt.com/api/v3/transaction";

    private static String TRANSACTION_REPORT_URL = "https://sandbox-reporting.rpdpymnt.com/api/v3/transactions/report";
    private static final String AUTHORIZATION= "Authorization";


    @Override
    public Optional<TransactionResponse> getTransaction(String transactionId) {

        log.info("getTransaction service triggered by {}", transactionId);

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .transactionId(transactionId)
                .build();

        String authorization = tokenService.generateToken();

        try {

            log.info("calling transaction API");

            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTHORIZATION, authorization);
            TransactionResponse transactionResponse = restTemplate
                    .postForEntity(TRANSACTION_GET_URL,
                    new HttpEntity<>(transactionRequest, headers), TransactionResponse.class)
                    .getBody();

            log.info("Transaction Response body: {}", transactionResponse);

            return Optional.ofNullable(transactionResponse);
        } catch (Exception e) {
            log.error("Error while getting transaction: {}", e.getMessage());
            throw new ServerResponseException("Failed to retrieve data from API. Please try again.");
        }

    }


    @Override
    public Optional<TransactionReportResponse> getTransactionReport(LocalDate fromDate, LocalDate toDate, Long merchantId, Long acquirerId) {


        log.info("getTransaction service report triggered");

        Map<String, Object> params = new HashMap<>();

        Optional.ofNullable(fromDate)
                .ifPresent(fromDateValue -> params.put("fromDate", fromDateValue.toString()));

        Optional.ofNullable(toDate)
                .ifPresent(toDateValue -> params.put("toDate", toDateValue.toString()));

        Optional
                .ofNullable(merchantId)
                .ifPresent(merchantIdValue -> params.put("merchant", merchantIdValue));

        Optional.ofNullable(acquirerId)
                .ifPresent(acquirerIdValue -> params.put("acquirer", acquirerIdValue));

        try {

            log.info("calling transaction report API");

            String authorization = tokenService.generateToken();
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTHORIZATION, authorization);
            TransactionReportResponse response = restTemplate
                    .postForEntity(TRANSACTION_REPORT_URL,
                            new HttpEntity<>(params, headers),
                            TransactionReportResponse.class)
                    .getBody();

            log.info("Response body: {}", response);

            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.error("Error while getting transaction report: {}", e.getMessage());

            //Note: BE returns exception so I just mock the response according to feedback
            List<TransactionReportData> responseList = new ArrayList<>();
            TransactionReportData response1 = TransactionReportData.builder().count(1).total(100).currency("USD").build();
            responseList.add(response1);
            TransactionReportData response2 = TransactionReportData.builder().count(2).total(200).currency("EUR").build();
            responseList.add(response2);

            TransactionReportResponse response = TransactionReportResponse.builder()
                    .response(responseList)
                    .status(Status.APPROVED).build();
            return Optional.ofNullable(response);

        //    throw new ServerResponseException("Failed to retrieve data from API. Please try again.");
        }

    }

    @Override
    public Optional<TransactionListResponse> getTransactions(LocalDate fromDate,
                                                             LocalDate toDate,
                                                             Status status,
                                                             Operation[] operation,
                                                             Long merchantId,
                                                             Long acquirerId,
                                                             PaymentMethod paymentMethod,
                                                             ErrorCode errorCode,
                                                             FilterField filterField,
                                                             String filterValue,
                                                             Integer page) {

        log.info("getTransaction service report triggered");

        String authorization = tokenService.generateToken();

        Map<String, Object> params = new HashMap<>();


        Optional.ofNullable(fromDate)
                .ifPresent(localDate -> params.put("fromDate", localDate.toString()));

        Optional.ofNullable(toDate)
                .ifPresent(localDate -> params.put("toDate", localDate.toString()));

        Optional
                .ofNullable(status)
                .ifPresent(statusValue -> params.put("status", statusValue.name()));

        Optional
                .ofNullable(operation)
                .ifPresent(operationValue -> {
                    String[] operationArray = Arrays.stream(operationValue)
                            .map(Operation::toString)
                            .toArray(String[]::new);
                    params.put("operation", operationArray);
                });

        Optional
                .ofNullable(merchantId)
                .ifPresent(merchantIdValue -> params.put("merchantId", merchantIdValue));

        Optional
                .ofNullable(acquirerId)
                .ifPresent(acquirerIdValue -> params.put("acquirerId", acquirerIdValue));

        Optional
                .ofNullable(paymentMethod)
                .ifPresent(paymentMethodValue -> params.put("paymentMethod", paymentMethodValue.name()));
        Optional
                .ofNullable(errorCode)
                .ifPresent(errorCodeValue -> params.put("errorCode", errorCodeValue.name()));

        Optional
                .ofNullable(filterField)
                .ifPresent(filterFieldValue -> params.put("filterField", filterFieldValue.name()));

        Optional
                .ofNullable(filterValue)
                .ifPresent(filterValue1 -> params.put("filterValue", filterValue1));

        Optional
                .ofNullable(page)
                .ifPresent(pageValue -> params.put("page", pageValue));

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTHORIZATION, authorization);
            TransactionListResponse response = restTemplate
                    .postForEntity(TRANSACTION_SEARCH_URL,
                            new HttpEntity<>(params, headers),
                            TransactionListResponse.class)
                    .getBody();

            log.info("Response body: {}", response);

            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.error("Error while getting transactions report: {}", e.getMessage());
            throw new ServerResponseException("Please try to call service again!");
        }

    }


}
