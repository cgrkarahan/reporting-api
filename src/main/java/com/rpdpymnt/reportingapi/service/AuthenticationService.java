package com.rpdpymnt.reportingapi.service;

import com.rpdpymnt.reportingapi.dto.AuthenticationRequest;
import com.rpdpymnt.reportingapi.dto.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
