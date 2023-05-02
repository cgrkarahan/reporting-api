package com.rpdpymnt.reportingapi.service.impl;

import com.rpdpymnt.reportingapi.dto.ClientRequest;
import com.rpdpymnt.reportingapi.dto.ClientResponse;
import com.rpdpymnt.reportingapi.exception.ServerResponseException;
import com.rpdpymnt.reportingapi.service.ClientService;
import com.rpdpymnt.reportingapi.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    private static String CLIENT_GET_URL = "https://sandbox-reporting.rpdpymnt.com/api/v3/client";

    /**
     * Retrieves client information from the reporting API using the specified transaction ID.
     *
     * @param transactionId the transaction ID for which to retrieve the client information
     * @return an Optional containing the ClientResponse object with the retrieved client information, or empty if the transaction ID was not found
     * @throws RuntimeException if there is an error while retrieving the transaction information
     */

    @Override
    public Optional<ClientResponse> getClientInfo(String transactionId) {

        log.info("getClient service  triggered");

        String authorization = tokenService.generateToken();

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId).build();

        try {

            log.info("Getting token from server ");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", authorization);

            ClientResponse clientResponse = restTemplate
                    .postForEntity(CLIENT_GET_URL,
                            new HttpEntity<>(clientRequest, headers), ClientResponse.class)
                    .getBody();

            return Optional.ofNullable(clientResponse);
        } catch (Exception e) {
            log.error("Error while getting transactions report: {}", e.getMessage());
            throw new ServerResponseException("Failed to get transaction.");
        }

    }
}
