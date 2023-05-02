package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantIpn {
    private String transactionId;
    private String referenceNo;
    private int amount;
    private String currency;
    private int date;
    private String code;
    private String message;
    private String operation;
    private String type;
    private String status;
    private Object customData;
    private String chainId;
    private String paymentType;
    private String authTransactionId;
    private String token;
    private int convertedAmount;
    private String convertedCurrency;
    private String IPNUrl;
    private String ipnType;
}
