package com.rpdpymnt.reportingapi.service.impl;


import com.rpdpymnt.reportingapi.dto.TokenResponse;

import com.rpdpymnt.reportingapi.service.TokenService;
import com.rpdpymnt.reportingapi.util.JwtCache;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTest {


    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(tokenService, "email", "test@gmail.com");
        ReflectionTestUtils.setField(tokenService, "isCacheEnable", "true");

    }

    @Test
    public void testGenerateToken() throws Exception {
        // Mock the external API call
        TokenResponse tokenResponse = TokenResponse.builder()
                .token("mock-token")
                .build();
        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(TokenResponse.class)))
                .willReturn(ResponseEntity.ok(tokenResponse));

        // Call the method under test
        String token = tokenService.generateToken();

        // Verify the results
        assertNotNull(token);
        assertEquals("mock-token", token);
    }

    @Test
    public void testGenerateTokenFromCache() throws Exception {



        // Mock the external API call
        TokenResponse tokenResponse = TokenResponse.builder()
                .token("mock-token")
                .build();

        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(TokenResponse.class)))
                .willReturn(ResponseEntity.ok(tokenResponse));


        String email = "test@gmail.com";
        String mockToken = "mock.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJ1c2VyIiwibWVyY2hhbnRJZCI6Mywic3Vi" +
                "TWVyY2hhbnRJZHMiOlszLDc0LDkzLDExOTEsMTI5NSwxMTEsMTM3LDEzOCwxNDIsMTQ1LDE0NiwxNTMsMzM0L" +
                "DE3NSwxODQsMjIwLDIyMSwyMjIsMjIzLDI5NCwzMjIsMzIzLDMyNywzMjksMzMwLDM0OSwzOTAsMzkxLDQ1N" +
                "Sw0NTYsNDc5LDQ4OCw1NjMsMTE0OSw1NzAsMTEzOCwxMTU2LDExNTcsMTE1OCwxMTc5LDEyOTMsMTI5NCwxMzA2LD" +
                "EzMDcsMTMyNCwxMzMxLDEzMzgsMTMzOSwxMzQxLDEzNDYsMTM0NywxMzQ4LDEzNDldLCJ0a" +
                "W1lc3RhbXAiOjE2ODMwMTkyNTJ9.data";
        JwtCache.addToken(email,mockToken);

        // Call the method under test
        String token = tokenService.generateToken();

        // Verify the results
        assertNotNull(token);
        assertEquals(tokenResponse.getToken(), token);

    }


    @Test
    public void testGenerateTokenTokenResponseNull() {
        // Set up test data
        String email = "user@example.com";
        String password = "password";
        String nullToken = null;

        // Configure mock objects
        given(restTemplate.postForEntity(anyString(), any(), eq(TokenResponse.class)))
                .willReturn(ResponseEntity.ok().body(TokenResponse.builder().token(nullToken).build()));

        // Call the method under test
        assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken();
        });
    }


}