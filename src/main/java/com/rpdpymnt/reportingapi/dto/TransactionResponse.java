package com.rpdpymnt.reportingapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    private Fx fx;

    private CustomerInfo customerInfo;

    private MerchantTransaction merchant;

    private Transaction transaction;
}
