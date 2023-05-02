package com.rpdpymnt.reportingapi.service.impl;


import com.rpdpymnt.reportingapi.dto.ClientRequest;
import com.rpdpymnt.reportingapi.dto.ClientResponse;
import com.rpdpymnt.reportingapi.dto.CustomerInfo;
import com.rpdpymnt.reportingapi.exception.ServerResponseException;
import com.rpdpymnt.reportingapi.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.util.Optional;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClientServiceImpl clientService;

    private  ClientResponse clientResponse;
    @BeforeEach
    public void setup() {
        clientResponse = ClientResponse.builder()
                .customerInfo(CustomerInfo.builder()
                        .billingFirstName("John")
                        .billingLastName("Doe")
                        .issueNumber(null)
                        .email("john@gmail.com")
                        .billingCompany("Sky")
                        .billingCity("London")
                        .updated_at("2018-10-12 15:12:24")
                        .created_at("2018-10-12 15:12:24")
                        .id(706784)
                        .build())
                .build();

    }

    @Test
    public void testGetClientInfo_Success() {


        when(tokenService.generateToken()).thenReturn("mockToken");

        // Mock the response from restTemplate
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "mockToken");
        ClientResponse mockResponse = new ClientResponse();
        mockResponse.setCustomerInfo(clientResponse.getCustomerInfo());
        mockResponse.setCustomerInfo(new CustomerInfo());


        when(restTemplate.postForEntity(anyString(), any(),
                eq(ClientResponse.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // Call the method being tested

        Optional<ClientResponse> result = clientService.getClientInfo("mockTransactionId");


        assertTrue(result.isPresent());
        assertEquals(mockResponse, result.get());

        // Verify that tokenService was called once with no arguments
        verify(tokenService, times(1)).generateToken();

        // Verify that restTemplate was called once with the expected arguments
        verify(restTemplate, times(1)).postForEntity(
                eq("https://sandbox-reporting.rpdpymnt.com/api/v3/client"),
                any(),
                eq(ClientResponse.class)
        );


    }

    @Test
    void testGetClientInfoWithInvalidTransactionId() {
        // Arrange
        String invalidTransactionId = "invalid-transaction-id";
        when(tokenService.generateToken()).thenReturn("valid-token");

        ClientRequest expectedRequest = ClientRequest.builder()
                .transactionId(invalidTransactionId).build();

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("Authorization", "valid-token");

        when(restTemplate.postForEntity(anyString(),any(HttpEntity.class), eq(ClientResponse.class)))
                .thenThrow(new RuntimeException("Invalid transaction ID"));

        // Act and Assert
        assertThrows(ServerResponseException.class, () -> {
            clientService.getClientInfo(invalidTransactionId);
        });

        verify(restTemplate).postForEntity(eq("https://sandbox-reporting.rpdpymnt.com/api/v3/client"),
                eq(new HttpEntity<>(expectedRequest, expectedHeaders)), eq(ClientResponse.class));
    }
}