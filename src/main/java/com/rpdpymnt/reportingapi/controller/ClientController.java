package com.rpdpymnt.reportingapi.controller;

import com.rpdpymnt.reportingapi.dto.ClientResponse;
import com.rpdpymnt.reportingapi.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ClientController {

    private final ClientService clientService;


    @GetMapping("/clients/{transactionId}")
    ResponseEntity<ClientResponse> getClient(@PathVariable(name = "transactionId") String transactionId) {
        Optional<ClientResponse> optionalClientResponse = clientService.getClientInfo(transactionId);
        if (optionalClientResponse.isPresent()) {
            return ResponseEntity.ok(optionalClientResponse.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
