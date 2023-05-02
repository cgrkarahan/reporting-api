package com.rpdpymnt.reportingapi.service;

import com.rpdpymnt.reportingapi.dto.ClientResponse;

import java.util.Optional;

public interface ClientService {

    Optional<ClientResponse> getClientInfo(String transactionId );
}
