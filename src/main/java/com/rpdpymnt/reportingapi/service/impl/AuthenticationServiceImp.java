package com.rpdpymnt.reportingapi.service.impl;


import com.rpdpymnt.reportingapi.config.JwtService;
import com.rpdpymnt.reportingapi.dto.AuthenticationRequest;
import com.rpdpymnt.reportingapi.dto.AuthenticationResponse;
import com.rpdpymnt.reportingapi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor

public class AuthenticationServiceImp implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = new User("adminUser", passwordEncoder.encode(request.getPassword()),
                new ArrayList<>());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
