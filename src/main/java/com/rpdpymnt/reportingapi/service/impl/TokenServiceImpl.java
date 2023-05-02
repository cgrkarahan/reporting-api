package com.rpdpymnt.reportingapi.service.impl;

import com.rpdpymnt.reportingapi.dto.TokenRequest;
import com.rpdpymnt.reportingapi.dto.TokenResponse;
import com.rpdpymnt.reportingapi.exception.ServerResponseException;
import com.rpdpymnt.reportingapi.exception.TokenGenerationException;
import com.rpdpymnt.reportingapi.service.TokenService;
import com.rpdpymnt.reportingapi.util.JwtCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {


    private final RestTemplate restTemplate;

    @Value("${sandbox.client.email}")
    private String email;

    @Value("${sandbox.client.password}")
    private String password;

    //Enable/Disable jwt cache
    @Value("${jwt_cache}")
    private String isCacheEnable;

    private static String LOGING_URL = "https://sandbox-reporting.rpdpymnt.com/api/v3/merchant/user/login";


    public static final long JWT_TOKEN_VALIDITY = 1200;


    /**
     * Generates an authentication token for the given email and password.
     * First checks the cache to see if the token has already been generated and is still valid.
     * If not, it requests a new token from the external token provider API and adds the new token to the cache for future use.
     *
     * @return the authentication token
     */
    @Override
    public String generateToken() {


        log.info("Checking token is exist or not in the cache");
        String cachedToken = JwtCache.getToken(email);


        if (isCacheEnable.equals("true") && cachedToken != null && !isTokenExpired(email, cachedToken)) {

            log.info("token found in the cache");
            return cachedToken;
        }

        //If cache is empty, trigger externalToke

        TokenResponse tokenResponse = getExternalToken(email, password);

        if ( tokenResponse == null || tokenResponse.getToken() == null) {
            throw new TokenGenerationException("Token cannot be null!");
        }
        String token = tokenResponse.getToken();


        // Add token to cache
        JwtCache.addToken(email, token);

        return token;
    }

    /**

     Retrieves an external token by sending a token request to a server with the given email and password

     @param email the email associated with the user account
     @param password the password for the user account
     @return a TokenResponse object representing the retrieved token
     @throws ServerResponseException if there was an error retrieving the token
     */
    private TokenResponse getExternalToken(String email, String password) {
        TokenRequest tokenRequest = TokenRequest.builder()
                .email(email)
                .password(password)
                .build();

        try {

            log.info("Getting token from server ");
            HttpHeaders headers = new HttpHeaders();

            TokenResponse tokenResponse = restTemplate
                    .postForEntity(LOGING_URL,
                            new HttpEntity<>(tokenRequest, headers), TokenResponse.class)
                    .getBody();

            log.info("Getting token from server {}", tokenResponse);
            return tokenResponse;
        } catch (Exception e) {
            log.error("Getting token from server {}", e.getMessage());
            throw new ServerResponseException("Failed to retrieve data from API. Please try again.");
        }
    }

    /**

     Determines if a token is expired by decoding the token and checking the expiration timestamp
     @param email the email associated with the token
     @param token the JWT token to check
     @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String email, String token) {

        String[] parts = token.split("\\.");

        JSONObject payload = new JSONObject(decode(parts[1]));

        if (payload.getLong("timestamp") < (System.currentTimeMillis() / 1000)) {
            log.info("token expired and remove from cache");
            JwtCache.removeToken(email);
            return true;
        }
        return false;

    }

    private String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

}
