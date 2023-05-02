package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantTransaction {

    private String referenceNo;
    private int merchantId;
    private String status;
    private String channel;
    private Object customData;
    private String chainId;
    private String type;
    private int agentInfoId;
    private String operation;
    private String updated_at;
    private String created_at;
    private int id;
    private int fxTransactionId;
    private int acquirerTransactionId;
    private String code;
    private String message;
    private String transactionId;
    private Agent agent;
}
