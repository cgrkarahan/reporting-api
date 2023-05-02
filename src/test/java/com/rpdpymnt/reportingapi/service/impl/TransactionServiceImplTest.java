package com.rpdpymnt.reportingapi.service.impl;

import com.rpdpymnt.reportingapi.dto.*;
import com.rpdpymnt.reportingapi.exception.ServerResponseException;
import com.rpdpymnt.reportingapi.service.TokenService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceImplTest {


    @Mock
    private TokenService tokenService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;
    private  FxMerchant fxMerchant;
    private Fx fx;
    private  CustomerInfo customerInfo;
    private Agent agent;
    private MerchantTransaction merchantTransaction;
    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);

        fxMerchant = FxMerchant.builder()
                .originalAmount(100)
                .originalCurrency("USD")
                .convertedAmount(85)
                .convertedCurrency("EUR")
                .build();


        fx = Fx.builder()
                .merchant(fxMerchant)
                .build();
        customerInfo = CustomerInfo.builder()
                .billingFirstName("John")
                .billingLastName("Doe")
                .issueNumber(null)
                .email("johndoe@example.com")
                .billingCompany("Acme Corp")
                .billingCity("New York")
                .updated_at("2022-04-30T23:59:59.999Z")
                .created_at("2022-04-01T00:00:00.000Z")
                .id(12345)
                .build();
        agent = Agent.builder()
                .id(1)
                .customerIp("192.168.0.1")
                .customerUserAgent("Mozilla/5.0 (Windows NT 10.0;")
                .merchantIp("192.168.0.2")
                .merchantUserAgent("Mozilla/5.0 (Windows NT 10.0;")
                .created_at("2022-04-01T00:00:00.000Z")
                .updated_at("2022-04-30T23:59:59.999Z")
                .deleted_at(null)
                .build();

         merchantTransaction = MerchantTransaction.builder()
                .referenceNo("REF123456")
                .merchantId(67890)
                .status("approved")
                .channel("web")
                .customData(null)
                .chainId("CHAIN123")
                .type("payment")
                .agentInfoId(1)
                .operation("sale")
                .updated_at("2022-04-30T23:59:59.999Z")
                .created_at("2022-04-01T00:00:00.000Z")
                .id(23456)
                .fxTransactionId(34567)
                .acquirerTransactionId(45678)
                .code("10000")
                .message("Success")
                .transactionId("TRANSACTION123")
                .agent(agent)
                .build();
    }



    @Test
    public void testGetTransactionReport()  {
        // Given
        LocalDate fromDate = LocalDate.of(2022, 1, 1);
        LocalDate toDate = LocalDate.of(2022, 2, 1);
        Long merchantId = 123L;
        Long acquirerId = 456L;

        Map<String, Object> params = new HashMap<>();
        params.put("fromDate", fromDate.toString());
        params.put("toDate", toDate.toString());
        params.put("merchant", merchantId);
        params.put("acquirer", acquirerId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "test-token");

        List<TransactionReportData> responseList = new ArrayList<>();
        TransactionReportData response1 = TransactionReportData.builder().count(1).total(100).currency("USD").build();
        responseList.add(response1);
        TransactionReportData response2 = TransactionReportData.builder().count(2).total(200).currency("EUR").build();
        responseList.add(response2);

        TransactionReportResponse expectedResponse = TransactionReportResponse.builder()
                .status(Status.APPROVED)
                .response(responseList)
                .build();

        ResponseEntity<TransactionReportResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(tokenService.generateToken()).thenReturn("test-token");

        ResponseEntity<TransactionReportResponse> data = ResponseEntity.ok().body(expectedResponse);

        when(restTemplate.postForEntity(anyString(), any(), eq(TransactionReportResponse.class)))
                .thenReturn(responseEntity);

        // When
        Optional<TransactionReportResponse> actualResponse = transactionServiceImpl.getTransactionReport(fromDate, toDate, merchantId, acquirerId);

        // Then
        assertEquals(expectedResponse, actualResponse.get());
    }

    @Test
    public void testGetTransaction() {
        // Arrange
        String transactionId = "123";
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .transactionId(transactionId)
                .build();
        String authorization = "token";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);


        TransactionResponse transactionResponse = TransactionResponse.builder()
                .fx(Fx.builder().build())
                .customerInfo(CustomerInfo.builder().build())
                .merchant(MerchantTransaction.builder().build())
                .transaction(Transaction.builder().merchant(MerchantTransaction.builder().build()).build())
                .build();

        transactionResponse.setCustomerInfo(  CustomerInfo.builder().billingFirstName("John")
                .billingLastName("Doe")
                .email("john@gmail.com")
                .build());
        when(tokenService.generateToken()).thenReturn(authorization);
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(
                new ResponseEntity<>(transactionResponse, HttpStatus.OK));

        // Act
        Optional<TransactionResponse> result = transactionServiceImpl.getTransaction(transactionId);

        // Assert
        assertTrue(result.isPresent());
        assertSame(transactionResponse, result.get());
        assertSame(transactionResponse.getCustomerInfo().getBillingFirstName(),result.get().getCustomerInfo().getBillingFirstName());

        verify(tokenService).generateToken();
     //   verify(restTemplate).postForEntity(eq(transactionGetURL), eq(asHttpEntityWithHeader(transactionRequest, headers)), eq(TransactionResponse.class));
    }

    @Test
    public void testGetTransactionServerResponseException() {
        // Arrange
        String transactionId = "123";
        String authorization = "token";
        when(tokenService.generateToken()).thenReturn(authorization);
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(new ServerResponseException("Bad Request"));

        // Act & Assert
        assertThrows(ServerResponseException.class, () -> {
            transactionServiceImpl.getTransaction(transactionId);
        });


    }

    @Test
    public void testGetTransactionsSuccess() {
        // given
        String authorization = "myAuthorizationToken";
        when(tokenService.generateToken()).thenReturn(authorization);

        TransactionListResponse expectedResponse = new TransactionListResponse();



        List<TransactionInfo> data = new ArrayList<>();

        data.add( TransactionInfo.builder().fx(fx).
                customerInfo(customerInfo).
                transaction(Transaction.builder().
                        merchant(merchantTransaction).build()).build());

        expectedResponse.builder().data(data).build();
        ResponseEntity<TransactionListResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(TransactionListResponse.class))).thenReturn(responseEntity);

        // when

        Operation[] operations = { Operation.DIRECT };

        Optional<TransactionListResponse> result = transactionServiceImpl.getTransactions(LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31), Status.APPROVED,  operations, 1L, 2L, PaymentMethod.CREDITCARD, ErrorCode.DO_NOT_HONOR,
                FilterField.REFERENCE_NO, "myReferenceNo", 0);

         //then
        verify(tokenService).generateToken();
        verify(restTemplate).postForEntity(anyString(), any(), eq(TransactionListResponse.class));

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals(expectedResponse, result.get())
        );
    }

    @Test
    public void testGetTransactionsServerResponseException() {
        // given
        String authorization = "myAuthorizationToken";
        when(tokenService.generateToken()).thenReturn(authorization);

        when(restTemplate.postForEntity(anyString(), any(), eq(TransactionListResponse.class))).thenThrow(new ServerResponseException("Unexpected Exception"));

        // when
        Exception exception = assertThrows(ServerResponseException.class, () -> {
            transactionServiceImpl.getTransactions(LocalDate.of(2023, 1, 1), null, null, null, null, null, null,
                    null, null, null, null);
        });

        // then
        verify(tokenService).generateToken();
        verify(restTemplate).postForEntity(anyString(), any(), eq(TransactionListResponse.class));

        assertEquals("Please try to call service again!", exception.getMessage());
    }

    @Test
    public void testGetTransactionsNullResponse() {
        // given
        String authorization = "myAuthorizationToken";
        when(tokenService.generateToken()).thenReturn(authorization);

        ResponseEntity<TransactionListResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(TransactionListResponse.class))).thenReturn(responseEntity);

        // when
        Optional<TransactionListResponse> result = transactionServiceImpl.getTransactions(null, null, null, null, null, null,
                null, null, null, null, null);

        // then
        verify(tokenService).generateToken();
        verify(restTemplate).postForEntity(anyString(), any(), eq(TransactionListResponse.class));

        assertFalse(result.isPresent());
    }



}
