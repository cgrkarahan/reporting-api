package com.rpdpymnt.reportingapi.controller;

import com.rpdpymnt.reportingapi.config.JwtService;
import com.rpdpymnt.reportingapi.dto.*;
import com.rpdpymnt.reportingapi.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TransactionService transactionService;

    private TransactionResponse response;
    private TransactionReportResponse transactionReportResponse;

    @BeforeEach
    public void setUp() {

        transactionReportResponse = TransactionReportResponse.builder()
                .status(Status.APPROVED)
                .response(Arrays.asList(
                        TransactionReportData.builder()
                                .count(20)
                                .total(50000)
                                .currency("USD")
                                .build(),
                        TransactionReportData.builder()
                                .count(10)
                                .total(500)
                                .currency("EUR")
                                .build()
                ))
                .build();

        response = TransactionResponse.builder()
                .fx(Fx.builder()
                        .merchant(FxMerchant.builder()
                                .originalAmount(100)
                                .originalCurrency("USD")
                                .convertedAmount(80)
                                .convertedCurrency("EUR")
                                .build())
                        .build())
                .customerInfo(CustomerInfo.builder()
                        .billingFirstName("John")
                        .billingLastName("Doe")
                        .email("john.doe@example.com")
                        .build())
                .merchant(MerchantTransaction.builder()
                        .referenceNo("1234567890")
                        .status("Success")
                        .agent(Agent.builder()
                                .customerIp("127.0.0.1")
                                .merchantIp("192.168.1.1")
                                .build())
                        .build())
                .transaction(Transaction.builder()
                        .merchant(MerchantTransaction.builder()
                                .referenceNo("1234567890")
                                .status("Success")
                                .agent(Agent.builder()
                                        .customerIp("127.0.0.1")
                                        .merchantIp("192.168.1.1")
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Test
    public void testGetTransaction() throws Exception {
        String id = "123";
        TransactionResponse transactionResponse = TransactionResponse.builder().build();


        when(transactionService.getTransaction(id)).thenReturn(Optional.of(response));


        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/transactions/{id}", id)
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fx.merchant.originalAmount").exists())
                .andExpect(jsonPath("$.customerInfo.billingFirstName").exists())
                .andExpect(jsonPath("$.merchant.referenceNo").exists())
                .andExpect(jsonPath("$.transaction.merchant.merchantId").exists());
    }

    @Test
    public void testGetTransactionNotFound() throws Exception {
        String id = "123";

        given(transactionService.getTransaction(id)).willReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/transactions/{id}", id)
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isNotFound());
    }




    @Test
    public void testGetTransactionReportWithFromDateAndToDate() throws Exception {
        LocalDate fromDate = LocalDate.now().minusDays(5);
        LocalDate toDate = LocalDate.now();
       // when(transactionService.getTransaction(id)).thenReturn(Optional.of(response));

        when(transactionService.getTransactionReport(fromDate,toDate,null,null)).
                thenReturn(Optional.of(transactionReportResponse));

        //TransactionReportResponse transactionReportResponse4 =  transactionService.getTransactionReport(fromDate,toDate,null,null).get();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/report")
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                )
                .param("fromDate", fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .param("toDate", toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].count").value(20))
                .andExpect(jsonPath("$.response[0].total").exists());
        verify(transactionService).getTransactionReport(eq(fromDate), eq(toDate), isNull(), isNull());
    }

    @Test
    public void testGetTransactionReportWithFromDateAndMerchant() throws Exception {
        LocalDate fromDate = LocalDate.now().minusDays(5);
        LocalDate toDate = LocalDate.now();
        Long merchant = 123L;

        when(transactionService.getTransactionReport(fromDate,toDate,merchant,null)).
                thenReturn(Optional.of(transactionReportResponse));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/report")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .param("fromDate", fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .param("toDate", toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .param("merchant", String.valueOf(merchant)))
                .andExpect(jsonPath("$.response[0].total").exists())
                .andExpect(status().isOk());

        verify(transactionService).getTransactionReport(eq(fromDate),  eq(toDate), eq(merchant), isNull());
    }

    @Test
    public void testGetTransactionReportWithFromDateAndAcquirer() throws Exception {
        LocalDate fromDate = LocalDate.now().minusDays(5);
        LocalDate toDate = LocalDate.now();
        Long acquirer = 456L;

        when(transactionService.getTransactionReport(fromDate,toDate,null,acquirer)).
                thenReturn(Optional.of(transactionReportResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/report")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .param("fromDate", fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .param("toDate", toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .param("acquirer", String.valueOf(acquirer)))
                .andExpect(status().isOk());

        verify(transactionService).getTransactionReport(eq(fromDate), eq(toDate), isNull(), eq(acquirer));
    }

    @Test
    public void testGetTransactionsWithAllParameters() throws Exception {
        // Arrange
        LocalDate fromDate = LocalDate.of(2022, 1, 1);
        LocalDate toDate = LocalDate.of(2022, 2, 1);
        Long merchantId = 1L;
        Long acquirerId = 2L;
        ErrorCode errorCode = ErrorCode.INVALID_CARD;
        FilterField filterField = FilterField.REFERENCE_NO;
        Operation operation = Operation.DIRECT;
        PaymentMethod paymentMethod = PaymentMethod.CREDITCARD;
        Status status = Status.APPROVED;
        String filterValue = "123";
        Integer page = 1;

        Operation[] operations = { Operation.DIRECT };
        TransactionListResponse transactionListResponse = createTransactionListResponse();
        when(transactionService.getTransactions(fromDate, toDate, status, operations, merchantId, acquirerId, paymentMethod, errorCode, filterField, filterValue, page))
                .thenReturn(Optional.of(transactionListResponse));

        // Act
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions")
                .param("fromDate", fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .param("toDate", toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .param("merchantId", merchantId.toString())
                .param("acquirerId", acquirerId.toString())
                .param("errorCode", errorCode.toString())
                .param("filterField", filterField.toString())
                .param("filterValue", filterValue)
                .param("operation", operation.toString())
                .param("paymentMethod", paymentMethod.toString())
                .param("status", status.toString())
                .param("page", page.toString())
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].merchant.referenceNo").value(transactionListResponse.getData().get(0).getMerchant().getReferenceNo()));

        // Assert
        verify(transactionService, times(1))
                .getTransactions(fromDate, toDate, status, operations, merchantId, acquirerId, paymentMethod, errorCode, filterField, filterValue, page);
    }

    // add more test methods for different parameter combinations

    private TransactionListResponse createTransactionListResponse() {
        List<TransactionInfo> data = new ArrayList<>();
        TransactionInfo transactionInfo = TransactionInfo.builder()
                .merchant(MerchantTransaction.builder().referenceNo("123").build())
                .build();
        data.add(transactionInfo);

        return TransactionListResponse.builder()
                .data(data)
                .build();
    }
}


