package com.rpdpymnt.reportingapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInfo {

    private Fx fx;

    private CustomerInfo customerInfo;

    private MerchantTransaction merchant;

    private Ipn ipn;

    private Transaction transaction;

    private Acquirer acquirer;

    private Boolean refundable;
}
