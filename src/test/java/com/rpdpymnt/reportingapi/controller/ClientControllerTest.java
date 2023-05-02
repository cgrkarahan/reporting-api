package com.rpdpymnt.reportingapi.controller;

import com.rpdpymnt.reportingapi.dto.ClientResponse;
import com.rpdpymnt.reportingapi.dto.CustomerInfo;
import com.rpdpymnt.reportingapi.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {
    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private String transactionId;
    private ClientResponse clientResponse;

    @BeforeEach
    public void setUp() {

        transactionId = "123";
        CustomerInfo customerInfo = CustomerInfo.builder()
                .billingFirstName("John")
                .billingLastName("Doe")
                .issueNumber(null)
                .email("john.doe@example.com")
                .billingCompany("ABC Corp")
                .billingCity("New York")
                .updated_at("2023-04-30T10:00:00Z")
                .created_at("2023-04-30T09:00:00Z")
                .id(1232131)
                .build();

         clientResponse = ClientResponse.builder()
                .customerInfo(customerInfo)
                .build();
    }

    @Test
    void testGetClientWhenClientExists() {
        when(clientService.getClientInfo(anyString())).thenReturn(Optional.of(clientResponse));

        ResponseEntity<ClientResponse> response = clientController.getClient(transactionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clientResponse, response.getBody());
    }

    @Test
    void testGetClientWhenClientDoesNotExist() {
        when(clientService.getClientInfo(anyString())).thenReturn(Optional.empty());

        ResponseEntity<ClientResponse> response = clientController.getClient(transactionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}